package de.unileipzig.dbpraktikum.loader.model;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class DVD extends Product {
    String format;
    int runningTime;
    int regionCode;
    List<String> directors;
    List<String> actors;
    List<String> creator;

    @Override
    public ProductType getType() {
        return ProductType.DVD;
    }
}
