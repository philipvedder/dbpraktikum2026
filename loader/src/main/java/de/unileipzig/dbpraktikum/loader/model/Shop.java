package de.unileipzig.dbpraktikum.loader.model;

import java.util.List;

/**
 * Shop base model class
 */
public record Shop (
    String name, 
    String street,
    String zip,
    List<Product> products
){}
