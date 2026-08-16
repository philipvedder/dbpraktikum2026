package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception typically thrown if a Object is a duplicate. 
 */
public class DuplicateException extends ValidationException {

    public DuplicateException(String paramName, String id) {
        super("Duplicate " + paramName + " found with ID: " + id);
    }
    
}
