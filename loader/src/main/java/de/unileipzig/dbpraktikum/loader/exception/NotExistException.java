package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception typically thrown if a expected object or Entry dfoes not exist. 
 */
public class NotExistException extends ValidationException {
    public NotExistException(String paramName, String id) {
        super(paramName + " : " + id + " not found in DB.");
    }
}


