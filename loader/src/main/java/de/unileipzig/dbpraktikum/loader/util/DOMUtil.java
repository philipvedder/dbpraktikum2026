package de.unileipzig.dbpraktikum.loader.util;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Collection of useful methods to interact with XML Element objects.
 */
public class DOMUtil {
    /**
     * Returns a named attribute of a XML Element. 
     * Returns null if Attribute does not exist or is Blank. 
     * Example would be to return name in <shop name="test">
     * @param element Element. The XML element which holds the attribute. 
     * @param attributeName String. The name of the Attribute. 
     * @return The Attribute as a String obj. 
     */
    public static String attr(Element element, String attributeName) {
        if (element == null || !element.hasAttribute(attributeName)) return null;

        String value = element.getAttribute(attributeName).trim();
        return value.isBlank() ? null : value;
    }

    /**
     * Returns the Child Text content of a XML Element.
     * Example would be the content of <label>test</label> 
     * Returns null if text does not exist or is Blank. 
     * @param element Element. The XML element which holds the content. 
     * @return The text as String. 
     */
    public static String childText(Element element) {
        if (element == null) return null;
        String value = element.getTextContent().trim();
        return value.isBlank() ? null : value;
    }

    /**
     * Creates a Map<String, Element> of all childs of a XML Element. 
     * The keys of the Map are the Tag names of the childs, the value is a deep copy of the XML Element. 
     * @param element Element. The XML Element to build the Map for
     * @return Map<String, Element>. The resulting Map. 
     */
    public static Map<String, Element> createChildMap(Element element) {
        Map<String, Element> result = new HashMap<>();
        Node child = element.getFirstChild();

        //Iterate over all childs
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) { //Only Element type nodes are mapped.
                result.put(((Element) child).getTagName(), (Element) child.cloneNode(true));
            }

            child = child.getNextSibling(); //Next child
        }

        return result;
    }
}
