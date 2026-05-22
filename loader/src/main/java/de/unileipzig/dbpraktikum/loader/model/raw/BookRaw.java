package de.unileipzig.dbpraktikum.loader.model.raw;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class BookRaw extends ProductRaw {
    private BookSpecRaw bookSpec;
    private List<String> publishers;
    private List<String> authors;

    public BookRaw(
        String id, 
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
        super(id, type, title, salesrank, imgUrl, similarProductIds, offer);

        this.bookSpec = bookSpec;
        this.publishers = publishers;
        this.authors = authors;
    }
}
