package glassfishdc.services;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class ConfigFileGenerator {
    private final Document templateFile;
    private final Document resourcesFile;

    public ConfigFileGenerator(InputStream templateFile, InputStream resourcesFile) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            this.templateFile = builder.parse(templateFile);
            this.resourcesFile = builder.parse(resourcesFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generate() {
        NodeList templateResources = templateFile.getElementsByTagName("resources");
        NodeList additionalResources = resourcesFile.getElementsByTagName("resources");

        Node templateResourceList = templateResources.item(0);
        for (Node nextAdditionalResource = additionalResources.item(0).getFirstChild();
             nextAdditionalResource.getNextSibling() != null;
             nextAdditionalResource = nextAdditionalResource.getNextSibling()) {
            Node additionalResource = templateFile.importNode(nextAdditionalResource, true);
            templateResourceList.appendChild(additionalResource);
        }

        DOMSource source = new DOMSource(templateFile);
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
            return new String(out.toByteArray());
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public static void usage() {
        System.out.println("usage: java -jar glassfish-domain-config.jar templatefile resourcesfile");
    }

    public static void main(String[] args) throws IOException {
        String templateFile = args[0];
        String resourcesFile = args[1];

        if (templateFile == null) {
            usage();
            throw new IllegalArgumentException("Must specify templateFile as first argument");
        }

        if (resourcesFile == null) {
            usage();
            throw new IllegalArgumentException("Must specify resourcesFile as second argument");
        }

        try (InputStream templateFileIn = new FileInputStream(templateFile);
            InputStream resourcesFileIn = new FileInputStream(resourcesFile)) {
            System.out.println(new ConfigFileGenerator(templateFileIn, resourcesFileIn).generate());
        }
    }
}
