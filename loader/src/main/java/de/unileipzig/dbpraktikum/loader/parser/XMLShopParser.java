package de.unileipzig.dbpraktikum.loader.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;
import de.unileipzig.dbpraktikum.loader.model.raw.BookCDRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.BookRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.BookSpecRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.DVDRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.DVDSpecRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.MusicRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.MusicSpecRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.PriceRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.ProductRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.ShopRaw;
import de.unileipzig.dbpraktikum.loader.util.DOMUtil;

/**
 * Parses the raw XML Elements of a <shop> XML file to Raw Objects, where every variable is of type String.
 * These are not validated or converted whatsoever, and is only for the first structure of the file. 
 */
public class XMLShopParser {
    /**
     * Entry method to parse the whole content of a <shop> XMl Element. 
     * Reads the shop attributes, and then triggers parsing of all <item> childs. 
     * @param rootElement The Root XML Element. 
     * @return ShopRaw. Object holding the complete XML content. 
     */
    public static ShopRaw parseXmlRoot(Element rootElement) {
        List<ProductRaw> products = new ArrayList<>();

        //Get Shop Attributes
        String name = DOMUtil.attr(rootElement, "name");
        String street = DOMUtil.attr(rootElement, "street");
        String zip = DOMUtil.attr(rootElement, "zip");

        //Parse all <item> child Elements
        for (Node item = rootElement.getFirstChild(); item != null; item = item.getNextSibling()) {
            if (item.getNodeType() != Node.ELEMENT_NODE) continue; //Only ELEMENT_NODE's can be valid items
            ProductRaw res = parseItem((Element) item);
            if (res != null) products.add(res);
        }

        //Info and return. 
        System.out.println(products.size() + " items in XML processed");
        return new ShopRaw(products, name, street, zip);
    }

    /**
     * Parses an <item> Element of a shop XML file into the corresponding Subclass of ProductRaw, depending on the type of the item. 
     * @param item A <item> XML Element. 
     * @return ProductRaw. Object with all information as String type variable. 
     */
    private static ProductRaw parseItem(Element item) {
        ProductType type = ProductType.fromXmlValue(DOMUtil.attr(item, "pgroup")); //Read Type of item. 

        // General attributes
        String asin = DOMUtil.attr(item, "asin");
        String salesRank = DOMUtil.attr(item, "salesrank");
        String picture = DOMUtil.attr(item, "picture");
        //String detailPage = DOMUtil.attr(item, "detailpage"); UNUSED
        //String ean = DOMUtil.attr(item, "ean"); UNUSED

        // General Child Nodes
        Map<String, List<Element>> childMap = DOMUtil.createChildMap(item);

        // These can only exist once
        String title = DOMUtil.childText(getFirstOrNull(childMap.get("title")));
        List<String> similars = parseSimilars(getFirstOrNull(childMap.get("similars")));
        
        // Can have multiple prices
        List<PriceRaw> prices = parsePrices(childMap.get("price"));

        // Parse specific child content and return the Subtypes of ProductRaw
        switch (type) {
            case MUSIC_CD:
                List<String> labels = parseNamedEntities(getFirstOrNull(childMap.get("labels")), "label");
                List<String> artists = parseNamedEntities(getFirstOrNull(childMap.get("artists")), "artist");
                List<String> tracks = parseTitles(getFirstOrNull(childMap.get("tracks")));

                MusicSpecRaw musicSpec = parseMusicSpec(getFirstOrNull(childMap.get("musicspec")));

                return new MusicRaw(
                    asin, 
                    type, 
                    title,
                    salesRank, 
                    picture, 
                    similars, 
                    prices, 
                    musicSpec,
                    labels,
                    artists,
                    tracks
                );

            case DVD:
                List<String> actors = parseNamedEntities(getFirstOrNull(childMap.get("actors")), "actor");
                List<String> creators = parseNamedEntities(getFirstOrNull(childMap.get("creators")), "creator");
                List<String> directors = parseNamedEntities(getFirstOrNull(childMap.get("directors")), "director");
                //List<String> studios = parseNamedEntities(childMap.get("studios"), "studio"); UNUSED

                DVDSpecRaw dvdSpec = parseDVDSpec(getFirstOrNull(childMap.get("dvdspec")));

                return new DVDRaw(
                    asin,
                    type,
                    title,
                    salesRank,
                    picture,
                    similars,
                    prices,
                    dvdSpec,
                    directors,
                    actors,
                    creators
                );
                
            case BOOK:
                List<String> authors = parseNamedEntities(getFirstOrNull(childMap.get("authors")), "author");
                List<String> publishers = parseNamedEntities(getFirstOrNull(childMap.get("publishers")), "publisher");

                BookSpecRaw bookSpec = parseBookSpec(getFirstOrNull(childMap.get("bookspec")));

                //Book CDs: No Pages tag, but titles.
                List<String> book_tracks = parseTitles(getFirstOrNull(childMap.get("tracks")));
                if (book_tracks.size() != 0) {
                    return new BookCDRaw(
                        asin, 
                        ProductType.BOOK_CD,
                        title,
                        salesRank,
                        picture,
                        similars,
                        prices,
                        bookSpec,
                        publishers,
                        authors,
                        book_tracks
                    );
                }

                
                return new BookRaw(
                    asin, 
                    type,
                    title,
                    salesRank,
                    picture,
                    similars,
                    prices,
                    bookSpec,
                    publishers,
                    authors
                );
                
            default:
                return null;
        }
    }

