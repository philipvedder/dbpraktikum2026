package de.unileipzig.dbpraktikum.loader.model;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class ProductRaw {
    private String id;
    private ProductType type;
    private String title;
    private int salesrank;
    private String imgUrl;
    private List<String> similarProductIds;
    private PriceRaw offer;

    public ProductType getType() {
        throw new UnsupportedOperationException("Unimplemented method 'getType' for class Product");
    }
}