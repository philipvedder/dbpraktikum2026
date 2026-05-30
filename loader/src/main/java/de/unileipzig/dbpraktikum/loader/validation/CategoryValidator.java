package de.unileipzig.dbpraktikum.loader.validation;

import java.util.ArrayList;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.exception.*;
import de.unileipzig.dbpraktikum.loader.logger.ErrorLogger;
import de.unileipzig.dbpraktikum.loader.model.Category;

/**
 * Validator class for XML Category objects
 * Checks each variable of each category object and returns validated Objects 
 */
public class CategoryValidator extends Validator {
    /**
     * Validates a List of Category objects. 
     * All errors that occur for each category will be logged. 
     * @param categories List<Category> the categories to validate
     * @return The validated List<Category>
     */
    public static List<Category> validateAll(List<Category> categories) {
        System.out.println("Validating " + categories.size() + " root categories...");

        //Validate each root category
        List<Category> results = new ArrayList<>();
        for (Category categoryRaw : categories) {
            Category c = validate(categoryRaw);
            if (c != null) 
                results.add(c);
        }

        //Result
        return results;
    }

    /**
     * Validates the input Category object by validating all its variables.
     * We allow Categories without items.
     * Writes itself to ErrorLog if a Category shows validation errors (only possible on the title name) and skips that Category and its childs then. 
     * Recursive on child Categories. 
     * @param c Category input. 
     * @return validated Category. 
     */
    private static Category validate(Category c) {
        List<ValidationException> categoryExceptions = new ArrayList<>(); //List of all Exceptions which occur during the validation.

        //Validate fields
        String name = requireNonBlank(c.name(), "title", categoryExceptions);
        List<String> items = cleanList(c.itemIds());

        //Throw Exception if some occured
        if (!categoryExceptions.isEmpty()) {
            ErrorLogger.reportErrors("Category with name " + c.name(), categoryExceptions);
            return null;
        }

        //Recursive Validation of Childs
        List<Category> childs = new ArrayList<>();
        for (Category child : c.childCategories()) {
            Category valChild = validate(child);
            if (valChild != null) childs.add(valChild);
        }

        //Return validated obj
        return new Category(name, items, childs);
    }
}
