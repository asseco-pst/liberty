package io.github.asseco.pst.liberty.builders

import io.github.asseco.pst.liberty.exceptions.BuilderException
import io.github.asseco.pst.liberty.models.Artifact
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

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
            return generateConfigFile(artifactConfigProperties, "${artifact.baseName}.xml")

        } catch (Exception exception) {
            throw new BuilderException("Could not build the configuration file due to: ${exception.getMessage()}")
        }
    }

    static File updateArtifactConfigFileAttribute(File artifactConfigFile, String tagName, String attribute, String value) throws BuilderException {
        Document artifactConfigDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(artifactConfigFile)
        NodeList nodes = artifactConfigDocument.getElementsByTagName(tagName)

        for (int idx = 0; idx < nodes.length; idx++) {
            Node node = nodes.item(idx).getAttributes().getNamedItem(attribute)
            node.setNodeValue(value)
        }

        return generateConfigFile(artifactConfigDocument, artifactConfigFile.name)
    }

    static File generateConfigFile(Document configDocument, String name) {
        File artifactConfigFile = new File(name)

        Transformer transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")

        DOMSource source = new DOMSource(configDocument)
        StreamResult outputFile = new StreamResult(artifactConfigFile)
        transformer.transform(source, outputFile)

        return artifactConfigFile
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
}
