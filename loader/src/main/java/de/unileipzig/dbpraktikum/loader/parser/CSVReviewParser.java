package de.unileipzig.dbpraktikum.loader.parser;

import java.util.ArrayList;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.raw.ReviewRaw;

public class CSVReviewParser {

    public static List<ReviewRaw> parseCsvRecords(List<List<String>> records) {
        List<ReviewRaw> results = new ArrayList<>();

        if (records == null || records.isEmpty()) {
            return results;
        }

        // skip header line
        for (int i = 1; i < records.size(); i++) {
            ReviewRaw res = parseItem(records.get(i));
            if (res != null) results.add(res);
        }

        System.out.println(results.size() + " reviews in CSV verarbeitet");
        return results;
    }

    public static ReviewRaw parseItem(List<String> row) {
        if (row == null || row.isEmpty()) {
            return null;
        }

        return new ReviewRaw(
            getValue(row, 0),        // product
            getValue(row, 1),        // rating
            getValue(row, 2),        // helpful
            getValue(row, 3),        // reviewdate
            getValue(row, 4),        // user
            getValue(row, 5),        // summary
            getContentValue(row, 6)  // content
        );
    }

    private static String getValue(List<String> row, int index) {
        if (index >= row.size()) {
            return "";
        }

        return clean(row.get(index));
    }

    private static String getContentValue(List<String> row, int startIndex) {
        if (startIndex >= row.size()) {
            return "";
        }

        return clean(String.join(",", row.subList(startIndex, row.size())));
    }

    private static String clean(String value) {
        if (value == null) {
            return "";
        }

        String cleaned = value.trim();

        if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length() >= 2) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        return cleaned;
    }
}
