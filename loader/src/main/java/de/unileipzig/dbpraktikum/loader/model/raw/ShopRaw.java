package de.unileipzig.dbpraktikum.loader.model.raw;

import java.util.List;

public record ShopRaw(
    List<ProductRaw> products,
    String name,
    String street,
    String zip
){}
