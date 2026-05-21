package de.unileipzig.dbpraktikum.loader.model;

import java.sql.Timestamp;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class BookRaw extends ProductRaw {
    private int pages;
    private Timestamp publication;
    private String isbn;
    private String publisher;
    private List<String> authors;

    @Override
    public ProductType getType() {
        return ProductType.BOOK;
    }
}
