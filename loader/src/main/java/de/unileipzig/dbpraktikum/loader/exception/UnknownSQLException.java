package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception for Exceptions during SQL execution. 
 */
public class UnknownSQLException extends ValidationException {
    public UnknownSQLException(String id, String msg) {
        super("Unknown SQL Exception on Product with id " + id + ":\n" + msg);
    }
}


