package io.github.asseco.pst.liberty.enums

/**
 * Defines the application state
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
enum State {
    STOPPED('STOPPED'),
    STARTING('STARTING'),
    STARTED('STARTED'),
    PARTIALY_STARTED('PARTIALY_STARTED'),
    INSTALLED('INSTALLED'),

    private final String state

    State(String state) {
        this.state = state
    }

    @Override
    String toString() {
        return state
    }

    String getState() {
        return state
    }
}
