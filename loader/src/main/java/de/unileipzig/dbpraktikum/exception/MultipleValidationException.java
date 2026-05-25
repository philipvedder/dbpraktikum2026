package de.unileipzig.dbpraktikum.exception;

import java.util.List;

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