    /**
     * Parse the <bookspec> element of the XML File. 
     * Returns null if not existent.
     * @param item XML Element of <bookspec>
     * @return BookSpecRaw object with content as String objects. 
     */
    private static BookSpecRaw parseBookSpec(Element item) {
        if (item == null || item.getNodeType() != Node.ELEMENT_NODE) return null;
        Map<String, List<Element>> childElements = DOMUtil.createChildMap(item);

        String pages = DOMUtil.childText(getFirstOrNull(childElements.get("pages")));
        String publication = DOMUtil.attr(getFirstOrNull(childElements.get("publication")), "date");
        String isbn = DOMUtil.attr(getFirstOrNull(childElements.get("isbn")), "val");

        return new BookSpecRaw(isbn, pages, publication);
    }

    /**
     * Parse the <musicspec> element of the XML File. 
     * Returns null if not existent.
     * @param item XML Element of <musicspec>
     * @return MusicSpecRaw object with content as String objects. 
     */
    private static MusicSpecRaw parseMusicSpec(Element item) {
        if (item == null || item.getNodeType() != Node.ELEMENT_NODE) return null;
        Map<String, List<Element>> childElements = DOMUtil.createChildMap(item);

        String releasedate = DOMUtil.childText(getFirstOrNull(childElements.get("releasedate")));

        return new MusicSpecRaw(releasedate);
    }

    /**
     * Parse the <dvdspec> element of the XML File. 
     * Returns null if not existent.
     * @param item XML Element of <dvdspec>
     * @return DVDSpecRaw object with content as String objects. 
     */
    private static DVDSpecRaw parseDVDSpec(Element item) {
        if (item == null || item.getNodeType() != Node.ELEMENT_NODE) return null;
        Map<String, List<Element>> childElements = DOMUtil.createChildMap(item);

        String format = DOMUtil.childText(getFirstOrNull(childElements.get("format")));
        String regioncode = DOMUtil.childText(getFirstOrNull(childElements.get("regioncode")));
        String runningtime = DOMUtil.childText(getFirstOrNull(childElements.get("runningtime")));

        return new DVDSpecRaw(format, regioncode, runningtime);
    }

    /**
     * Parses the <tracks> Element of the Item, holding the tracks of a CD type item. 
     * Returns null if not existent. 
     * @param item The <tracks> XMl Element
     * @return List of the title names as Strings. 
     */
    private static List<String> parseTitles(Element item) {
        if (item == null || item.getNodeType() != Node.ELEMENT_NODE) return new ArrayList<>();
        List<String> result = new ArrayList<>();

        //Iterate through all childs. 
        Node child = item.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) child;

