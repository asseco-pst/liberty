package io.github.asseco.pst.liberty.services

import io.github.asseco.pst.liberty.enums.Server
import io.github.asseco.pst.liberty.exceptions.*

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
     * Starts the artifact on the Liberty profile
     * @throws io.github.asseco.pst.liberty.exceptions.ArtifactStartException
     */
    IDeployService startArtifact() throws ArtifactStartException

    /**
     * Restarts the artifact on the Liberty profile
     * @throws io.github.asseco.pst.liberty.exceptions.ArtifactStopException
     */
    IDeployService restartArtifact() throws ArtifactStopException

    /**
     * Stops the artifact on the Liberty profile
     * @throws ArtifactStopException
     */
    IDeployService stopArtifact() throws ArtifactStopException

    /**
     * Checks if the artifact is installed on the Liberty profile
     * @return true if installed, false otherwise
     * @throws ArtifactGenericException
     */
    boolean isArtifactInstalled() throws ArtifactGenericException

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