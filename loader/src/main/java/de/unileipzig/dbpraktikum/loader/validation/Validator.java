package de.unileipzig.dbpraktikum.loader.validation;

import java.sql.Date;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.exception.*;

/**
 * Abstract Validator class providing general validation methods. 
 * All these methods are following an equal logic: If validation fails, return null, and add the corresponding exception to the exceptions list.
 * These are not thrown to prevent the validation to end on the first error. We want to collect and report all errors on every object. 
 */
public abstract class Validator {
    /**
     * Converts a String to a Date if possible. Otherwise, adds NotAValidDateFormatException to exceptions.
     * Adds BlankException if input String is Blank.
     * Adds NullException if input String is NULL. 
     * @param s String to validate
     * @param name String with name of Parameter, which is getting checked. This is only for Error reporting.
     * @param exceptions List<ValidationException>, where exceptions will be added. 
     * @return Valid Date or NULL
     */
    protected static Date requireDate(String s, String name, List<ValidationException> exceptions) {
        s = requireNonBlank(s, name, exceptions);
        if (s == null) return null;

        Date result = null;

        try {
            result = Date.valueOf(s);
        } catch (IllegalArgumentException e) {
            if (exceptions != null) exceptions.add(new NotAValidDateFormatException(name, s));
            return null;
        }

        return result;
    }

    /**
     * Returns the first Element of a List<T>. Otherwise, adds ListEmptyException to exceptions.
     * @param list list to get first item from
     * @param name String with name of Parameter, which is getting checked. This is only for Error reporting.
     * @param exceptions List<ValidationException>, where exceptions will be added. 
     * @return Valid Element of Type T (Type of input List), or NULL
     */
    protected static <T> T getFirstFromList(List<T> list, String name, List<ValidationException> exceptions) {
        if (list == null || list.size() == 0) {
            if (exceptions != null) exceptions.add(new ListEmptyException(name));
            return null;
        }

        return list.get(0);
    }

    /**
     * Returns a cleaned List<String> object. Cleaning includes:
     * - Removal of distinct objects
     * - String Trimming
     * - Removal of NULL and Blank Strings
     * @param list list to clean
     * @return Cleaned up List
     */
    protected static List<String> cleanList(List<String> list) {
        if (list == null) return null;

        List<String> result = list.stream()
                                .filter((s) -> (s != null && !s.isBlank()))
                                .map((s) -> s.trim())
                                .distinct()
                                .toList();

        return result;
    }

    /**
     * Converts a String to a Integer if possible. 
     * Otherwise, adds NotAnIntegerException to exceptions.
     * Adds BlankException if String is Blank
     * Adds NullException if String is NULL
     * @param s String to validate
     * @param name String with name of Parameter, which is getting checked. This is only for Error reporting.
     * @param exceptions List<ValidationException>, where exceptions will be added. 
     * @return Valid Integer or NULL
     */
    protected static Integer requireInt(String s, String name, List<ValidationException> exceptions) {
        s = requireNonBlank(s, name, exceptions);
        if (s == null) return null;

        Integer result = null;

        try {
            result = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            if (exceptions != null) exceptions.add(new NotAnIntegerException(name, s));
            return null;
        }

        return result;
    }

    /**
     * Converts a String to a Non-Negative Integer if possible. 
     * Otherwise, adds NotANonNegativeIntegerException to exceptions. 
     * Adds NotAnIntegerException if String is no Integer.
     * Adds BlankException if String is Blank
     * Adds NullException if String is NULL
     * @param s String to validate
     * @param name String with name of Parameter, which is getting checked. This is only for Error reporting.
     * @param exceptions List<ValidationException>, where exceptions will be added. 
     * @return Valid Integer >= 0 or NULL
     */
    protected static Integer requireNonNegativeInt(String s, String name, List<ValidationException> exceptions) {
        Integer result = requireInt(s, name, exceptions);
        if (result == null) return null;

        if (result < 0) {
            if (exceptions != null) exceptions.add(new NotANonNegativeIntegerException(name, s));
            return null;
        }

        return result;
    }

    /**
     * Converts a String to a Positive Integer if possible. 
     * Otherwise, adds NotAPositiveIntegerException to exceptions. 
     * Adds NotAnIntegerException if String is no Integer.
     * Adds BlankException if String is Blank
     * Adds NullException if String is NULL
     * @param s String to validate
     * @param name String with name of Parameter, which is getting checked. This is only for Error reporting.
     * @param exceptions List<ValidationException>, where exceptions will be added. 
     * @return Valid Integer >0 or NULL
     */
    protected static Integer requirePositiveInt(String s, String name, List<ValidationException> exceptions) {
        Integer result = requireInt(s, name, exceptions);
        if (result == null) return null;

        if (result <= 0) {
            if (exceptions != null) exceptions.add(new NotAPositiveIntegerException(name, s));
            return null;
        }

        return result;
    }

