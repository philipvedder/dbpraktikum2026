package de.unileipzig.dbpraktikum.loader.model.raw;

/**
 * Raw model class for parsed <bookspec> XML Element. 
 * All information stored in Strings, and will be converted during validation. 
 */
public record BookSpecRaw(
    String isbn,
    String pages,
    String publication
){}