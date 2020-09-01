package io.github.asseco.pst.liberty.exceptions

/**
 * Classe used to throw UnsupportedClass exceptions.
 *
 * @date 29/07/2020
 * @version 1.0.0
 */
class StrategyNotSupportedException extends Exception {
    StrategyNotSupportedException(String message) {
        super(message)
    }
}
