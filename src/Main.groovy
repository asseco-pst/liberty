import io.github.asseco.pst.liberty.builders.DeployBuilder
import io.github.asseco.pst.liberty.enums.Strategy
import io.github.asseco.pst.liberty.services.IDeployService

class Main {
    static void main(String[] args) {
        try {
            DeployBuilder deployBuilder = new DeployBuilder()

            IDeployService installService = deployBuilder
                    .setProfileDetails('localhost', 9443, 'admin', 'admin')
                    .setDeployStrategy(Strategy.Custom, true)
                    .setArtifactPath("C:/Users/10000102/dev/resources/artifacts/hardware-manager-ear___v2.1.0.ear")
                    .setPackageName('hardware-manager')
                    .build()

            installService.connect().installArtifact().await(2000).startArtifact()

            System.out.println(installService.await(2000).getInstalledArtifacts().toString())
            System.out.println(installService.await(2000).getServerInformation().toString())

            installService.stopArtifact().await(2000).uninstallArtifact().disconnect()

        } catch (Exception exception) {
            exception.printStackTrace()
        }
    }
}
