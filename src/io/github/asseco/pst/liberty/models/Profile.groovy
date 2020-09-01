package io.github.asseco.pst.liberty.models

/**
 * Class that stores the profile information
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
class Profile {
    private final String hostname
    private final Integer port
    private final String username
    private final String password

    Profile(String hostname, Integer port, String username, String password) {
        this.hostname = hostname
        this.port = port
        this.username = username
        this.password = password
    }

    String getHostname() {
        return this.hostname
    }

    String getPort() {
        return this.port
    }

    String getUsername() {
        return this.username
    }

    String getPassword() {
        return this.password
    }
}
