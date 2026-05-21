package de.unileipzig.dbpraktikum.loader.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.unileipzig.dbpraktikum.loader.model.DVDRaw;
import de.unileipzig.dbpraktikum.loader.model.PriceRaw;
import de.unileipzig.dbpraktikum.loader.model.ProductRaw;
import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;
import de.unileipzig.dbpraktikum.loader.util.DOMUtil;

public class ItemParser {
    public static void parseItem(Element item) {
        ProductType type = ProductType.fromXmlValue(DOMUtil.attr(item, "pgroup"));

        String asin = DOMUtil.attr(item, "asin");
        String salesRank = DOMUtil.attr(item, "salesrank");
        String picture = DOMUtil.attr(item, "picture");
        String detailPage = DOMUtil.attr(item, "detailpage");
        String ean = DOMUtil.attr(item, "ean");

        Map<String, Element> childMap = DOMUtil.createChildMap(item);

        String title = DOMUtil.childText(childMap.get("title"));
        PriceRaw price = parsePrice(childMap.get("price"));

        System.out.println(price);

        // DvdSpecRaw dvdSpec = parseDvdSpec(item);

        //List<String> similars = parseSimilars(childMap.get("similars"));
        //System.out.println(similars);

        // List<NamedEntityRaw> actors = parseNamedEntities(item, "actors", "actor");
        // List<NamedEntityRaw> artists = parseNamedEntities(item, "artists", "artist");
        // List<NamedEntityRaw> authors = parseNamedEntities(item, "authors", "author");
        // List<NamedEntityRaw> creators = parseNamedEntities(item, "creators", "creator");
        // List<NamedEntityRaw> directors = parseNamedEntities(item, "directors", "director");
        // List<NamedEntityRaw> labels = parseNamedEntities(item, "labels", "label");
        // List<NamedEntityRaw> publishers = parseNamedEntities(item, "publishers", "publisher");
        // List<NamedEntityRaw> studios = parseNamedEntities(item, "studios", "studio");

    }

    //TODO: This does not work :(
    private static List<String> parseSimilars(Element item) {
        if (item == null) return new ArrayList<>();

        Node simProductItem = item.getFirstChild();
        List<String> results = new ArrayList<>();

        while (simProductItem != null) {
            if (simProductItem.getNodeType() != Node.ELEMENT_NODE || !simProductItem.hasChildNodes()) {
                simProductItem = simProductItem.getNextSibling();
                continue;
            }

            //System.out.println(((Element) simProductItem).getTagName());
            // Map<String, Element> childMap = DOMUtil.createChildMap((Element) simProductItem);
            // String simAsin = DOMUtil.childText(childMap.get("asin"));
            // results.add(simAsin);

            System.out.println(((Element) simProductItem).getTagName());
            simProductItem = simProductItem.getNextSibling();
        }

        return results;
    }

    private static PriceRaw parsePrice(Element item) {
        String mult = DOMUtil.attr(item, "mult");
        String state = DOMUtil.attr(item, "state");
        String currency = DOMUtil.attr(item, "currency");
        String price = DOMUtil.childText(item);

        return new PriceRaw(price, mult, state, currency);
    }
}
