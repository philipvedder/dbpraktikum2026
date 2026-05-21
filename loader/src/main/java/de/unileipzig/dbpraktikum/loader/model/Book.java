package de.unileipzig.dbpraktikum.loader.model;

import java.sql.Timestamp;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class Book extends Product {
    int pages;
    Timestamp publication;
    String isbn;
    String publisher;
    List<String> authors;

    @Override
    public ProductType getType() {
        return ProductType.BOOK;
    }
}
