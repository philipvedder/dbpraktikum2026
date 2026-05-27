package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception typically thrown if a List is erroneously empty. 
 */
public class ListEmptyException extends ValidationException {
    public ListEmptyException(String paramName) {
        super(paramName + " : List must not be empty");
    }
}


