package de.unileipzig.dbpraktikum.loader.exception;

/**
 * Validator Exception typically thrown if a Date object was not supposed to lie in the fututre, but does. 
 */
public class DateInFutureException extends ValidationException {
    public DateInFutureException(String paramName, String val) {
        super(paramName + " : Date must not lie in the future. Got: " + val);
    }
}


