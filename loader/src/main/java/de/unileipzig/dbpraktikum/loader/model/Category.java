package de.unileipzig.dbpraktikum.loader.model;

import java.util.List;

/**
 * Typed Category model class. 
 */
public record Category (
    String name, //title of the category
    List<String> itemIds, //Included Procuts ids
    List<Category> childCategories //child categories
){}
