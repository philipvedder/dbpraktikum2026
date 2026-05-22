package de.unileipzig.dbpraktikum.loader.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unileipzig.dbpraktikum.loader.model.Category;

public class CategoryValidator {
    public static List<Category> validateAll(List<Category> products) {
        List<Category> results = new ArrayList<>();

        for (Category categoryRaw : products) {
            Category c = validate(categoryRaw);
            if (c != null) results.add(c);
        }

        return results;
    }

    public static Category validate(Category c) {
        Map<String, String> errors = new HashMap<>();

        //TODO

        return c;
    }
}
