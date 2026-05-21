package de.unileipzig.dbpraktikum.loader.model;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class Product {
    String id;
    ProductType type;
    String title;
    int salesrank;
    String imgUrl;
    List<String> similarProductIds;
    Offer offer;

    public ProductType getType() {
        throw new UnsupportedOperationException("Unimplemented method 'getType' for class Product");
    }
}