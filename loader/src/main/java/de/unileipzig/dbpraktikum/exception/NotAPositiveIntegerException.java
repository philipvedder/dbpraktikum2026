package de.unileipzig.dbpraktikum.exception;

public class NotAPositiveIntegerException extends ValidationException {
    public NotAPositiveIntegerException(String paramName, String val) {
        super(paramName + " : Value must be >= 0. Got: " + val);
    }
}


