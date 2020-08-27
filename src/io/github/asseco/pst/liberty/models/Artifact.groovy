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
    private final File sourcePath
    private final String name
    private final String baseName

    Artifact(String sourcePath) {
        this.sourcePath = new File(sourcePath)
        this.name = this.sourcePath.getAbsoluteFile().getName()
        this.baseName = getBasename(this.name)
        this.type = Type.valueOf(getExtension(this.name).toUpperCase())
    }

    private static String getExtension(String filename) {
        if (filename.lastIndexOf(".") > 0) {
            return filename.substring(filename.lastIndexOf(".") + 1)
        }
        return ''
    }

    private static String getBasename(String filename) {
        if (filename.lastIndexOf(".") > 0) {
            return filename.substring(0, filename.lastIndexOf("."))
        }
        return filename
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

    String getBaseName() {
        return baseName
    }
}
