import io.github.asseco.pst.liberty.builders.DeployBuilder
import io.github.asseco.pst.liberty.enums.Strategy
import io.github.asseco.pst.liberty.services.IDeployService

class Main {
    static void main(String[] args) {
        try {
            DeployBuilder deployBuilder = new DeployBuilder()

            IDeployService installService = deployBuilder
                    .setProfileDetails('localhost', 9443, 'admin', 'admin')
                    .setDeployStrategy(Strategy.JMX, true)
                    .setArtifactPath("C:/Users/10000102/dev/resources/artifacts/hardware-manager-ear___v2.1.0.ear")
                    .setPackageName('hardware-manager')
                    .build()

            installService
                    .connect()
                    .installArtifact()
                    .await(2000)
                    .restartArtifact()
                    .disconnect()


            IDeployService listService = deployBuilder
                    .setProfileDetails('localhost', 9443, 'admin', 'admin')
                    .setDeployStrategy(Strategy.JMX, true)
                    .build()

            System.out.println(listService.connect().getInstalledArtifacts().toString())
            listService.disconnect()

        } catch (Exception exception) {
            exception.printStackTrace()
        }
    }
}
