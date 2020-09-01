package io.github.asseco.pst.liberty.services

import io.github.asseco.pst.liberty.enums.Server
import io.github.asseco.pst.liberty.exceptions.*
import io.github.asseco.pst.liberty.models.Artifact
import io.github.asseco.pst.liberty.models.Package
import io.github.asseco.pst.liberty.models.Profile
import io.github.asseco.pst.liberty.strategies.deploy.IDeployStrategy

/**
 * Deployment service class
 *
 * @date 31/08/2020
 * @version 1.0.0
 */
final class DeployService implements IDeployService {
    private final IDeployStrategy deployStrategy
    private final Profile profile
    private final Artifact artifact
    private final Package pkg

    DeployService(Profile profile, IDeployStrategy deployStrategy, Artifact artifact = null, Package pkg = null) {
        this.profile = profile
        this.deployStrategy = deployStrategy
        this.artifact = artifact
        this.pkg = pkg
    }

    @Override
    IDeployService installArtifact() throws ArtifactInstallException {
        if (this.artifact) {
            this.deployStrategy.installArtifact(this.artifact)
            return this
        }
        throw new ArtifactInstallException("No artifact was defined")
    }

    @Override
    IDeployService uninstallArtifact() throws ArtifactUninstallException {
        if (this.artifact) {
            this.deployStrategy.uninstallArtifact(this.artifact)
            return this
        }
        throw new ArtifactUninstallException("No artifact was defined")
    }

    @Override
    IDeployService startArtifact() throws ArtifactStartException {
        if (this.artifact) {
            this.deployStrategy.startArtifact(this.artifact)
            return this
        }
        throw new ArtifactStartException("No artifact was defined")
    }

    @Override
    IDeployService restartArtifact() throws ArtifactStopException {
        if (this.artifact) {
            this.deployStrategy.restartArtifact(this.artifact)
            return this
        }
        throw new ArtifactStopException("No artifact was defined")
    }

    @Override
    IDeployService stopArtifact() throws ArtifactStopException {
        if (this.artifact) {
            this.deployStrategy.stopArtifact(this.artifact)
            return this
        }
        throw new ArtifactStopException("No artifact was defined")
    }

    @Override
    boolean isArtifactInstalled() throws ArtifactGenericException {
        if (this.artifact) {
            return this.deployStrategy.isArtifactInstalled(this.artifact)
        }
        throw new ArtifactGenericException("No artifact was defined")
    }

    @Override
    List<String> getInstalledArtifactsForPackage() throws PackageException {
        if (this.pkg) {
            return this.deployStrategy.getInstalledArtifactsForPackage(this.pkg)
        }
        throw new PackageException("No package was defined")
    }

    @Override
    List<String> getInstalledArtifacts() throws PackageException {
        return this.deployStrategy.getInstalledArtifacts()
    }

    @Override
    Map<Server, String> getServerInformation() throws ServerInformationException {
        return this.deployStrategy.getServerInformation()
    }

    @Override
    IDeployService connect() throws ProfileConnectionException {
        this.deployStrategy.connect()
        return this
    }

    @Override
    IDeployService await(long miliseconds) {
        Thread.sleep(miliseconds)
        return this
    }

    @Override
    IDeployService disconnect() throws ProfileConnectionException {
        this.deployStrategy.disconnect()
        return this
    }
}
