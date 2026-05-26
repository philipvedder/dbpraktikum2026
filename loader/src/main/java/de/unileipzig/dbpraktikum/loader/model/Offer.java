package de.unileipzig.dbpraktikum.loader.model;

/**
 * Typed Offer model class. 
 */
public record Offer (
    double price,
    String currency,
    String state
){}
