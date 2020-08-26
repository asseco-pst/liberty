package io.github.asseco.pst.liberty.enums

/**
 * Defines the artifact type
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
enum Type {
    EAR('ear'),
    WAR('war'),
    JAR('jar'),
    RAR('rar'),
    EMPTY('')

    private final String type

    Type(String type) {
        this.type = type
    }

    @Override
    String toString() {
        return type
    }

    String getType() {
        return type
    }
}
