package de.unileipzig.dbpraktikum.loader.validation;

import java.util.ArrayList;
import java.util.List;

import de.unileipzig.dbpraktikum.exception.MultipleValidationException;
import de.unileipzig.dbpraktikum.exception.ValidationException;
import de.unileipzig.dbpraktikum.loader.logger.ErrorLogger;
import de.unileipzig.dbpraktikum.loader.model.Category;

public class CategoryValidator extends Validator {
    public static List<Category> validateAll(List<Category> categories) {
        System.out.println("Validating " + categories.size() + " root categories...");

        List<Category> results = new ArrayList<>();
        for (Category categoryRaw : categories) {
            try {
                Category c = validate(categoryRaw);
                if (c != null) 
                    results.add(c);
                
            } catch (MultipleValidationException e) {
                ErrorLogger.reportErrors(categoryRaw.name() + " - Category", e.getExceptions());
            }
        }

        //Result
        return results;
    }

    public static Category validate(Category c) throws MultipleValidationException {
        List<ValidationException> categoryExceptions = new ArrayList<>(); //List of all Exceptions which occur during the validation.

        String name = requireNonBlank(c.name(), "title", categoryExceptions);
        List<String> items = cleanList(c.itemIds());

        List<Category> childs = new ArrayList<>();
        for (Category child : c.childCategories()) {
            try {
                Category valChild = validate(child);
                childs.add(valChild);
            } catch (MultipleValidationException ex) {
                categoryExceptions.addAll(ex.getExceptions());
            }
        }

        if (!categoryExceptions.isEmpty()) {
            throw new MultipleValidationException(categoryExceptions);
        }

        return new Category(name, items, childs);
    }
}
