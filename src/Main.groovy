import io.github.asseco.pst.liberty.enums.Strategy
import io.github.asseco.pst.liberty.models.Artifact
import io.github.asseco.pst.liberty.models.Package
import io.github.asseco.pst.liberty.models.Profile
import io.github.asseco.pst.liberty.services.DeployerService
import io.github.asseco.pst.liberty.services.IDeployerService

class Main {
    static void main(String[] args) {
        Artifact artifact = new Artifact("C:/Users/10000102/dev/resources/artifacts/hardware-manager-ear___v2.1.0.ear")
        Package pkg = new Package('hardware-manager')

        Profile profile = new Profile('localhost', 9443, 'admin', 'admin')
        IDeployerService deployerService = new DeployerService(profile, Strategy.JMX, true)

        try {
            deployerService.connect()
            deployerService.installArtifact(artifact)
            Thread.sleep(2000)
            deployerService.startArtifact(artifact)

            deployerService.getInstalledArtifactsForPackage(pkg).forEach({
                System.out.println(it)
            })

            deployerService.stopArtifact(artifact)
            Thread.sleep(2000)
            deployerService.uninstallArtifact(artifact)
        } catch (Exception exception) {
            exception.printStackTrace()
        }
    }
}
