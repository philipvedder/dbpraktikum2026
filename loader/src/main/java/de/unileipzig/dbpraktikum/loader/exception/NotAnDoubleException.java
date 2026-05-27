package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception typically thrown if some content was expected to represent a Double, but isn't. 
 */
public class NotAnDoubleException extends ValidationException {
    public NotAnDoubleException(String paramName, String val) {
        super(paramName + " : Value must be a floating point number. Got: " + val);
    }
}


