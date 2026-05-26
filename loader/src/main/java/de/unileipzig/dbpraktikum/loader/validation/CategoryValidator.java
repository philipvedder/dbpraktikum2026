package de.unileipzig.dbpraktikum.loader.validation;

import java.util.ArrayList;
import java.util.List;

import de.unileipzig.dbpraktikum.exception.MultipleValidationException;
import de.unileipzig.dbpraktikum.exception.ValidationException;
import de.unileipzig.dbpraktikum.loader.logger.ErrorLogger;
import de.unileipzig.dbpraktikum.loader.model.Category;

/**
 * Validator class for XML <category> objects
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
            try {
                Category c = validate(categoryRaw);
                if (c != null) 
                    results.add(c);
                
            } catch (MultipleValidationException e) {
                //Log exceptions that occured for each category
                ErrorLogger.reportErrors(categoryRaw.name() + " - Category", e.getExceptions());
            }
        }

        //Result
        return results;
    }

    /**
     * Validates the input Category object by validating all its variables.
     * We allow Categories without items.
     * Throws if any ValidationErrors occur on the Category or its content.  
     * @param c Category input. 
     * @return validated Category. 
     * @throws MultipleValidationException, if any Validation threw an error. MultipleValidationException contains a list of all ValidationExceptions that occured on this Category.
     */
    public static Category validate(Category c) throws MultipleValidationException {
        List<ValidationException> categoryExceptions = new ArrayList<>(); //List of all Exceptions which occur during the validation.

        //Validate fields
        String name = requireNonBlank(c.name(), "title", categoryExceptions);
        List<String> items = cleanList(c.itemIds());

        //Recursive Validation of Childs
        List<Category> childs = new ArrayList<>();
        for (Category child : c.childCategories()) {
            try {
                Category valChild = validate(child);
                childs.add(valChild);
            } catch (MultipleValidationException ex) {
                categoryExceptions.addAll(ex.getExceptions());
            }
        }

        //Throw Exception if some occured
        if (!categoryExceptions.isEmpty()) {
            throw new MultipleValidationException(categoryExceptions);
        }

        //Return validated obj
        return new Category(name, items, childs);
    }
}
