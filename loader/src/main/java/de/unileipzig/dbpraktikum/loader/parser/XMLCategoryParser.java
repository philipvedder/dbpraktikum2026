package de.unileipzig.dbpraktikum.loader.parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.unileipzig.dbpraktikum.loader.model.Category;
import de.unileipzig.dbpraktikum.loader.util.DOMUtil;

public class XMLCategoryParser {
    public static List<Category> parseXmlRoot(Element rootElement) {
        List<Category> results = new ArrayList<>();

        for (Node item = rootElement.getFirstChild(); item != null; item = item.getNextSibling()) {
            if (item.getNodeType() != Node.ELEMENT_NODE) continue; //Only ELEMENT_NODE's can be valid items
            Category res = parseCategory((Element) item);
            if (res != null) results.add(res);
        }

        System.out.println(results.size() + " items in XML verarbeitet");
        return results;
    }

    public static Category parseCategory(Element item) {
        String name = null;
        List<Category> childCategoryRaws = new ArrayList<>();
        List<String> itemIds = new ArrayList<>();
        
        Node child = item.getFirstChild();
        if (child != null && child.getNodeType() == Node.TEXT_NODE) {
            name = child.getNodeValue();
            child = child.getNextSibling();
        }

        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) child;

                if (e.getTagName() == "category") {
                    childCategoryRaws.add(parseCategory(e));
                }

                if (e.getTagName() == "item") {
                    itemIds.add(DOMUtil.childText(e));
                }
            }

            child = child.getNextSibling();
        }

        return new Category(name, itemIds, childCategoryRaws);
    }
}
