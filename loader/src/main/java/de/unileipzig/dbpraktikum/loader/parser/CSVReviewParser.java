package de.unileipzig.dbpraktikum.loader.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unileipzig.dbpraktikum.loader.model.raw.ReviewRaw;

public class CSVReviewParser {

    public static List<ReviewRaw> parseCsvRecords(List<List<String>> records) {
        List<ReviewRaw> results = new ArrayList<>();

        if (records == null || records.isEmpty()) {
            return results;
        }

        // Build Header->Index Map, to respect positions of the Headers
        List<String> header = records.get(0);
        Map<String, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < header.size(); i++) {
            indexMap.put(header.get(i), i);
        }

        // Skip header line, and parse each row into a raw object
        for (int i = 1; i < records.size(); i++) {
            ReviewRaw res = parseItem(records.get(i), indexMap);
            if (res != null) results.add(res);
        }

        System.out.println(results.size() + " reviews in CSV verarbeitet");
        return results;
    }

    public static ReviewRaw parseItem(List<String> row, Map<String, Integer> indexMap) {
        if (row == null || row.isEmpty() || row.size() != indexMap.size()) {
            System.out.println(row);
            return null;
        }

        return new ReviewRaw(
            clean(row.get(indexMap.get("product"))),
            clean(row.get(indexMap.get("rating"))),
            clean(row.get(indexMap.get("helpful"))),
            clean(row.get(indexMap.get("reviewdate"))),
            clean(row.get(indexMap.get("user"))),
            clean(row.get(indexMap.get("summary"))),
            clean(row.get(indexMap.get("content")))
        );
    }

    private static String clean(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
