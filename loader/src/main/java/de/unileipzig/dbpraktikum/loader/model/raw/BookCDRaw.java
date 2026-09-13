package de.unileipzig.dbpraktikum.loader.model.raw;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

/**
 * Raw model class for parsed <item> XML element of type Book. 
 * All information stored in Strings, and will be converted during validation. 
 */
public class BookCDRaw extends ProductRaw {
    private BookSpecRaw bookSpec;
    private List<String> publishers;
    private List<String> authors;
    private List<String> tracks;

    public BookCDRaw(
        String asin, 
        ProductType type, 
        String title, 
        String salesrank, 
        String imgUrl,
        List<String> similarProductIds, 
        List<PriceRaw> offers, 
        BookSpecRaw bookSpec,
        List<String> publishers, 
        List<String> authors,
        List<String> tracks
    ) {
        super(asin, type, title, salesrank, imgUrl, similarProductIds, offers);

        this.bookSpec = bookSpec;
        this.publishers = publishers;
        this.authors = authors;
        this.tracks = tracks;
    }

    //Getters
    public BookSpecRaw getBookSpec() {
        return bookSpec;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<String> getTracks() {
        return tracks;
    }
}
