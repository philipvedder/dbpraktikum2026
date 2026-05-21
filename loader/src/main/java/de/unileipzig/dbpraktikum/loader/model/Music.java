package de.unileipzig.dbpraktikum.loader.model;

import java.sql.Timestamp;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class Music extends Product {
    Timestamp releaseDate;
    String label;
    List<String> artists;
    List<String> tracks;

    @Override
    public ProductType getType() {
        return ProductType.MUSIC;
    }
}
