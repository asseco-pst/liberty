package io.github.asseco.pst.liberty.strategies.deploy

import io.github.asseco.pst.liberty.enums.Strategy
import io.github.asseco.pst.liberty.exceptions.StrategyNotSupportedException
import io.github.asseco.pst.liberty.models.Profile

/**
 * Factory class for obtaining the strategies
 *
 * @date 31/08/2020
 * @version 1.0.0
 */
final class StrategyFactory {
    private final Profile profile

    StrategyFactory(Profile profile) {
        this.profile = profile
    }

    IDeployStrategy getStrategy(Strategy strategy, boolean acceptInsecureCertificates = false) {
        switch (strategy) {
            case Strategy.DROPINS:
                return new DropinsDeployStrategy(this.profile, acceptInsecureCertificates)
            case Strategy.CUSTOM:
                return new CustomDeployStrategy(this.profile, acceptInsecureCertificates)
            default:
                throw new StrategyNotSupportedException("The provided strategy is still not supported")
        }
    }
}
