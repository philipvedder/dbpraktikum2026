package de.unileipzig.dbpraktikum.loader.model;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class Product {
    private String asin;
    private ProductType type;
    private String title;
    private Integer salesrank;
    private String imgUrl;
    private List<String> similarProductIds;
    private Offer offer;

    public Product() {};
    
    public Product(String asin, ProductType type, String title, Integer salesrank, String imgUrl, List<String> similarProductIds, Offer offer) {
        this.asin = asin;
        this.type = type;
        this.title = title;
        this.salesrank = salesrank;
        this.imgUrl = imgUrl;
        this.similarProductIds = similarProductIds;
        this.offer = offer;
    }

    public void lateSetProductData(
        String asin, 
        ProductType type, 
        String title,
        Integer salesrank, 
        String imgUrl,
        List<String> similarProductIds,
        Offer offer
    ) {
        this.asin = asin;
        this.type = type;
        this.title = title;
        this.salesrank = salesrank;
        this.imgUrl = imgUrl;
        this.similarProductIds = similarProductIds;
        this.offer = offer;   
    }
}
