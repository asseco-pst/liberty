package io.github.asseco.pst.liberty.services

import com.ibm.websphere.application.ApplicationMBean
import com.ibm.websphere.filetransfer.FileServiceMXBean
import com.ibm.websphere.filetransfer.FileTransferMBean
import io.github.asseco.pst.liberty.enums.Bean
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

    JMXLiberty(Profile profile, boolean acceptInsecureCertificates = false) {
        super(profile, acceptInsecureCertificates)
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    void uninstallArtifact(Artifact artifact) throws ArtifactUninstallException {
        try {
            ObjectName mBeanObject = new ObjectName(Bean.FILE_TRANSFER.toString())
            if (!this.client.isRegistered(mBeanObject)) {
                throw new ArtifactUninstallException("Failed to uninstall artifact ${artifact.name} due to: File Transfer MBean is not registered on the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
            }
            FileTransferMBean fileTransferMBean = JMX.newMBeanProxy(this.client, mBeanObject, FileTransferMBean.class)
            fileTransferMBean.deleteFile("${LIBERTY_DEPLOYMENT_PATH}/${artifact.name}")

        } catch (ArtifactUninstallException exception) {
            throw exception
        } catch (Exception exception) {
            throw new ArtifactUninstallException("Failed to uninstall artifact ${artifact.name} due to: ${exception.getMessage()}")
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
    List<String> getInstalledArtifactsForPackage(Package pkg) throws PackageException {
        try {
            ObjectName mXBeanObject = new ObjectName(Bean.FILE_SERVICE.toString())
            if (!this.client.isRegistered(mXBeanObject)) {
                throw new PackageException("Failed to get installed artifacts for package ${pkg.name} due to: File Service MXBean is not registered on the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
            }
            FileServiceMXBean fileServiceMXBean = JMX.newMXBeanProxy(this.client, mXBeanObject, FileServiceMXBean.class)
            FileServiceMXBean.MetaData[] artifacts = fileServiceMXBean.getDirectoryEntries(LIBERTY_DEPLOYMENT_PATH, false, FileServiceMXBean.REQUEST_OPTIONS_ALL)

            return Arrays.stream(artifacts)
                    .filter({ FileServiceMXBean.MetaData artifact ->
                        artifact.fileName.contains(pkg.name)
                    })
                    .map({ FileServiceMXBean.MetaData filteredArtifact ->
                        (new File(filteredArtifact.fileName)).getName()
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


}
