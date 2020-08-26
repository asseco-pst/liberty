package io.github.asseco.pst.liberty.services

import io.github.asseco.pst.liberty.exceptions.ArtifactGenericException
import io.github.asseco.pst.liberty.exceptions.ArtifactInstallException
import io.github.asseco.pst.liberty.exceptions.ArtifactStartException
import io.github.asseco.pst.liberty.exceptions.ArtifactStopException
import io.github.asseco.pst.liberty.exceptions.ArtifactUninstallException
import io.github.asseco.pst.liberty.exceptions.PackageException
import io.github.asseco.pst.liberty.exceptions.ProfileConnectionException
import io.github.asseco.pst.liberty.models.Artifact

/**
 * Interface that states the available operations for deployment.
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
interface IDeployerService {
    /**
     * Installs the artifact on the Liberty profile
     * @param artifact The artifact to install
     * @throws ArtifactInstallException
     */
    void installArtifact(Artifact artifact) throws ArtifactInstallException

    /**
     * Uninstalls the artifact from the Liberty profile
     * @param artifact The artifact to uninstall
     * @throws ArtifactUninstallException
     */
    void uninstallArtifact(Artifact artifact) throws ArtifactUninstallException

    /**
     * Starts the artifact on the Liberty profile
     * @param artifact
     * @throws ArtifactStartException
     */
    void startArtifact(Artifact artifact) throws ArtifactStartException

    /**
     * Stops the artifact on the Liberty profile
     * @param artifact
     * @throws ArtifactStopException
     */
    void stopArtifact(Artifact artifact) throws ArtifactStopException

    /**
     * Checks if the artifact is installed on the Liberty profile
     * @param artifact
     * @return
     * @throws ArtifactGenericException
     */
    boolean isArtifactInstalled(Artifact artifact) throws ArtifactGenericException

    /**
     * Gets a list of installed artifacts for the given package
     * @param pkg The package
     * @return The list of installed artifacts 
     * @throws PackageException
     */
    List<String> getInstalledArtifactsForPackage(Package pkg) throws PackageException

    /**
     * Connects to the Liberty profile
     * @throws ProfileConnectionException
     */
    void connect() throws ProfileConnectionException

    /**
     * Disconnects from the Liberty profile
     * @throws ProfileConnectionException
     */
    void disconnect() throws ProfileConnectionException
}