    /**
     * Converts a String to a Integer if possible and checks if it inbetween given bounds. 
     * Specifically, it requires lowerBound < givenInt < upperBound, some same value as bound is allowed. 
     * Otherwise, adds IntegerNotInBoundsException to exceptions. 
     * Adds NotAnIntegerException if String is no Integer.
     * Adds BlankException if String is Blank
     * Adds NullException if String is NULL
     * @param s String to validate
     * @param lowerBound Integer. Lower bound for Value check
     * @param upperBound Integer. Upper bound for Value check
     * @param name String with name of Parameter, which is getting checked. This is only for Error reporting.
     * @param exceptions List<ValidationException>, where exceptions will be added. 
     * @return Valid Integer >0 or NULL
     */
    protected static Integer requireIntBetween(String s, Integer lowerBound, Integer upperBound, String name, List<ValidationException> exceptions) {
        Integer result = requireInt(s, name, exceptions);
        if (result == null) return null;

        if (result < lowerBound || result > upperBound) {
            if (exceptions != null) exceptions.add(new IntegerNotInBoundsException(name, lowerBound, upperBound, s));
            return null;
        }

        return result;
    }

    /**
     * Converts a String to a Double if possible. 
     * Otherwise, adds NotAnDoubleException to exceptions.
     * Adds BlankException if String is Blank
     * Adds NullException if String is NULL
     * @param s String to validate
     * @param name String with name of Parameter, which is getting checked. This is only for Error reporting.
     * @param exceptions List<ValidationException>, where exceptions will be added. 
     * @return Valid Double or NULL
     */
    protected static Double requireDouble(String s, String name, List<ValidationException> exceptions) {
        s = requireNonBlank(s, name, exceptions);
        Double result = null;

        try {
            result = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            if (exceptions != null) exceptions.add(new NotAnDoubleException(name, s));
            return null;
        }

        return result;
    }

    /**
     * Converts a String to a Non-Negative Double if possible. 
     * Otherwise, adds NotAnNonNegativeDoubleException to exceptions.
     * Adds NotAnDoubleException if String is not a Double
     * Adds BlankException if String is Blank
     * Adds NullException if String is NULL
     * @param s String to validate
     * @param name String with name of Parameter, which is getting checked. This is only for Error reporting.
     * @param exceptions List<ValidationException>, where exceptions will be added. 
     * @return Valid Double >= 0 or NULL
     */
    protected static Double requireNonNegativeDouble(String s, String name, List<ValidationException> exceptions) {
        Double result = requireDouble(s, name, exceptions);
        if (result == null) return null;

        if (result < 0) {
            if (exceptions != null) exceptions.add(new NotAnNonNegativeDoubleException(name, s));
        }

        return result;
    }

    /**
     * Checks that the Input object is not null. 
     * Otherwise, adds NullException to exceptions.
     * @param o Object to validate
     * @param name String with name of Parameter, which is getting checked. This is only for Error reporting.
     * @param exceptions List<ValidationException>, where exceptions will be added. 
     * @return The input object
     */
    protected static <T> T requireNotNull(T o, String name, List<ValidationException> exceptions) {
        if (o == null) {
            if (exceptions != null) exceptions.add(new NullException(name));
        }

        return o;
    }

    /**
     * Checks that the Input String is not Blank. 
     * Otherwise, adds BlankException to exceptions.
     * Adds NullException if input String is null.
     * @param o String to validate
     * @param name String with name of Parameter, which is getting checked. This is only for Error reporting.
     * @param exceptions List<ValidationException>, where exceptions will be added. 
     * @return Non-Blank String or NULL
     */
    protected static String requireNonBlank(String s, String name, List<ValidationException> exceptions) {
        s = requireNotNull(s, name, exceptions);
        if (s == null) return null;

        if (s.isBlank()) {
            if (exceptions != null) exceptions.add(new BlankException(name));
            return null;
        }

        return s.trim();
    }

    /**
     * Checks that the Input String is not longer that numberOfChars. 
     * Otherwise, adds StringMaxLengthException to exceptions.
     * @param o String to validate
     * @param numberOfChars Maximum alowed number of characters
     * @param name String with name of Parameter, which is getting checked. This is only for Error reporting.
     * @param exceptions List<ValidationException>, where exceptions will be added. 
     * @return Input String or NULL if too long. 
     */
    protected static String requireStringMaxLength(String s, int numberOfChars, String name, List<ValidationException> exceptions) {
        if (s == null) return null;

        if (s.length() > numberOfChars) {
            if (exceptions != null) exceptions.add(new StringMaxLengthException(name, s, numberOfChars));
            return null;
        }

        return s;
    }
}