                if (e.getTagName().toLowerCase().trim().equals("title")) { //Get text content of each <title> element. 
                    result.add(DOMUtil.childText(e));
                }
            }

            child = child.getNextSibling();
        }

        return result;
    }

    /**
     * Helper method to parse each Element which has childs, where each child has name Attribute. 
     * Example: <artists> <artist name="Ward Churchill"/> <artist>This is also okay</artist> </artists>
     * @param item Parent XML Element, which has the named childs 
     * @param elementTag Tag of the named childs. In the Example, this would be "artist"
     * @return Returns a list of Strings, where each String is the content of the corresponding name attribute. 
     */
    private static List<String> parseNamedEntities(Element item, String elementTag) {
        if (item == null || item.getNodeType() != Node.ELEMENT_NODE) return new ArrayList<>();
        List<String> result = new ArrayList<>();

        //Iterate through each child
        Node child = item.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) { //Only Element nodes can be named entities
                Element e = (Element) child;

                if (e.getTagName().toLowerCase().trim().equals(elementTag.toLowerCase().trim())) { //Only parse elements with correct Tag. 
                    // Look at "name" tag.
                    String content = DOMUtil.attr(e, "name");

                    // Fall back to tag content if possible
                    if (content == null) {
                        content = DOMUtil.childText(e);
                    }

                    if (content != null && !content.isBlank()) {
                        result.add(content);
                    }

                }
            }

            child = child.getNextSibling();
        }

        return result;
    }

    /**
     * Parse the <similars> XMl Element for similar product IDs. 
     * Returns a list of only the similar IDs. 
     * In the folowing example, only "3473581852" would be in the returned list. 
     * <similars>
     *  <sim_product>
     *      <asin>3473581852</asin>
     *      <title>Ich will ihn, ich will ihn nicht</title>
     *  </sim_product>
     * <similars>
     * Returns an empty list if the item has no sim_product childs. 
     * @param item <similars> XML Element
     * @return List of the similar product ids. 
     */
    private static List<String> parseSimilars(Element item) {
        if (item == null) return new ArrayList<>();

        //iterate through each childs
        Node simProductItem = item.getFirstChild();
        List<String> results = new ArrayList<>();
        while (simProductItem != null) {
            //Only accept Element child Elements, which themselve have childs. 
            if (simProductItem.getNodeType() != Node.ELEMENT_NODE || !simProductItem.hasChildNodes()) { 
                simProductItem = simProductItem.getNextSibling();
                continue;
            }

            //Build child map, and extract only the asin number. 
            Map<String, List<Element>> childMap = DOMUtil.createChildMap((Element) simProductItem);
            String simAsin = DOMUtil.childText(getFirstOrNull(childMap.get("asin")));
            results.add(simAsin);

            //next child
            simProductItem = simProductItem.getNextSibling();
        }

        return results;
    }

    /**
     * Parse a set of <price> XML Elements into PriceRaw objects, constructed of String objects. 
     * Returns null if not existent. 
     * @param item <price> XML Elements
     * @return List<PriceRaw> object with content as Strings.
     */
    private static List<PriceRaw> parsePrices(List<Element> items) {
        if (items == null || items.size() == 0) {
            return null;
        }

        List<PriceRaw> result = new ArrayList<>();

        for (Element item : items) {
            if (item == null || item.getNodeType() != Node.ELEMENT_NODE) continue; //not an real element

            String mult = DOMUtil.attr(item, "mult");
            String state = DOMUtil.attr(item, "state");
            String currency = DOMUtil.attr(item, "currency");
            String price = DOMUtil.childText(item);

            result.add(new PriceRaw(price, mult, state, currency));
        }

        return result;
    }

    /**
     * GetFirstOrNull utility helper for lists. Returns the first list element, or NULL if list is empty
     * @param <T> Type of list
     * @param list list to search in
     * @return First element or Null
     */
    public static <T> T getFirstOrNull(List<T> list) {
        if (list == null) return null;
        return list.isEmpty() ? null : list.get(0);
    }
}
