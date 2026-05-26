package de.unileipzig.dbpraktikum.loader.model.raw;

/**
 * Raw model class for parsed <price> XML Element. 
 * All information stored in Strings, and will be converted during validation. 
 */
public record PriceRaw (
    String price,
    String mult,
    String state,
    String currency
){}
