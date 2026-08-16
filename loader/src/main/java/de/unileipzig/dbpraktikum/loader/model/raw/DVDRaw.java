package de.unileipzig.dbpraktikum.loader.model.raw;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

/**
 * Raw model class for parsed <item> XML element of type DVD. 
 * All information stored in Strings, and will be converted during validation. 
 */
public class DVDRaw extends ProductRaw {
    private DVDSpecRaw dvdSpec;
    private List<String> directors;
    private List<String> actors;
    private List<String> creators;

    public DVDRaw(
        String asin, 
        ProductType type, 
        String title, 
        String salesrank, 
        String imgUrl,
        List<String> similarProductIds, 
        PriceRaw offer, 
        DVDSpecRaw dvdSpec,
        List<String> directors, 
        List<String> actors,
        List<String> creators
    ) {
        super(asin, type, title, salesrank, imgUrl, similarProductIds, offer);

        this.dvdSpec = dvdSpec;
        this.directors = directors;
        this.actors = actors;
        this.creators = creators;
    }

    //Getters
    public DVDSpecRaw getDvdSpec() {
        return dvdSpec;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public List<String> getActors() {
        return actors;
    }

    public List<String> getCreators() {
        return creators;
    }
}
