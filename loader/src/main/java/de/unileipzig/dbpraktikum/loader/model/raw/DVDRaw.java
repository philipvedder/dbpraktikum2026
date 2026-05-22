package de.unileipzig.dbpraktikum.loader.model.raw;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class DVDRaw extends ProductRaw {
    private DVDSpecRaw dvdSpec;
    private List<String> directors;
    private List<String> actors;
    private List<String> creators;

    public DVDRaw(
        String id, 
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
        super(id, type, title, salesrank, imgUrl, similarProductIds, offer);

        this.dvdSpec = dvdSpec;
        this.directors = directors;
        this.actors = actors;
        this.creators = creators;
    }
}
