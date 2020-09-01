package io.github.asseco.pst.liberty.strategies.deploy

import com.ibm.websphere.application.ApplicationMBean
import com.ibm.websphere.filetransfer.FileServiceMXBean
import com.ibm.websphere.kernel.server.ServerInfoMBean
import io.github.asseco.pst.liberty.enums.Bean
import io.github.asseco.pst.liberty.enums.Server
import io.github.asseco.pst.liberty.exceptions.*
import io.github.asseco.pst.liberty.models.Artifact
import io.github.asseco.pst.liberty.models.Package
import io.github.asseco.pst.liberty.models.Profile

import javax.management.JMX
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import javax.management.remote.JMXConnector
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import javax.net.ssl.*
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

/**
 * Abstract class to setup default stuff
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
abstract class AbstractJMXDeployStrategy implements IDeployStrategy {
    private static String JMX_CONNECTION_PROTOCOL = 'service:jmx:rest://'
    private static String JMX_CONNECTION_ENDPOINT = 'IBMJMXConnectorREST'
    private static String JMX_REMOTE_PROTOCOL_PROVIDER_PKGS = 'jmx.remote.protocol.provider.pkgs'
    private static String JMX_PROTOCOL_PROVIDER = 'com.ibm.ws.jmx.connector.client'
    private static HashMap<String, Object> JMX_PROTOCOL_PROVIDER_OPTIONS = [
            'com.ibm.ws.jmx.connector.client.disableURLHostnameVerification': true
    ]

    protected static String DROPINS_DEPLOYMENT_PATH = '${server.output.dir}/dropins'
    protected static String CUSTOM_DEPLOYMENT_PATH = '${server.output.dir}/artifacts'
    protected static String CUSTOM_CONFIG_DROPINS_PATH = '${server.output.dir}/configDropins/defaults'

    protected final Profile profile
    protected JMXConnector connector
    protected MBeanServerConnection client

    AbstractJMXDeployStrategy(Profile profile, boolean acceptInsecureCertificates = false) {
        this.profile = profile

        if (acceptInsecureCertificates) {
            disableSSLVerification()
        }
    }

    /** {@inheritDoc} */
    @Override
    void startArtifact(Artifact artifact) throws ArtifactStartException {
        try {
            ObjectName mBeanObject = new ObjectName("${Bean.APPLICATION.toString()}${artifact.baseName}")
            if (!this.client.isRegistered(mBeanObject)) {
                throw new ArtifactStartException("Failed to start artifact ${artifact.name} due to: Artifact is not registered on the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
            }
            ApplicationMBean applicationMBean = JMX.newMBeanProxy(this.client, mBeanObject, ApplicationMBean.class)
            applicationMBean.start()

        } catch (ArtifactStartException exception) {
            throw exception
        } catch (Exception exception) {
            throw new ArtifactStartException("Failed to start artifact ${artifact.name} due to: ${exception.getMessage()}")
        }
    }

    /** {@inheritDoc} */
    @Override
    void restartArtifact(Artifact artifact) throws ArtifactStopException {
        try {
            ObjectName mBeanObject = new ObjectName("${Bean.APPLICATION.toString()}${artifact.baseName}")
            if (!this.client.isRegistered(mBeanObject)) {
                throw new ArtifactRestartException("Failed to restart artifact ${artifact.name} due to: Artifact is not registered on the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
            }
            ApplicationMBean applicationMBean = JMX.newMBeanProxy(this.client, mBeanObject, ApplicationMBean.class)
            applicationMBean.restart()

        } catch (ArtifactRestartException exception) {
            throw exception
        } catch (Exception exception) {
            throw new ArtifactRestartException("Failed to restart artifact ${artifact.name} due to: ${exception.getMessage()}")
        }
    }

    /** {@inheritDoc} */
    @Override
    void stopArtifact(Artifact artifact) throws ArtifactStopException {
        try {
            ObjectName mBeanObject = new ObjectName("${Bean.APPLICATION.toString()}${artifact.baseName}")
            if (!this.client.isRegistered(mBeanObject)) {
                throw new ArtifactStopException("Failed to stop artifact ${artifact.name} due to: Artifact is not registered on the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
            }
            ApplicationMBean applicationMBean = JMX.newMBeanProxy(this.client, mBeanObject, ApplicationMBean.class)
            applicationMBean.stop()

        } catch (ArtifactStopException exception) {
            throw exception
        } catch (Exception exception) {
            throw new ArtifactStopException("Failed to stop artifact ${artifact.name} due to: ${exception.getMessage()}")
        }
    }

    /** {@inheritDoc} */
    @Override
    boolean isArtifactInstalled(Artifact artifact) throws ArtifactGenericException {
        try {
            ObjectName mBeanObject = new ObjectName("${Bean.APPLICATION.toString()}${artifact.baseName}")
            return this.client.isRegistered(mBeanObject)
        } catch (Exception exception) {
            throw new ArtifactGenericException("Failed to obtain information about artifact ${artifact.name} due to: ${exception.getMessage()}")
        }
    }

    /** {@inheritDoc} */
    @Override
    List<String> getInstalledArtifacts() throws PackageException {
        try {
            ObjectName mXBeanObject = new ObjectName(Bean.FILE_SERVICE.toString())
            if (!this.client.isRegistered(mXBeanObject)) {
                throw new PackageException("Failed to get installed artifacts due to: File Service MXBean is not registered on the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
            }
            FileServiceMXBean fileServiceMXBean = JMX.newMXBeanProxy(this.client, mXBeanObject, FileServiceMXBean.class)
            List artifacts = new ArrayList()

            FileServiceMXBean.MetaData[] dropinArtifacts = fileServiceMXBean.getDirectoryEntries(DROPINS_DEPLOYMENT_PATH, false, FileServiceMXBean.REQUEST_OPTIONS_ALL)
            artifacts.addAll(dropinArtifacts)

            FileServiceMXBean.MetaData[] customArtifacts = fileServiceMXBean.getDirectoryEntries(CUSTOM_DEPLOYMENT_PATH, false, FileServiceMXBean.REQUEST_OPTIONS_ALL)
            artifacts.addAll(customArtifacts)

            return artifacts.stream()
                    .map({ FileServiceMXBean.MetaData filteredArtifact ->
                        (new File(filteredArtifact.fileName)).getName()
                    })
                    .collect()

        } catch (PackageException exception) {
            throw exception
        } catch (Exception exception) {
            throw new PackageException("Failed to list installed artifacts due to: ${exception.getMessage()}")
        }
    }

    /** {@inheritDoc} */
    @Override
    List<String> getInstalledArtifactsForPackage(Package pkg) throws PackageException {
        try {
            List<String> artifacts = this.getInstalledArtifacts()
            return artifacts.stream()
                    .filter({ String artifact ->
                        artifact.contains(pkg.name)
                    })
                    .collect()
        } catch (PackageException exception) {
            throw exception
        } catch (Exception exception) {
            throw new PackageException("Failed to list installed artifacts for package ${pkg.name} due to: ${exception.getMessage()}")
        }
    }

    /** {@inheritDoc} */
    @Override
    Map<Server, String> getServerInformation() throws ServerInformationException {
        try {
            ObjectName mBeanObject = new ObjectName("${Bean.SERVER_INFORMATION.toString()}")
            if (!this.client.isRegistered(mBeanObject)) {
                throw new ServerInformationException("Failed to obtain server information due to: ServerInfoMBean is not registered on the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
            }
            ServerInfoMBean serverInfoMBean = JMX.newMBeanProxy(this.client, mBeanObject, ServerInfoMBean.class)
            HashMap<Server, String> serverInformation = new HashMap<Server, String>()

            /*Fills out server information*/
            serverInformation.put(Server.NAME, serverInfoMBean.name)
            serverInformation.put(Server.HOSTNAME, serverInfoMBean.defaultHostname)
            serverInformation.put(Server.INSTALL_DIR, serverInfoMBean.installDirectory)
            serverInformation.put(Server.JAVA_RUNTIME_VERSION, serverInfoMBean.javaRuntimeVersion)
            serverInformation.put(Server.JAVA_SPEC_VERSION, serverInfoMBean.javaSpecVersion)
            serverInformation.put(Server.LIBERTY_VERSION, serverInfoMBean.libertyVersion)
            serverInformation.put(Server.USER_DIR, serverInfoMBean.userDirectory)
            /*End*/

            return serverInformation
        } catch (ServerInformationException exception) {
            throw exception
        } catch (Exception exception) {
            throw new ServerInformationException("Failed to fetch information due to: ${exception.getMessage()}")
        }
    }

    /** {@inheritDoc} */
    @Override
    void connect() throws ProfileConnectionException {
        HashMap<String, Object> environment = new HashMap<String, Object>()
        environment.put(JMX_REMOTE_PROTOCOL_PROVIDER_PKGS, JMX_PROTOCOL_PROVIDER)
        environment.put(JMXConnector.CREDENTIALS, new String[]{this.profile.username, this.profile.password})

        JMX_PROTOCOL_PROVIDER_OPTIONS.forEach({ String key, Object value ->
            environment.put(key, value)
        })

        JMXServiceURL serviceURL = new JMXServiceURL("${JMX_CONNECTION_PROTOCOL}${profile.hostname}:${profile.port}/${JMX_CONNECTION_ENDPOINT}")
        this.connector = JMXConnectorFactory.newJMXConnector(serviceURL, environment)

        this.connector.connect()

        this.client = this.connector.getMBeanServerConnection()
        if (this.client == null) {
            throw new ProfileConnectionException("Failed to connect to the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
        }
    }

    /** {@inheritDoc} */
    @Override
    void disconnect() throws ProfileConnectionException {
        if (this.connector) {
            try {
                connector.close()
            } catch (Exception exception) {
                exception.printStackTrace()
            } finally {
                connector = null
            }
        }
    }

    private static void disableSSLVerification() {
        X509TrustManager nullTrustManager = new X509TrustManager() {
            @Override
            void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

            @Override
            void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

            @Override
            X509Certificate[] getAcceptedIssuers() {
                return null
            }
        }

        HostnameVerifier nullHostnameVerifier = new HostnameVerifier() {
            @Override
            boolean verify(String s, SSLSession sslSession) {
                return true
            }
        }
        SSLContext sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, [nullTrustManager] as TrustManager[], null)
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory())
        HttpsURLConnection.setDefaultHostnameVerifier(nullHostnameVerifier)
    }
}
