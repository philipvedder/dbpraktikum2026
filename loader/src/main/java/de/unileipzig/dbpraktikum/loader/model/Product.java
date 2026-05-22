package de.unileipzig.dbpraktikum.loader.model;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;
import de.unileipzig.dbpraktikum.loader.model.raw.PriceRaw;

public class Product {
    private String asin;
    private ProductType type;
    private String title;
    private Integer salesrank;
    private String imgUrl;
    private List<String> similarProductIds;
    private Offer offer;
}
