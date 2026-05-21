package de.unileipzig.dbpraktikum.loader.input;

import java.io.IOException;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLReader {
    private static void mapXmlNodeToitem(Node item) {
        if (item.getNodeType() != Node.ELEMENT_NODE) return; //Only ELEMENT_NODE's can be valid items
        String type = item.getAttributes().getNamedItem("pgroup").getNodeValue().toString().toLowerCase().trim();

        switch (type) {
            case "dvd":


                break;
            case "book":
            case "buch":


                break;
            case "music":
            case "musik":
            case "musical":

            
                break;
            default:
                System.out.println("ERROR: Konnte XML-Eintrag mit typ " + type + "nicht verarbeiten.");
                break;
        }
    }

    public static void mapXmlToObjects(Element rootElement) throws Exception {
        int counter = 0;
        for (Node item = rootElement.getFirstChild(); item != null; item = item.getNextSibling()) {
            counter++;

            mapXmlNodeToitem(item);
            continue;

            // if (item.getNodeType() == Node.ELEMENT_NODE) {
            //     System.out.println(item.getAttributes().getNamedItem("pgroup"));
            //     System.out.println(item.getAttributes().getNamedItem("asin"));
            //     System.out.println(item.getAttributes().getNamedItem("salesrank"));
            //     System.out.println(item.getAttributes().getNamedItem("picture"));
            //     System.out.println(item.getAttributes().getNamedItem("ean"));

            //     for (Node item2 = item.getFirstChild(); item2 != null; item2 = item2.getNextSibling()) {
            //         if (item2.getNodeType() != Node.ELEMENT_NODE) continue;

            //         System.out.println(item2.getLocalName());

            //         switch (item2.getLocalName()) {
            //             case "price":
            //                 System.out.println("XXX");
            //                 break;
                    
            //             default:
            //                 break;
            //         }
            //     }

            //     return;
            // }
        }

        System.out.println(counter + " items in XML verarbeitet");
    }

    public static Element readXmlFile(Path xmlFile) throws ParserConfigurationException, IOException, SAXException {
        System.out.println("Lese XML-Datei: " + xmlFile);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Standard configuration: Allow XML-Namespaces, Ignore comments, secure processing
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile.toFile());
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();

        System.out.println("XML erfolgreich gelesen. Root-Element: " + root.getTagName());
        return root;
    }


}
