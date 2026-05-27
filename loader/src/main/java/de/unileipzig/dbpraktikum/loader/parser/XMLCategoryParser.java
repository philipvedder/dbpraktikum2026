package de.unileipzig.dbpraktikum.loader.parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.unileipzig.dbpraktikum.loader.model.Category;
import de.unileipzig.dbpraktikum.loader.util.DOMUtil;

/**
 * Parses the raw XML Elements of a <categories> XML file to Raw Objects, where every variable is of type String.
 * These are not validated or converted whatsoever, and is only for the first structure of the file. 
 */
public class XMLCategoryParser {

    /**
     * Entry method to parse the whole content of a <categories> XMl Element. 
     * Triggers parsing of all <category> childs. 
     * @param rootElement The Root XML Element. 
     * @return List of all parsed Category objects. 
     */
    public static List<Category> parseXmlRoot(Element rootElement) {
        List<Category> results = new ArrayList<>();

        //cycle through each 
        Node child = rootElement.getFirstChild();
        while (child != null) {
            //Only ELEMENT_NODE's can be valid categories
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                child = child.getNextSibling();
                continue;
            } 
            
            //Parse each Category
            Category res = parseCategory((Element) child);
            if (res != null) results.add(res);
            
            //Next
            child = child.getNextSibling();
        }

        System.out.println(results.size() + " items in XML verarbeitet");
        return results;
    }

    /**
     * Parses a single <category> XML Element into a Catetory object. 
     * No validation or type conversion is done. Each attribute is a String. 
     * Recursive on child Categories. 
     * @param item <category> XML Element
     * @return Category object. 
     */
    private static Category parseCategory(Element item) {
        String name = null;
        List<Category> childCategoryRaws = new ArrayList<>();
        List<String> itemIds = new ArrayList<>();
        
        //Find first text content child, which holds the name of the category
        Node child = item.getFirstChild();
        if (child != null && child.getNodeType() == Node.TEXT_NODE) {
            name = child.getNodeValue();
            child = child.getNextSibling();
        }

        //Iterate thorugh each child
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) child;

                //Sub Categories are recursively parsed. 
                if (e.getTagName() == "category") {
                    childCategoryRaws.add(parseCategory(e));
                }

                //Extract ids of included products. 
                if (e.getTagName() == "item") {
                    itemIds.add(DOMUtil.childText(e));
                }
            }

            //next child
            child = child.getNextSibling();
        }

        //Return obj with all child categories included. 
        return new Category(name, itemIds, childCategoryRaws);
    }
}
