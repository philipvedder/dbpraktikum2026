package de.unileipzig.dbpraktikum.loader.model.raw;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class ProductRaw {
    private String asin;
    private ProductType type;
    private String title;
    private String salesrank;
    private String imgUrl;
    private List<String> similarProductIds;
    private PriceRaw offer;
    
    public ProductRaw(String asin, ProductType type, String title, String salesrank, String imgUrl,
            List<String> similarProductIds, PriceRaw offer) {
        this.asin = asin;
        this.type = type;
        this.title = title;
        this.salesrank = salesrank;
        this.imgUrl = imgUrl;
        this.similarProductIds = similarProductIds;
        this.offer = offer;
    }

    public String getAsin() {
        return asin;
    }

    public ProductType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSalesrank() {
        return salesrank;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public List<String> getSimilarProductIds() {
        return similarProductIds;
    }

    public PriceRaw getOffer() {
        return offer;
    }

    
}