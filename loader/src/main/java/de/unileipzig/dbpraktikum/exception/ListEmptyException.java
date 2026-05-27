package de.unileipzig.dbpraktikum.exception;

public class ListEmptyException extends ValidationException {
    public ListEmptyException(String paramName) {
        super(paramName + " : List must not be empty");
    }
}


