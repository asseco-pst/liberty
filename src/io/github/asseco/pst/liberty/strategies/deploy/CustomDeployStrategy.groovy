package io.github.asseco.pst.liberty.strategies.deploy


import com.ibm.websphere.filetransfer.FileTransferMBean
import io.github.asseco.pst.liberty.builders.ConfigFileBuilder
import io.github.asseco.pst.liberty.enums.Bean
import io.github.asseco.pst.liberty.enums.Server
import io.github.asseco.pst.liberty.exceptions.ArtifactGenericException
import io.github.asseco.pst.liberty.exceptions.ArtifactInstallException
import io.github.asseco.pst.liberty.exceptions.ArtifactUninstallException
import io.github.asseco.pst.liberty.models.Artifact
import io.github.asseco.pst.liberty.models.Profile

import javax.management.JMX
import javax.management.ObjectName

/**
 * Deployer service responsible for installing artifacts on the artifacts folder in the Liberty profile
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
final class CustomDeployStrategy extends AbstractJMXDeployStrategy {
    CustomDeployStrategy(Profile profile, boolean acceptInsecureCertificates = false) {
        super(profile, acceptInsecureCertificates)
    }

    /** {@inheritDoc} */
    @Override
    void installArtifact(Artifact artifact) throws ArtifactInstallException {
        File configFile = null

        try {
            ObjectName mBeanObject = new ObjectName(Bean.FILE_TRANSFER.toString())
            if (!this.client.isRegistered(mBeanObject)) {
                throw new ArtifactInstallException("Failed to install artifact ${artifact.name} due to: File Transfer MBean is not registered on the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
            }

            Map<Server, String> serverInformation = this.getServerInformation()
            FileTransferMBean fileTransferMBean = JMX.newMBeanProxy(this.client, mBeanObject, FileTransferMBean.class)
            configFile = ConfigFileBuilder.buildArtifactConfigFile(serverInformation.get(Server.NAME), CUSTOM_DEPLOYMENT_PATH, artifact)

            fileTransferMBean.uploadFile(artifact.sourcePath.absolutePath, "${CUSTOM_DEPLOYMENT_PATH}/${artifact.name}", false)
            fileTransferMBean.uploadFile(configFile.absolutePath, "${CUSTOM_CONFIG_DROPINS_PATH}/${artifact.baseName}.xml", false)

        } catch (ArtifactInstallException exception) {
            throw exception
        } catch (Exception exception) {
            throw new ArtifactInstallException("Failed to install artifact ${artifact.name} due to: ${exception.getMessage()}")
        } finally {
            /*Deletes the configuration file to avoid garbage in the system*/
            configFile?.exists() ? configFile.delete() : null
        }
    }

    /** {@inheritDoc} */
    @Override
    void uninstallArtifact(Artifact artifact) throws ArtifactUninstallException {
        try {
            this.uninstallArtifact(artifact.name)
        } catch (ArtifactUninstallException exception) {
            throw exception
        } catch (Exception exception) {
            throw new ArtifactUninstallException("Failed to uninstall artifact ${artifact.name} due to: ${exception.getMessage()}")
        }
    }

    /** {@inheritDoc} */
    @Override
    void uninstallArtifact(String name) throws ArtifactUninstallException {
        try {
            ObjectName mBeanObject = new ObjectName(Bean.FILE_TRANSFER.toString())
            if (!this.client.isRegistered(mBeanObject)) {
                throw new ArtifactUninstallException("Failed to uninstall artifact ${name} due to: File Transfer MBean is not registered on the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
            }
            FileTransferMBean fileTransferMBean = JMX.newMBeanProxy(this.client, mBeanObject, FileTransferMBean.class)

            fileTransferMBean.deleteFile("${CUSTOM_DEPLOYMENT_PATH}/${name}")
            fileTransferMBean.deleteFile("${CUSTOM_CONFIG_DROPINS_PATH}/${getBasename(name)}.xml")

        } catch (ArtifactUninstallException exception) {
            throw exception
        } catch (Exception exception) {
            throw new ArtifactUninstallException("Failed to uninstall artifact ${name} due to: ${exception.getMessage()}")
        }
    }

    /** {@inheritDoc} */
    @Override
    void updateArtifactAutoStart(Artifact artifact) throws ArtifactGenericException {
        try {
            this.updateArtifactAutoStart(artifact.baseName, artifact.autoStart)
        } catch (ArtifactGenericException exception) {
            throw exception
        } catch (Exception exception) {
            throw new ArtifactGenericException("Failed to update artifact ${artifact.baseName} due to: ${exception.getMessage()}")
        }
    }

    /** {@inheritDoc} */
    @Override
    void updateArtifactAutoStart(String baseName, boolean enableAutoStart) throws ArtifactGenericException {
        File configFile = null

        try {
            ObjectName mBeanObject = new ObjectName(Bean.FILE_TRANSFER.toString())
            if (!this.client.isRegistered(mBeanObject)) {
                throw new ArtifactInstallException("Failed to update artifact ${baseName} due to: File Transfer MBean is not registered on the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
            }

            FileTransferMBean fileTransferMBean = JMX.newMBeanProxy(this.client, mBeanObject, FileTransferMBean.class)
            fileTransferMBean.downloadFile("${CUSTOM_CONFIG_DROPINS_PATH}/${baseName}.xml", "${baseName}.xml")

            configFile = new File("${baseName}.xml")
            configFile = ConfigFileBuilder.updateArtifactConfigFileAttribute(configFile, "application", "autoStart", "true")
            fileTransferMBean.uploadFile(configFile.absolutePath, "${CUSTOM_CONFIG_DROPINS_PATH}/${baseName}.xml", false)

        } catch (ArtifactGenericException exception) {
            throw exception
        } catch (Exception exception) {
            throw new ArtifactGenericException("Failed to update artifact ${baseName} due to: ${exception.getMessage()}")
        } finally {
            /*Deletes the configuration file to avoid garbage in the system*/
            configFile?.exists() ? configFile.delete() : null
        }
    }

    private static String getBasename(String filename) {
        if (filename.lastIndexOf(".") > 0) {
            return filename.substring(0, filename.lastIndexOf("."))
        }
        return filename
    }
}
