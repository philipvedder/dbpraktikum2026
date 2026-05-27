package de.unileipzig.dbpraktikum.exception;

public class NotAnIntegerException extends ValidationException {
    public NotAnIntegerException(String paramName, String val) {
        super(paramName + " : Value must be a Integer. Got: " + val);
    }
}


