package de.unileipzig.dbpraktikum.exception;

public class NotExistException extends ValidationException {
    public NotExistException(String paramName, String id) {
        super(paramName + " : " + id + " not found in DB.");
    }
}


