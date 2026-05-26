package de.unileipzig.dbpraktikum.loader.input;

import java.io.IOException;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class XMLReader {
    public static Element readXmlFile(Path xmlFile) throws ParserConfigurationException, IOException, SAXException {
        System.out.println("Reading XML-File: " + xmlFile);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Standard configuration: Allow XML-Namespaces, Ignore comments, secure processing
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile.toFile());
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();

        System.out.println("Successfully read XML-File. Root element: " + root.getTagName());
        return root;
    }
}
