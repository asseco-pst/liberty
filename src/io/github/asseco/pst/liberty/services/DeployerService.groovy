package io.github.asseco.pst.liberty.services

import io.github.asseco.pst.liberty.enums.Strategy
import io.github.asseco.pst.liberty.exceptions.*
import io.github.asseco.pst.liberty.models.Artifact
import io.github.asseco.pst.liberty.models.Package
import io.github.asseco.pst.liberty.models.Profile

import javax.naming.OperationNotSupportedException

/**
 * Generic deployer service that implements the given deployment strategy.
 *
 * @date 27/08/2020
 * @version 1.0.0
 */
class DeployerService implements IDeployerService {
    private final IDeployerService deployerService

    DeployerService(Profile profile, Strategy strategy, boolean acceptInsecureCertificates = false) {
        this.deployerService = instantiateStrategy(profile, strategy, acceptInsecureCertificates)
    }

    /** {@inheritDoc} */
    @Override
    void installArtifact(Artifact artifact) throws ArtifactInstallException {
        this.deployerService.installArtifact(artifact)
    }

    /** {@inheritDoc} */
    @Override
    void uninstallArtifact(Artifact artifact) throws ArtifactUninstallException {
        this.deployerService.uninstallArtifact(artifact)
    }

    /** {@inheritDoc} */
    @Override
    void startArtifact(Artifact artifact) throws ArtifactStartException {
        this.deployerService.startArtifact(artifact)
    }

    /** {@inheritDoc} */
    @Override
    void restartArtifact(Artifact artifact) throws ArtifactStopException {
        this.deployerService.restartArtifact(artifact)
    }

    /** {@inheritDoc} */
    @Override
    void stopArtifact(Artifact artifact) throws ArtifactStopException {
        this.deployerService.stopArtifact(artifact)
    }

    /** {@inheritDoc} */
    @Override
    boolean isArtifactInstalled(Artifact artifact) throws ArtifactGenericException {
        this.deployerService.isArtifactInstalled(artifact)
    }

    /** {@inheritDoc} */
    @Override
    List<String> getInstalledArtifactsForPackage(Package pkg) throws PackageException {
        this.deployerService.getInstalledArtifactsForPackage(pkg)
    }

    /** {@inheritDoc} */
    @Override
    void connect() throws ProfileConnectionException {
        this.deployerService.connect()
    }

    /** {@inheritDoc} */
    @Override
    void disconnect() throws ProfileConnectionException {
        this.deployerService.disconnect()
    }

    private static IDeployerService instantiateStrategy(Profile profile, Strategy strategy, boolean acceptInsecureCertificates = false) {
        switch (strategy) {
            case Strategy.JMX:
                return new JMXLiberty(profile, acceptInsecureCertificates)
            default:
                throw new OperationNotSupportedException("This strategy is not supported yet")
        }
    }
}
