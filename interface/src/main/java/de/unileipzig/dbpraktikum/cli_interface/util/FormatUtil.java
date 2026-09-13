package de.unileipzig.dbpraktikum.cli_interface.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class FormatUtil {
    /**
     * Format a BigDecimal as String with two nunbers after the comma, or "-" if null
     * @param d BigDecimal
     * @return the formatted String
     */
    public static String formatDecimal(BigDecimal d) {
        if (d == null) {
            return "-";
        }

        return String.format("%.2f", d);
    }

    /**
     * Truncats a String to a given length
     * @param s String to truncate
     * @param length max length
     * @return truncated String
     */
    public static String trunc(String s, int length) {
        return s.substring(0, Math.min(length, s.length()));
    }    

    /**
     * Format a integer as String. "-" if Null
     * @param i Integer to format
     * @return formatted String
     */
    public static String formatInt(Integer i) {
        if (i == null) {
            return "-";
        }

        return i.toString();
    }

    /**
     * Format a TimeStamp as a Date String
     * @param t Timestamp to format
     * @return Formatted String
     */
    public static String formatDate(Timestamp t) {
        return new SimpleDateFormat("dd.MM.yyyy").format(t);
    }

    /**
     * Format a TimeStamp as a Date-and-time String
     * @param t Timestamp to format
     * @return Formatted String
     */
    public static String formatDateAndTime(Timestamp t) {
        return new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(t);
    }
}
