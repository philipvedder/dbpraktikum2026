package de.unileipzig.dbpraktikum.loader.validation;

import java.sql.Date;
import java.util.List;

import de.unileipzig.dbpraktikum.exception.BlankException;
import de.unileipzig.dbpraktikum.exception.ListEmptyException;
import de.unileipzig.dbpraktikum.exception.NotANonNegativeIntegerException;
import de.unileipzig.dbpraktikum.exception.NotAPositiveIntegerException;
import de.unileipzig.dbpraktikum.exception.NotAValidDateFormatException;
import de.unileipzig.dbpraktikum.exception.NotAnDoubleException;
import de.unileipzig.dbpraktikum.exception.NotAnIntegerException;
import de.unileipzig.dbpraktikum.exception.NotAnNonNegativeDoubleException;
import de.unileipzig.dbpraktikum.exception.NullException;
import de.unileipzig.dbpraktikum.exception.StringMaxLengthException;
import de.unileipzig.dbpraktikum.exception.ValidationException;

public class Validator {
    // Validation Methods
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

    protected static <T> T getFirstFromList(List<T> list, String name, List<ValidationException> exceptions) {
        if (list == null || list.size() == 0) {
            if (exceptions != null) exceptions.add(new ListEmptyException(name));
            return null;
        }

        return list.get(0);
    }

    protected static List<String> cleanList(List<String> list) {
        if (list == null) return null;

        List<String> result = list.stream()
                                .filter((s) -> (s != null && !s.isBlank()))
                                .map((s) -> s.trim())
                                .distinct()
                                .toList();

        return result;
    }

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

    protected static Integer requireNonNegativeInt(String s, String name, List<ValidationException> exceptions) {
        Integer result = requireInt(s, name, exceptions);
        if (result == null) return null;

        if (result < 0) {
            if (exceptions != null) exceptions.add(new NotANonNegativeIntegerException(name, s));
            return null;
        }

        return result;
    }

    protected static Integer requirePositiveInt(String s, String name, List<ValidationException> exceptions) {
        Integer result = requireInt(s, name, exceptions);
        if (result == null) return null;

        if (result <= 0) {
            if (exceptions != null) exceptions.add(new NotAPositiveIntegerException(name, s));
            return null;
        }

        return result;
    }

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

    protected static Double requireNonNegativeDouble(String s, String name, List<ValidationException> exceptions) {
        Double result = requireDouble(s, name, exceptions);
        if (result == null) return null;

        if (result < 0) {
            if (exceptions != null) exceptions.add(new NotAnNonNegativeDoubleException(name, s));
        }

        return result;
    }

    protected static <T> T requireNotNull(T o, String name, List<ValidationException> exceptions) {
        if (o == null) {
            if (exceptions != null) exceptions.add(new NullException(name));
        }

        return o;
    }

    protected static String requireNonBlank(String s, String name, List<ValidationException> exceptions) {
        s = requireNotNull(s, name, exceptions);
        if (s == null) return null;

        if (s.isBlank()) {
            if (exceptions != null) exceptions.add(new BlankException(name));
            return null;
        }

        return s.trim();
    }

    protected static String requireStringMaxLength(String s, int numberOfChars, String name, List<ValidationException> exceptions) {
        if (s == null) return null;

        if (s.length() > numberOfChars) {
            if (exceptions != null) exceptions.add(new StringMaxLengthException(name, s, numberOfChars));
            return null;
        }

        return s;
    }
}
