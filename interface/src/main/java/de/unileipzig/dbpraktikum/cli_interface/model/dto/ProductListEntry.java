package de.unileipzig.dbpraktikum.cli_interface.model.dto;

import java.math.BigDecimal;

import de.unileipzig.dbpraktikum.cli_interface.model.enums.ProductType;

public class ProductListEntry {
    String id;
    String title; 
    ProductType type;
    BigDecimal avgRating;

    public ProductListEntry(String id, String title, ProductType type, BigDecimal avgRating) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.avgRating = avgRating;
    }

    //Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public ProductType getType() {
        return type;
    }

    public BigDecimal getAvgRating() {
        return avgRating;
    }
}