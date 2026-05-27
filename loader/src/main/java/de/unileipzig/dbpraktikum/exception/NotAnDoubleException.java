package de.unileipzig.dbpraktikum.exception;

public class NotAnDoubleException extends ValidationException {
    public NotAnDoubleException(String paramName, String val) {
        super(paramName + " : Value must be a floating point number. Got: " + val);
    }
}


