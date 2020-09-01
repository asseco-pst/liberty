package io.github.asseco.pst.liberty.builders

import io.github.asseco.pst.liberty.exceptions.BuilderException
import io.github.asseco.pst.liberty.models.Artifact
import org.w3c.dom.Document
import org.w3c.dom.Element

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * XML config file builder for creating the necessary application configuration
 *
 * @date 31/08/2020
 * @version 1.0.0
 */
class ConfigFileBuilder {

    static File buildArtifactConfigFile(String serverName, String deploymentPath, Artifact artifact) throws BuilderException {
        try {
            Document artifactConfigProperties = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
            Element serverRootElement = artifactConfigProperties.createElement("server")

            serverRootElement.setAttribute("description", serverName)
            serverRootElement.appendChild(fillApplicationConfiguration(artifactConfigProperties, artifact, deploymentPath))

            artifactConfigProperties.appendChild(serverRootElement)
            return generateConfigFile(artifactConfigProperties, artifact)

        } catch (Exception exception) {
            throw new BuilderException("Could not build the configuration file due to: ${exception.getMessage()}")
        }
    }

    private static Element fillApplicationConfiguration(Document document, Artifact artifact, String deploymentPath) {
        Element artifactNode = document.createElement("application")

        /* Fills out the necessary attributes for the node configuration*/
        artifactNode.setAttribute("id", artifact.baseName)
        artifactNode.setAttribute("name", artifact.baseName)
        artifactNode.setAttribute("type", artifact.type.toString())
        artifactNode.setAttribute("location", "${deploymentPath}/${artifact.name}")
        artifactNode.setAttribute("autoStart", artifact.autoStart.toString())
        /*End*/

        return artifactNode
    }

    private static File generateConfigFile(Document configDocument, Artifact artifact) {
        File artifactConfigFile = new File("${artifact.baseName}.xml")

        Transformer transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")

        DOMSource source = new DOMSource(configDocument)
        StreamResult outputFile = new StreamResult(artifactConfigFile)
        transformer.transform(source, outputFile)

        return artifactConfigFile
    }
}
