package de.unileipzig.dbpraktikum.loader.model.raw;

/**
 * Raw model class for parsed <musicspec> XML Element. 
 * All information stored in Strings, and will be converted during validation. 
 */
public record MusicSpecRaw(
    String releasedate
) {}
