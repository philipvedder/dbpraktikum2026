package de.unileipzig.dbpraktikum.loader.util;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DOMUtil {
    public static String attr(Element element, String attributeName) {
        if (element == null) return null;
        if (!element.hasAttribute(attributeName)) {
            return null;
        }

        String value = element.getAttribute(attributeName).trim();

        return value.isBlank() ? null : value;
    }

    public static String childText(Element element) {
        if (element == null) return null;
        String value = element.getTextContent().trim();
        return value.isBlank() ? null : value;
    }

    public static Map<String, Element> createChildMap(Element element) {
        Map<String, Element> result = new HashMap<>();
        Node child = element.getFirstChild();

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                result.put(((Element) child).getTagName(), (Element) child.cloneNode(true));
            }

            child = child.getNextSibling();
        }

        return result;
    }
}
