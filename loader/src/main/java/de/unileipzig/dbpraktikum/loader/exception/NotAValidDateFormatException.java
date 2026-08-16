package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception typically thrown if some content was expected to represent a Date, but isn't. 
 */
public class NotAValidDateFormatException extends ValidationException {
    public NotAValidDateFormatException(String paramName, String val) {
        super(paramName + " : Value must be in valid Date format yyyy-[m]m-[d]d. Got: " + val);
    }
}


