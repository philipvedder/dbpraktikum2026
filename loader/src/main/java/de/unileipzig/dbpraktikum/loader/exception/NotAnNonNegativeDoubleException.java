package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception typically thrown if some content was expected to represent a Double which is >= 0, but isn't. 
 */
public class NotAnNonNegativeDoubleException extends ValidationException {
    public NotAnNonNegativeDoubleException(String paramName, String val) {
        super(paramName + " : Value must be >= 0.0. Got: " + val);
    }
}


