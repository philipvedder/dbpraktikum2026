package de.unileipzig.dbpraktikum.loader.model.raw;

import java.util.List;

/**
 * Raw model class for parsed <shop> XML element. 
 * All information stored in Strings, and will be converted during validation. 
 */
public record ShopRaw(
    List<ProductRaw> products,
    String name,
    String street,
    String zip
){}
