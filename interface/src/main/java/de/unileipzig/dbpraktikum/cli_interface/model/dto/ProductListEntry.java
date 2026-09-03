package de.unileipzig.dbpraktikum.cli_interface.model.dto;

import java.math.BigDecimal;

import de.unileipzig.dbpraktikum.cli_interface.model.enums.ProductType;

public class ProductListEntry {
    String id;
    String title; 
    ProductType type;
    BigDecimal avgRating;
    Integer ratingQuantity;

    public ProductListEntry(String id, String title, ProductType type, BigDecimal avgRating, Integer ratingQuantity) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.avgRating = avgRating;
        this.ratingQuantity = ratingQuantity;
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

    public Integer getRatingQuantity() {
        return ratingQuantity;
    }
}