package de.unileipzig.dbpraktikum.exception;

public class NullException extends ValidationException {
    public NullException(String paramName) {
        super(paramName + " : Value must not be NULL");
    }
}


