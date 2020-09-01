package io.github.asseco.pst.liberty.strategies.deploy


import com.ibm.websphere.filetransfer.FileTransferMBean
import io.github.asseco.pst.liberty.builders.ConfigFileBuilder
import io.github.asseco.pst.liberty.enums.Bean
import io.github.asseco.pst.liberty.enums.Server
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
            ObjectName mBeanObject = new ObjectName(Bean.FILE_TRANSFER.toString())
            if (!this.client.isRegistered(mBeanObject)) {
                throw new ArtifactUninstallException("Failed to uninstall artifact ${artifact.name} due to: File Transfer MBean is not registered on the Liberty Profile on hostname ${this.profile.hostname} and port ${this.profile.port}")
            }
            FileTransferMBean fileTransferMBean = JMX.newMBeanProxy(this.client, mBeanObject, FileTransferMBean.class)

            fileTransferMBean.deleteFile("${CUSTOM_DEPLOYMENT_PATH}/${artifact.name}")
            fileTransferMBean.deleteFile("${CUSTOM_CONFIG_DROPINS_PATH}/${artifact.baseName}.xml")

        } catch (ArtifactUninstallException exception) {
            throw exception
        } catch (Exception exception) {
            throw new ArtifactUninstallException("Failed to uninstall artifact ${artifact.name} due to: ${exception.getMessage()}")
        }
    }
}
