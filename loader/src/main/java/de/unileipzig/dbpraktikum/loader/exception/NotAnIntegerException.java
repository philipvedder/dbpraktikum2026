package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception typically thrown if some content was expected to represent a Integer, but isn't. 
 */
public class NotAnIntegerException extends ValidationException {
    public NotAnIntegerException(String paramName, String val) {
        super(paramName + " : Value must be a Integer. Got: " + val);
    }
}


