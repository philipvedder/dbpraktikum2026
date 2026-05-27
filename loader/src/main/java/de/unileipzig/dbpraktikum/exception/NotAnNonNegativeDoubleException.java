package de.unileipzig.dbpraktikum.exception;

public class NotAnNonNegativeDoubleException extends ValidationException {
    public NotAnNonNegativeDoubleException(String paramName, String val) {
        super(paramName + " : Value must be > 0.0. Got: " + val);
    }
}


