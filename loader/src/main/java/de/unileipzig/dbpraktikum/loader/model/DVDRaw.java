package de.unileipzig.dbpraktikum.loader.model;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class DVDRaw extends ProductRaw {
    private String format;
    private int runningTime;
    private int regionCode;
    private List<String> directors;
    private List<String> actors;
    private List<String> creator;

    @Override
    public ProductType getType() {
        return ProductType.DVD;
    }
}
