package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception typically thrown if a Object is erroneously NULL. 
 */
public class NullException extends ValidationException {
    public NullException(String paramName) {
        super(paramName + " : Value must not be NULL");
    }
}


