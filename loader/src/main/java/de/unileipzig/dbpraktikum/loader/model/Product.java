package de.unileipzig.dbpraktikum.loader.model;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

/**
 * Product base model class. Extended by Music, Book and DVD. 
 */
public class Product {
    private String asin; //ID
    private ProductType type; //Type IN (Book, Music, DVD)
    private String title;
    private Integer salesrank; //Optional
    private String imgUrl; //Optional
    private List<String> similarProductIds;
    private Offer offer; //Price information

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

    //Method to set the product data late, for subclasses. 
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

    //Getters
    public String getAsin() {
        return asin;
    }

    public ProductType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Integer getSalesrank() {
        return salesrank;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public List<String> getSimilarProductIds() {
        return similarProductIds;
    }

    public Offer getOffer() {
        return offer;
    }

    
}
