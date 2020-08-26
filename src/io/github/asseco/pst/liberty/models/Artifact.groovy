package io.github.asseco.pst.liberty.models

import io.github.asseco.pst.liberty.enums.Type

/**
 * Class that stores the artifact information
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
class Artifact {
    private Type type
    private final Package pkg
    private final File sourcePath
    private String name

    Artifact(File sourcePath, Package pkg) {
        this.sourcePath = sourcePath
        this.pkg = pkg
        this.name = sourcePath.getAbsoluteFile().getName()
        this.type = getExtension(this.name) as Type
    }

    private static String getExtension(String name) {
        if (name.lastIndexOf(".") > 0) {
            return name.substring(name.lastIndexOf(".") + 1)
        }
        return ''
    }

    Type getType() {
        return type
    }

    File getSourcePath() {
        return sourcePath
    }

    String getName() {
        return name
    }

    Package getPackage() {
        return pkg
    }
}
