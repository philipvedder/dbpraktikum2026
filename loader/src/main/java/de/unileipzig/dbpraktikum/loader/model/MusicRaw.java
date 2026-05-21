package de.unileipzig.dbpraktikum.loader.model;

import java.sql.Timestamp;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class MusicRaw extends ProductRaw {
    private Timestamp releaseDate;
    private String label;
    private List<String> artists;
    private List<String> tracks;

    @Override
    public ProductType getType() {
        return ProductType.MUSIC;
    }
}
