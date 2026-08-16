package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception for Exceptions which are unexpected, and cannot be specified. 
 */
public class UnknownException extends ValidationException {
    public UnknownException(String id, String msg) {
        super("Unknown Exception on Product with id " + id + ":\n" + msg);
    }
}


