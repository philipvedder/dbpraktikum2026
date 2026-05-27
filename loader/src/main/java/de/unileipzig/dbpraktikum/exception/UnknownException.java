package de.unileipzig.dbpraktikum.exception;

public class UnknownException extends ValidationException {
    public UnknownException(String id, String msg) {
        super("Unknown Exception on Product with id " + id + ":\n" + msg);
    }
}


