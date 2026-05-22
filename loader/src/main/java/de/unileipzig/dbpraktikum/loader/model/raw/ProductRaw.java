package de.unileipzig.dbpraktikum.loader.model.raw;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class ProductRaw {
    private String id;
    private ProductType type;
    private String title;
    private String salesrank;
    private String imgUrl;
    private List<String> similarProductIds;
    private PriceRaw offer;
    
    public ProductRaw(String id, ProductType type, String title, String salesrank, String imgUrl,
            List<String> similarProductIds, PriceRaw offer) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.salesrank = salesrank;
        this.imgUrl = imgUrl;
        this.similarProductIds = similarProductIds;
        this.offer = offer;
    }
}