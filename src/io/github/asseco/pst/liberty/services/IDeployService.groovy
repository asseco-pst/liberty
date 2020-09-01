package io.github.asseco.pst.liberty.services

import io.github.asseco.pst.liberty.enums.Server
import io.github.asseco.pst.liberty.enums.State
import io.github.asseco.pst.liberty.exceptions.*
import io.github.asseco.pst.liberty.models.Artifact

interface IDeployService {
    /**
     * Installs the artifact on the Liberty profile
     * @throws io.github.asseco.pst.liberty.exceptions.ArtifactInstallException
     */
    IDeployService installArtifact() throws ArtifactInstallException

    /**
     * Uninstalls the artifact from the Liberty profile
     * @throws io.github.asseco.pst.liberty.exceptions.ArtifactUninstallException
     */
    IDeployService uninstallArtifact() throws ArtifactUninstallException

    /**
     * Installs the artifact on the Liberty profile
     * @param artifact the artifact to install
     * @throws io.github.asseco.pst.liberty.exceptions.ArtifactInstallException
     */
    IDeployService installArtifact(Artifact artifact) throws ArtifactInstallException

    /**
     * Uninstalls the artifact from the Liberty profile
     * @param artifact the artifact to uninstall
     * @throws ArtifactUninstallException
     */
    IDeployService uninstallArtifact(Artifact artifact) throws ArtifactUninstallException

    /**
     * Uninstalls the artifact from the Liberty profile
     * @param name the name of the artifact to uninstall
     * @throws ArtifactUninstallException
     */
    IDeployService uninstallArtifact(String name) throws ArtifactUninstallException

    /**
     * Starts the artifact on the Liberty profile
     * @throws io.github.asseco.pst.liberty.exceptions.ArtifactStartException
     */
    IDeployService startArtifact() throws ArtifactStartException

    /**
     * Starts the artifact on the Liberty profile
     * @param name the artifact base name
     * @throws io.github.asseco.pst.liberty.exceptions.ArtifactStartException
     */
    IDeployService startArtifact(String baseName) throws ArtifactStartException

    /**
     * Restarts the artifact on the Liberty profile
     * @throws io.github.asseco.pst.liberty.exceptions.ArtifactStopException
     */
    IDeployService restartArtifact() throws ArtifactStopException

    /**
     * Restarts the artifact on the Liberty profile
     * @param name the artifact base name
     * @throws io.github.asseco.pst.liberty.exceptions.ArtifactStopException
     */
    IDeployService restartArtifact(String baseName) throws ArtifactStopException

    /**
     * Stops the artifact on the Liberty profile
     * @throws ArtifactStopException
     */
    IDeployService stopArtifact() throws ArtifactStopException

    /**
     * Stops the artifact on the Liberty profile
     * @param name the artifact base name
     * @throws ArtifactStopException
     */
    IDeployService stopArtifact(String baseName) throws ArtifactStopException

    /**
     * Updates the auto start configuration of the artifact
     * @throws ArtifactGenericException
     */
    IDeployService updateArtifactAutoStart() throws ArtifactGenericException

    /**
     * Updates the auto start configuration of the artifact
     * @param baseName the artifact base name
     * @param enableAutoStart if it should autostart or not
     * @throws ArtifactGenericException
     */
    IDeployService updateArtifactAutoStart(String baseName, boolean enableAutoStart) throws ArtifactGenericException

    /**
     * Checks if the artifact is installed on the Liberty profile
     * @return true if installed, false otherwise
     * @throws ArtifactGenericException
     */
    boolean isArtifactInstalled() throws ArtifactGenericException

    /**
     * Checks if the artifact is installed on the Liberty profile
     * @param baseName the artifact base name
     * @return
     * @throws ArtifactGenericException
     */
    boolean isArtifactInstalled(String baseName) throws ArtifactGenericException

    /**
     * Gets the artifact state from Liberty
     * @return the artifact state
     * @throws ArtifactGenericException
     */
    State getArtifactState() throws ArtifactGenericException

    /**
     * Gets the artifact state from Liberty
     * @param baseName the artifact base name
     * @return the artifact state
     * @throws ArtifactGenericException
     */
    State getArtifactState(String baseName) throws ArtifactGenericException

    /**
     * Gets a list of installed artifacts for the given package
     * @return The list of installed artifacts
     * @throws io.github.asseco.pst.liberty.exceptions.PackageException
     */
    List<String> getInstalledArtifactsForPackage() throws PackageException

    /**
     * Gets a list of installed artifacts
     * @return The list of installed artifacts
     * @throws io.github.asseco.pst.liberty.exceptions.PackageException
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
     * @throws io.github.asseco.pst.liberty.exceptions.ProfileConnectionException
     */
    IDeployService connect() throws ProfileConnectionException

    /**
     * Waits a certain number of miliseconds
     * @param miliseconds
     * @return
     */
    IDeployService await(long miliseconds)

    /**
     * Disconnects from the Liberty profile
     * @throws ProfileConnectionException
     */
    IDeployService disconnect() throws ProfileConnectionException
}