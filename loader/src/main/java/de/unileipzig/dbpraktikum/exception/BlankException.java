package de.unileipzig.dbpraktikum.exception;

public class BlankException extends ValidationException {
    public BlankException(String paramName) {
        super(paramName + " : Value must not be empty");
    }
}


