package de.unileipzig.dbpraktikum.loader.model.raw;

import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

/**
 * Raw model class for parsed <item> XML element of type MusicCD. 
 * All information stored in Strings, and will be converted during validation. 
 */
public class MusicRaw extends ProductRaw {
    private MusicSpecRaw musicSpec;
    private List<String> labels;
    private List<String> artists;
    private List<String> tracks;

    public MusicRaw(
        String asin, 
        ProductType type, 
        String title, 
        String salesrank, 
        String imgUrl,
        List<String> similarProductIds, 
        PriceRaw offer, 
        MusicSpecRaw musicSpec, 
        List<String> labels, 
        List<String> artists, 
        List<String> tracks
    ) {
        super(asin, type, title, salesrank, imgUrl, similarProductIds, offer);

        this.musicSpec = musicSpec;
        this.labels = labels;
        this.artists = artists;
        this.tracks = tracks;
    }

    //Getters
    public MusicSpecRaw getMusicSpec() {
        return musicSpec;
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<String> getArtists() {
        return artists;
    }

    public List<String> getTracks() {
        return tracks;
    }

    
}
