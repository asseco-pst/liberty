package io.github.asseco.pst.liberty.strategies.deploy


import com.ibm.websphere.filetransfer.FileTransferMBean
import io.github.asseco.pst.liberty.enums.Bean
import io.github.asseco.pst.liberty.exceptions.ArtifactGenericException
import io.github.asseco.pst.liberty.exceptions.ArtifactInstallException
import io.github.asseco.pst.liberty.exceptions.ArtifactUninstallException
import io.github.asseco.pst.liberty.models.Artifact
import io.github.asseco.pst.liberty.models.Profile

import javax.management.JMX
import javax.management.ObjectName

/**
 * Deployer service responsible for installing artifacts on the dropins folder in the Liberty profile
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
final class DropinsDeployStrategy extends AbstractJMXDeployStrategy {
    DropinsDeployStrategy(Profile profile, boolean acceptInsecureCertificates = false) {
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
            fileTransferMBean.uploadFile(artifact.sourcePath.absolutePath, "${DROPINS_DEPLOYMENT_PATH}/${artifact.name}", false)

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
            fileTransferMBean.deleteFile("${DROPINS_DEPLOYMENT_PATH}/${name}")

        } catch (ArtifactUninstallException exception) {
            throw exception
        } catch (Exception exception) {
            throw new ArtifactUninstallException("Failed to uninstall artifact ${name} due to: ${exception.getMessage()}")
        }
    }

    @Override
    void updateArtifactAutoStart(Artifact artifact) throws ArtifactGenericException {
        /* Since we are using the dropins strategy, this does nothing here */
    }

    @Override
    void updateArtifactAutoStart(String baseName, boolean enableAutoStart) throws ArtifactGenericException {
        /* Since we are using the dropins strategy, this does nothing here */
    }
}
