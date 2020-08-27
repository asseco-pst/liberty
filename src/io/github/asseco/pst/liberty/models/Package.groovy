package io.github.asseco.pst.liberty.models
/**
 * Class that stores the package information
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
class Package {
    private final String name

    Package(String name) {
        this.name = name
    }

    String getName() {
        return name
    }
}
