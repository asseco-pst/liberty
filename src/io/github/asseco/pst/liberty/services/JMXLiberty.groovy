package io.github.asseco.pst.liberty.services

import com.ibm.websphere.filetransfer.FileTransferMBean
import io.github.asseco.pst.liberty.enums.Bean
import io.github.asseco.pst.liberty.exceptions.*
import io.github.asseco.pst.liberty.models.Artifact
import io.github.asseco.pst.liberty.models.Profile

import javax.management.JMX
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import javax.management.remote.JMXConnector
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import java.util.stream.Stream

/**
 * Deployer service responsible for the interaction with the Liberty profile
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
class JMXLiberty extends AbstractDeployer {
    private static String JMX_CONNECTION_PROTOCOL = 'service:jmx:rest://'
    private static String JMX_CONNECTION_ENDPOINT = 'IBMJMXConnectorREST'
    private static String JMX_REMOTE_PROTOCOL_PROVIDER_PKGS = 'jmx.remote.protocol.provider.pkgs'
    private static String JMX_PROTOCOL_PROVIDER = 'com.ibm.ws.jmx.connector.client'
    private static HashMap<String, Object> JMX_PROTOCOL_PROVIDER_OPTIONS = [
            'com.ibm.ws.jmx.connector.client.disableURLHostnameVerification': true
    ]

    private JMXConnector connector
    private MBeanServerConnection client

    JMXLiberty(Profile profile) {
        super(profile)
    }

    @Override
    void installArtifact(Artifact artifact) throws ArtifactInstallException {
        try {
            ObjectName mBeanObject = new ObjectName(Bean.FILE_TRANSFER.toString())
            if (!this.client.isRegistered(mBeanObject)) {
                throw new ArtifactInstallException("Failed to install artifact ${artifact.name} due to: File Transfer MBean is not registered on the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
            }

            FileTransferMBean fileTransferMBean = JMX.newMBeanProxy(this.client, mBeanObject, FileTransferMBean.class)
            fileTransferMBean.uploadFile(artifact.sourcePath.absolutePath, "${LIBERTY_DEPLOYMENT_PATH}/${artifact.name}", false)

        } catch (ArtifactInstallException exception) {
            throw exception
        } catch (Exception exception) {
            throw new ArtifactInstallException("Failed to install artifact ${artifact.name} due to: ${exception.getMessage()}")
        }
    }

    @Override
    void uninstallArtifact(Artifact artifact) throws ArtifactUninstallException {

    }

    @Override
    void startArtifact(Artifact artifact) throws ArtifactStartException {

    }

    @Override
    void stopArtifact(Artifact artifact) throws ArtifactStopException {

    }

    @Override
    boolean isArtifactInstalled(Artifact artifact) throws ArtifactGenericException {
        return false
    }

    @Override
    List<String> getInstalledArtifactsForPackage(Package pkg) throws PackageException {
        return null
    }

    @Override
    void connect() throws ProfileConnectionException {
        HashMap<String, Object> environment = new HashMap<String, Object>()
        environment.put(JMX_REMOTE_PROTOCOL_PROVIDER_PKGS, JMX_PROTOCOL_PROVIDER)
        environment.put(JMXConnector.CREDENTIALS, new String[]{this.profile.username, this.profile.password})

        Stream combinedEnvironment = Stream.concat(environment.entrySet().stream(), JMX_PROTOCOL_PROVIDER_OPTIONS.entrySet().stream())
        JMXServiceURL serviceURL = new JMXServiceURL("${JMX_CONNECTION_PROTOCOL}${profile.hostname}:${profile.port}/${JMX_CONNECTION_ENDPOINT}")

        this.connector = JMXConnectorFactory.newJMXConnector(serviceURL, combinedEnvironment.collect() as Map<String, Object>)
        this.connector.connect()

        this.client = this.connector.getMBeanServerConnection()
        if (this.client == null) {
            throw new ProfileConnectionException("Failed to connect to the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
        }
    }

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
}
