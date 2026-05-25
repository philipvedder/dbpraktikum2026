package de.unileipzig.dbpraktikum.exception;

public class DuplicateException extends ValidationException {

    public DuplicateException(String id) {
        super("Duplicate found with ID: " + id);
    }
    
}
