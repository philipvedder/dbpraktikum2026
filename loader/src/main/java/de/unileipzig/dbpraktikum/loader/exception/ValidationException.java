package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Superclass of all specific ValidationExceptions. 
 * Is not different from a basic Exception. 
 */
public abstract class ValidationException extends Exception {
    public ValidationException(String msg) {
        super(msg);
    }
}


