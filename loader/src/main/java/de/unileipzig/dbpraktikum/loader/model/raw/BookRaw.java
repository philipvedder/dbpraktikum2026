package de.unileipzig.dbpraktikum.loader.model.raw;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

/**
 * Raw model class for parsed <item> XML element of type Book. 
 * All information stored in Strings, and will be converted during validation. 
 */
public class BookRaw extends ProductRaw {
    private BookSpecRaw bookSpec;
    private List<String> publishers;
    private List<String> authors;

    public BookRaw(
        String asin, 
        ProductType type, 
        String title, 
        String salesrank, 
        String imgUrl,
        List<String> similarProductIds, 
        PriceRaw offer, 
        BookSpecRaw bookSpec,
        List<String> publishers, 
        List<String> authors
    ) {
        super(asin, type, title, salesrank, imgUrl, similarProductIds, offer);

        this.bookSpec = bookSpec;
        this.publishers = publishers;
        this.authors = authors;
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
}
