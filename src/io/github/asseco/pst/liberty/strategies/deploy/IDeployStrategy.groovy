package io.github.asseco.pst.liberty.strategies.deploy

import io.github.asseco.pst.liberty.enums.Server
import io.github.asseco.pst.liberty.enums.State
import io.github.asseco.pst.liberty.exceptions.*
import io.github.asseco.pst.liberty.models.Artifact
import io.github.asseco.pst.liberty.models.Package

/**
 * Interface that states the available operations for deployment.
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
interface IDeployStrategy {
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
     * Uninstalls the artifact from the Liberty profile
     * @param baseName The artifact name
     * @throws ArtifactUninstallException
     */
    void uninstallArtifact(String name) throws ArtifactUninstallException

    /**
     * Starts the artifact on the Liberty profile
     * @param artifact
     * @throws ArtifactStartException
     */
    void startArtifact(Artifact artifact) throws ArtifactStartException

    /**
     * Starts the artifact on the Liberty profile
     * @param baseName the artifact base name
     * @throws ArtifactStartException
     */
    void startArtifact(String baseName) throws ArtifactStartException

    /**
     * Restarts the artifact on the Liberty profile
     * @param artifact
     * @throws ArtifactStopException
     */
    void restartArtifact(Artifact artifact) throws ArtifactStopException

    /**
     * Restarts the artifact on the Liberty profile
     * @param baseName the artifact base name
     * @throws ArtifactStopException
     */
    void restartArtifact(String baseName) throws ArtifactStopException

    /**
     * Stops the artifact on the Liberty profile
     * @param artifact
     * @throws ArtifactStopException
     */
    void stopArtifact(Artifact artifact) throws ArtifactStopException

    /**
     * Stops the artifact on the Liberty profile
     * @param baseName the artifact base name
     * @throws ArtifactStopException
     */
    void stopArtifact(String baseName) throws ArtifactStopException

    /**
     * Updates the auto start configuration of the artifact
     * @param artifact the artifact to be updated
     * @throws ArtifactGenericException
     */
    void updateArtifactAutoStart(Artifact artifact) throws ArtifactGenericException

    /**
     * Updates the auto start configuration of the artifact
     * @param baseName the artifact base name
     * @param enableAutoStart if it should autostart or not
     * @throws ArtifactGenericException
     */
    void updateArtifactAutoStart(String baseName, boolean enableAutoStart) throws ArtifactGenericException

    /**
     * Checks if the artifact is installed on the Liberty profile
     * @param artifact
     * @return
     * @throws ArtifactGenericException
     */
    boolean isArtifactInstalled(Artifact artifact) throws ArtifactGenericException

    /**
     * Checks if the artifact is installed on the Liberty profile
     * @param baseName the artifact base name
     * @return
     * @throws ArtifactGenericException
     */
    boolean isArtifactInstalled(String baseName) throws ArtifactGenericException

    /**
     * Gets the artifact state from Liberty
     * @param artifact
     * @return the artifact state
     * @throws ArtifactGenericException
     */
    State getArtifactState(Artifact artifact) throws ArtifactGenericException

    /**
     * Gets the artifact state from Liberty
     * @param baseName the artifact base name
     * @return the artifact state
     * @throws ArtifactGenericException
     */
    State getArtifactState(String baseName) throws ArtifactGenericException

    /**
     * Gets a list of installed artifacts for the given package
     * @param pkg The package
     * @return The list of installed artifacts
     * @throws PackageException
     */
    List<String> getInstalledArtifactsForPackage(Package pkg) throws PackageException

    /**
     * Gets a list of installed artifacts
     * @return The list of installed artifacts
     * @throws PackageException
     */
    List<String> getInstalledArtifacts() throws PackageException

    /**
     * Gets a list of server properties
     * @return The list of server properties
     * @throws ServerInformationException
     */
    Map<Server, String> getServerInformation() throws ServerInformationException

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
