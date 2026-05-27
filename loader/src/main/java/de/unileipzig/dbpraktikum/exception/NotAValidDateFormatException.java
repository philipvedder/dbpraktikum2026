package de.unileipzig.dbpraktikum.exception;

public class NotAValidDateFormatException extends ValidationException {
    public NotAValidDateFormatException(String paramName, String val) {
        super(paramName + " : Value must be in valid Date format yyyy-[m]m-[d]d. Got: " + val);
    }
}


