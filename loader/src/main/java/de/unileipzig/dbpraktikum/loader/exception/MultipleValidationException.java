package de.unileipzig.dbpraktikum.loader.exception;

import java.util.List;

/**
 * Validator Exception Wrapper, which allows one to throw a List of ValidatorExceptions. 
 */
public class MultipleValidationException extends ValidationException {
    private List<ValidationException> exceptions;

    public MultipleValidationException(List<ValidationException> exceptions) {
        super("Multiple Validation Exceptions");
        this.exceptions = exceptions;
    }

    public List<ValidationException> getExceptions() {
        return exceptions;
    }
}


