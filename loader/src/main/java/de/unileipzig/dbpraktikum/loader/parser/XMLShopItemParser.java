package de.unileipzig.dbpraktikum.loader.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;
import de.unileipzig.dbpraktikum.loader.model.raw.BookRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.BookSpecRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.DVDRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.DVDSpecRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.MusicRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.MusicSpecRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.PriceRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.ProductRaw;
import de.unileipzig.dbpraktikum.loader.util.DOMUtil;

public class XMLShopItemParser {
    public static List<ProductRaw> parseXmlRoot(Element rootElement) {
        List<ProductRaw> results = new ArrayList<>();

        for (Node item = rootElement.getFirstChild(); item != null; item = item.getNextSibling()) {
            if (item.getNodeType() != Node.ELEMENT_NODE) continue; //Only ELEMENT_NODE's can be valid items
            ProductRaw res = parseItem((Element) item);
            if (res != null) results.add(res);
        }

        System.out.println(results.size() + " items in XML verarbeitet");
        return results;
    }

    public static ProductRaw parseItem(Element item) {
        ProductType type = ProductType.fromXmlValue(DOMUtil.attr(item, "pgroup"));

        // General attributes
        String asin = DOMUtil.attr(item, "asin");
        String salesRank = DOMUtil.attr(item, "salesrank");
        String picture = DOMUtil.attr(item, "picture");
        //String detailPage = DOMUtil.attr(item, "detailpage"); UNUSED
        //String ean = DOMUtil.attr(item, "ean"); UNUSED

        // General Child Nodes
        Map<String, Element> childMap = DOMUtil.createChildMap(item);

        String title = DOMUtil.childText(childMap.get("title"));
        PriceRaw price = parsePrice(childMap.get("price"));
        List<String> similars = parseSimilars(childMap.get("similars"));

        // Specific Child Nodes
        switch (type) {
            case MUSIC_CD:
                List<String> labels = parseNamedEntities(childMap.get("labels"), "label");
                List<String> artists = parseNamedEntities(childMap.get("artists"), "artist");
                List<String> tracks = parseTitles(childMap.get("tracks"));

                MusicSpecRaw musicSpec = parseMusicSpec(childMap.get("musicspec"));

                return new MusicRaw(
                    asin, 
                    type, 
                    title,
                    salesRank, 
                    picture, 
                    similars, 
                    price, 
                    musicSpec,
                    labels,
                    artists,
                    tracks
                );

            case DVD:
                List<String> actors = parseNamedEntities(childMap.get("actors"), "actor");
                List<String> creators = parseNamedEntities(childMap.get("creators"), "creator");
                List<String> directors = parseNamedEntities(childMap.get("directors"), "director");
                //List<String> studios = parseNamedEntities(childMap.get("studios"), "studio"); UNUSED

                DVDSpecRaw dvdSpec = parseDVDSpec(childMap.get("dvdspec"));

                return new DVDRaw(
                    asin,
                    type,
                    title,
                    salesRank,
                    picture,
                    similars,
                    price,
                    dvdSpec,
                    directors,
                    actors,
                    creators
                );
                
            case BOOK:
                List<String> authors = parseNamedEntities(childMap.get("authors"), "author");
                List<String> publishers = parseNamedEntities(childMap.get("publishers"), "publisher");

                BookSpecRaw bookSpec = parseBookSpec(childMap.get("bookspec"));
                
                return new BookRaw(
                    asin, 
                    type,
                    title,
                    salesRank,
                    picture,
                    similars,
                    price,
                    bookSpec,
                    publishers,
                    authors
                );
                
            default:
                return null;
        }
    }

    private static BookSpecRaw parseBookSpec(Element item) {
        if (item == null || item.getNodeType() != Node.ELEMENT_NODE) return null;
        Map<String, Element> childElements = DOMUtil.createChildMap(item);

        String pages = DOMUtil.childText(childElements.get("pages"));
        String publication = DOMUtil.attr(childElements.get("publication"), "date");
        String isbn = DOMUtil.attr(childElements.get("isbn"), "val");

        return new BookSpecRaw(isbn, pages, publication);
    }

    private static MusicSpecRaw parseMusicSpec(Element item) {
        if (item == null || item.getNodeType() != Node.ELEMENT_NODE) return null;
        Map<String, Element> childElements = DOMUtil.createChildMap(item);

        String releasedate = DOMUtil.childText(childElements.get("releasedate"));

        return new MusicSpecRaw(releasedate);
    }

    private static DVDSpecRaw parseDVDSpec(Element item) {
        if (item == null || item.getNodeType() != Node.ELEMENT_NODE) return null;
        Map<String, Element> childElements = DOMUtil.createChildMap(item);

        String format = DOMUtil.childText(childElements.get("format"));
        String regioncode = DOMUtil.childText(childElements.get("regioncode"));
        String runningtime = DOMUtil.childText(childElements.get("runningtime"));

        return new DVDSpecRaw(format, regioncode, runningtime);
    }

    private static List<String> parseTitles(Element item) {
        if (item == null || item.getNodeType() != Node.ELEMENT_NODE) return new ArrayList<>();
        List<String> result = new ArrayList<>();

        Node child = item.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) child;

                if (e.getTagName().toLowerCase().trim() == "title") {
                    result.add(DOMUtil.childText(e));
                }
            }

            child = child.getNextSibling();
        }

        return result;
    }

    private static List<String> parseNamedEntities(Element item, String elementTag) {
        if (item == null || item.getNodeType() != Node.ELEMENT_NODE) return new ArrayList<>();
        List<String> result = new ArrayList<>();

        Node child = item.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) child;

                if (e.getTagName().toLowerCase().trim() == elementTag.toLowerCase().trim()) {
                    result.add(DOMUtil.attr(e, "name"));
                }
            }

            child = child.getNextSibling();
        }

        return result;
    }

    private static List<String> parseSimilars(Element item) {
        if (item == null) return new ArrayList<>();

        Node simProductItem = item.getFirstChild();
        List<String> results = new ArrayList<>();

        while (simProductItem != null) {
            if (simProductItem.getNodeType() != Node.ELEMENT_NODE || !simProductItem.hasChildNodes()) {
                simProductItem = simProductItem.getNextSibling();
                continue;
            }

            Map<String, Element> childMap = DOMUtil.createChildMap((Element) simProductItem);
            String simAsin = DOMUtil.childText(childMap.get("asin"));
            results.add(simAsin);

            simProductItem = simProductItem.getNextSibling();
        }

        return results;
    }

    private static PriceRaw parsePrice(Element item) {
        if (item == null || item.getNodeType() != Node.ELEMENT_NODE) return null;

        String mult = DOMUtil.attr(item, "mult");
        String state = DOMUtil.attr(item, "state");
        String currency = DOMUtil.attr(item, "currency");
        String price = DOMUtil.childText(item);

        return new PriceRaw(price, mult, state, currency);
    }
}
