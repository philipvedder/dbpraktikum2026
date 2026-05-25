package de.unileipzig.dbpraktikum.exception;

public class StringMaxLengthException extends ValidationException {
    public StringMaxLengthException(String paramName, String val, int numberOfChars) {
        super(paramName + " : Value must not have more than " + numberOfChars + " characters. Has: " + val.length());
    }
}
