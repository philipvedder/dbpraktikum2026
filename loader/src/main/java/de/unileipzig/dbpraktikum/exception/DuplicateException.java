package de.unileipzig.dbpraktikum.exception;

public class DuplicateException extends ValidationException {

    public DuplicateException(String paramName, String id) {
        super("Duplicate " + paramName + " found with ID: " + id);
    }
    
}
