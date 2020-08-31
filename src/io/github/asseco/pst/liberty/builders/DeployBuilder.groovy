package io.github.asseco.pst.liberty.builders

import io.github.asseco.pst.liberty.enums.Strategy
import io.github.asseco.pst.liberty.exceptions.BuilderException
import io.github.asseco.pst.liberty.models.Artifact
import io.github.asseco.pst.liberty.models.Package
import io.github.asseco.pst.liberty.models.Profile
import io.github.asseco.pst.liberty.services.DeployService
import io.github.asseco.pst.liberty.services.IDeployService
import io.github.asseco.pst.liberty.strategies.deploy.IDeployStrategy
import io.github.asseco.pst.liberty.strategies.deploy.StrategyFactory

/**
 * Builder class for building the necessary objects for the deployment service
 *
 * @date 31/08/2020
 * @version 1.0.0
 */
class DeployBuilder {
    protected Profile profile
    protected IDeployStrategy deployStrategy
    protected Artifact artifact
    protected Package pkg

    DeployBuilder setProfileDetails(String hostname, Integer port, String username, String password) {
        this.profile = new Profile(hostname, port, username, password)
        return this
    }

    DeployBuilder setDeployStrategy(Strategy strategy, boolean acceptInsecureCertificates = false) {
        if (profile) {
            this.deployStrategy = (new StrategyFactory(this.profile)).getStrategy(strategy, acceptInsecureCertificates)
            return this
        }
        throw new BuilderException("You need to set the profile first!")
    }

    DeployBuilder setArtifactPath(String sourcePath) {
        this.artifact = new Artifact(sourcePath)
        return this
    }

    DeployBuilder setPackageName(String name) {
        this.pkg = new Package(name)
        return this
    }

    IDeployService build() {
        return new DeployService(profile, deployStrategy, artifact, pkg)
    }
}
