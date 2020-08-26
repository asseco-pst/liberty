package io.github.asseco.pst.liberty.services

import io.github.asseco.pst.liberty.models.Profile

/**
 * Abstract class to setup default stuff
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
abstract class AbstractDeployer implements IDeployerService {
    protected static String LIBERTY_DEPLOYMENT_PATH = '${server.output.dir}/dropins'
    protected final Profile profile

    AbstractDeployer(Profile profile) {
        this.profile = profile
    }
}
