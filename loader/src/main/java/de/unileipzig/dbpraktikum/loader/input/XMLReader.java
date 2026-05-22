package de.unileipzig.dbpraktikum.loader.input;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.unileipzig.dbpraktikum.loader.model.raw.ProductRaw;
import de.unileipzig.dbpraktikum.loader.parser.XMLItemParser;

public class XMLReader {
    public static List<ProductRaw> parseXml(Element rootElement) throws Exception {
        List<ProductRaw> results = new ArrayList<>();

        for (Node item = rootElement.getFirstChild(); item != null; item = item.getNextSibling()) {
            if (item.getNodeType() != Node.ELEMENT_NODE) continue; //Only ELEMENT_NODE's can be valid items
            ProductRaw res = XMLItemParser.parseItem((Element) item);
            if (res != null) results.add(res);
        }

        System.out.println(results.size() + " items in XML verarbeitet");
        return results;
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
