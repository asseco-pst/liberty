package io.github.asseco.pst.liberty.enums

/**
 * Supported managed beans for Liberty
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
enum Bean {
    APPLICATION( "WebSphere:service=com.ibm.websphere.application.ApplicationMBean,name="),
    SERVER_INFORMATION( "WebSphere:feature=kernel,name=ServerInfo"),
    FILE_TRANSFER("WebSphere:feature=restConnector,type=FileTransfer,name=FileTransfer"),
    FILE_SERVICE("WebSphere:feature=restConnector,type=FileService,name=FileService")

    private String descriptor;

    Bean(String descriptor) {
        this.descriptor = descriptor
    }

    @Override
    String toString() {
        return descriptor
    }

    String getDescriptor() {
        return descriptor
    }
}
