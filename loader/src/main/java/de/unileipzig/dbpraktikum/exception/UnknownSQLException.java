package de.unileipzig.dbpraktikum.exception;

public class UnknownSQLException extends ValidationException {
    public UnknownSQLException(String id, String msg) {
        super("Unknown SQL Exception on Product with id " + id + ":\n" + msg);
    }
}


