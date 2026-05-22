package de.unileipzig.dbpraktikum.loader.model.raw;

public record PriceRaw (
    String price,
    String mult,
    String state,
    String currency
){}
