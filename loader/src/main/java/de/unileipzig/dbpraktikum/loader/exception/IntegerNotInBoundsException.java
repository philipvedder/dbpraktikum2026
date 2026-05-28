package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception typically thrown if some content was expected to represent a Integer which is > 0, but isn't. 
 */
public class IntegerNotInBoundsException extends ValidationException {
    public IntegerNotInBoundsException(String paramName, Integer lowerBound, Integer upperBound, String val) {
        super(paramName + " : Value must be >= " + lowerBound + "and <= " + upperBound + ". Got: " + val);
    }
}


