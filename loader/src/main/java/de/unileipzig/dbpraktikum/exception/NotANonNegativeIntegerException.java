package de.unileipzig.dbpraktikum.exception;

public class NotANonNegativeIntegerException extends ValidationException {
    public NotANonNegativeIntegerException(String paramName, String val) {
        super(paramName + " : Value must be > 0. Got: " + val);
    }
}


