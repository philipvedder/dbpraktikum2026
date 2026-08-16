package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception typically thrown if a String is erroneously empty. 
 */
public class BlankException extends ValidationException {
    public BlankException(String paramName) {
        super(paramName + " : Value must not be empty");
    }
}


