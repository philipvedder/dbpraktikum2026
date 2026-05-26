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

/**
 * XML Reader class to read a XML file from a given Path to a XMl Element Object
 */
public class XMLReader {
    /**
     * Read the raw content of a XML file and construct a XML Element object from it. 
     * Respects the character encoding provided in the XML file. 
     * @param xmlFile Path to XMl file
     * @return XMl Element object of Root Element of XML.
     * @throws ParserConfigurationException thrown on invalid XML reader configurations
     * @throws IOException thrown on file reading problems. 
     * @throws SAXException thrown on XML file reading problems
     */
    public static Element readXmlFile(Path xmlFile) throws ParserConfigurationException, IOException, SAXException {
        System.out.println("Reading XML-File: " + xmlFile);

        //Construct DocumentBuilder 
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Standard configuration: Allow XML-Namespaces, Ignore comments, secure processing
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);

        //Read file and normalize
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile.toFile());
        document.getDocumentElement().normalize();

        //get root XML Element
        Element root = document.getDocumentElement();

        System.out.println("Successfully read XML-File. Root element: " + root.getTagName());
        return root;
    }
}
