package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception typically thrown if a String was expected to only have a maximum of N characters, but isn't. 
 */
public class StringMaxLengthException extends ValidationException {
    public StringMaxLengthException(String paramName, String val, int numberOfChars) {
        super(paramName + " : Value must not have more than " + numberOfChars + " characters. Has: " + val.length());
    }
}
