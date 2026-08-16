package de.unileipzig.dbpraktikum.loader.model.raw;

/**
 * Raw model class for parsed <dvdspec> XML Element. 
 * All information stored in Strings, and will be converted during validation. 
 */
public record DVDSpecRaw (
    String format,
    String regioncode,
    String runningtime
) {}
