package de.unileipzig.dbpraktikum.cli_interface.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class FormatUtil {
    public static String formatDecimal(BigDecimal d) {
        if (d == null) {
            return "-";
        }

        return String.format("%.2f", d);
    }

    public static String trunc(String s, int length) {
        return s.substring(0, Math.min(length, s.length()));
    }    

    public static String formatInt(Integer i) {
        if (i == null) {
            return "-";
        }

        return i.toString();
    }

    public static String formatDate(Timestamp t) {
        return new SimpleDateFormat("dd.MM.yyyy").format(t);
    }

    public static String formatDateAndTime(Timestamp t) {
        return new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(t);
    }
}
