package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception typically thrown if some content was expected to represent a Integer which is > 0, but isn't. 
 */
public class NotANonNegativeIntegerException extends ValidationException {
    public NotANonNegativeIntegerException(String paramName, String val) {
        super(paramName + " : Value must be >= 0. Got: " + val);
    }
}


