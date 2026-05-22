package de.unileipzig.dbpraktikum.loader.model;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class DVD extends Product {
    private List<String> directorNames;
    private List<String> actorNames;
    private List<String> creatorNames;
    private String format;
    private int runningtime;
    private int regioncode;

    public DVD(
        String asin, 
        ProductType type, 
        String title,
        Integer salesrank, 
        String imgUrl,
        List<String> similarProductIds,
        Offer offer,
        List<String> directorNames,
        List<String> actorNames,
        List<String> creatorNames,
        String format, 
        int runningtime,
        int regioncode
    ) {
        super(asin, type, title, salesrank, imgUrl, similarProductIds, offer);
        
        this.directorNames = directorNames;
        this.actorNames = actorNames;
        this.creatorNames = creatorNames;
        this.format = format;
        this.runningtime = runningtime;
        this.regioncode = regioncode;
    }

    public DVD(
        List<String> directorNames,
        List<String> actorNames,
        List<String> creatorNames,
        String format, 
        int runningtime,
        int regioncode
    ) {
        super();

        this.directorNames = directorNames;
        this.actorNames = actorNames;
        this.creatorNames = creatorNames;
        this.format = format;
        this.runningtime = runningtime;
        this.regioncode = regioncode;
    }
}
