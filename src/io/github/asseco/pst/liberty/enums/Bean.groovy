package io.github.asseco.pst.liberty.enums

/**
 * Supported managed beans for Liberty
 *
 * @date 26/08/2020
 * @version 1.0.0
 */
enum Bean {
    APPLICATION("WebSphere:service=com.ibm.websphere.application.ApplicationMBean,name="),
    FILE_TRANSFER('WebSphere:feature=restConnector,type=FileTransfer,name=FileTransfer')

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
