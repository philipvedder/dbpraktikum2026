package de.unileipzig.dbpraktikum.loader.model;

import java.util.List;

public record Category (
    String name,
    List<String> itemIds,
    List<Category> childCategories
){}
