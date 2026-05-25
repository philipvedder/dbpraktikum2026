package de.unileipzig.dbpraktikum.loader.model;

import java.sql.Date;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class Music extends Product {
    private String labelName;
    private List<String> artistNames;
    private List<String> trackNames;
    private Date releaseDate;

    public Music(
        String asin, 
        ProductType type, 
        String title,
        Integer salesrank, 
        String imgUrl,
        List<String> similarProductIds,
        Offer offer,
        String labelName,
        List<String> artistNames,
        List<String> trackNames,
        Date releaseDate
    ) {
        super(asin, type, title, salesrank, imgUrl, similarProductIds, offer);
        
        this.labelName = labelName;
        this.artistNames = artistNames;
        this.trackNames = trackNames;
        this.releaseDate = releaseDate;
    }

    public Music(
        String labelName,
        List<String> artistNames,
        List<String> trackNames,
        Date releaseDate
    ) {
        super();

        this.labelName = labelName;
        this.artistNames = artistNames;
        this.trackNames = trackNames;
        this.releaseDate = releaseDate;
    }

    public String getLabelName() {
        return labelName;
    }

    public List<String> getArtistNames() {
        return artistNames;
    }

    public List<String> getTrackNames() {
        return trackNames;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    
}
