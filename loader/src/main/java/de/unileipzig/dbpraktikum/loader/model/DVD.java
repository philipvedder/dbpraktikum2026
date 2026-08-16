package de.unileipzig.dbpraktikum.loader.model;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

/**
 * Typed DVD model class. 
 * Extends Product with specific attributes. 
 */
public class DVD extends Product {
    private List<String> directorNames;
    private List<String> actorNames;
    private List<String> creatorNames;
    private List<String> formats;
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
        List<String> formats, 
        int runningtime,
        int regioncode
    ) {
        super(asin, type, title, salesrank, imgUrl, similarProductIds, offer);
        
        this.directorNames = directorNames;
        this.actorNames = actorNames;
        this.creatorNames = creatorNames;
        this.formats = formats;
        this.runningtime = runningtime;
        this.regioncode = regioncode;
    }

    // Constructor for only DVD specific variables
    public DVD(
        List<String> directorNames,
        List<String> actorNames,
        List<String> creatorNames,
        List<String> formats, 
        int runningtime,
        int regioncode
    ) {
        super();

        this.directorNames = directorNames;
        this.actorNames = actorNames;
        this.creatorNames = creatorNames;
        this.formats = formats;
        this.runningtime = runningtime;
        this.regioncode = regioncode;
    }

    //Getter
    public List<String> getDirectorNames() {
        return directorNames;
    }

    public List<String> getActorNames() {
        return actorNames;
    }

    public List<String> getCreatorNames() {
        return creatorNames;
    }

    public List<String> getFormats() {
        return formats;
    }

    public int getRunningtime() {
        return runningtime;
    }

    public int getRegioncode() {
        return regioncode;
    }

    
}
