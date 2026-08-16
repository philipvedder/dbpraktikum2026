package de.unileipzig.dbpraktikum.loader.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unileipzig.dbpraktikum.loader.model.raw.ReviewRaw;

/**
 * Parses the raw CSV Elements containing review data to Raw Objects, where every variable is of type String.
 * These are not validated or converted whatsoever, and is only for the first structure of the file. 
 */
public class CSVReviewParser {

    /**
     * Entry method to parse the whole content of a review CSV file. 
     * Finds the order of the review data fields from the header row, and then triggers parsing of each row of the CSV file. 
     * @param records List<List<String>>. List of rows, where each row represents a review and is a list of Strings. 
     * The first row must contain the headers (product, rating, helpful, reviewdate, user, summary, content) in any order. 
     * @return List<ReviewRaw>. List of parsed Reviews. 
     */
    public static List<ReviewRaw> parseCsvRecords(List<List<String>> records) {
        List<ReviewRaw> results = new ArrayList<>();
        //Nothing to do on empty inputs. 
        if (records == null || records.isEmpty()) {
            return results;
        }

        //Build Header->Index Map, to respect positions of the Headers
        List<String> header = records.get(0);
        Map<String, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < header.size(); i++) {
            indexMap.put(header.get(i), i);
        }

        //Skip header line, and parse each row into a raw object
        for (int i = 1; i < records.size(); i++) {
            ReviewRaw res = parseItem(records.get(i), indexMap);
            if (res != null) results.add(res);
        }

        //Stat print
        System.out.println(results.size() + " reviews in CSV verarbeitet");
        return results;
    }

    /**
     * Parses a review data row from a review csv file into a ReviewRaw object. Each data field will be of type String. 
     * @param row List<String>. The row containing the review data. 
     * @param indexMap Map<String, Integer>. Map pointing each column header to a list index, pointing to the corresponding field in the "row" parameter List. 
     * Must contain an index for the headers (product, rating, helpful, reviewdate, user, summary, content).
     * @return ReviewRaw. Object with all information as String type variable. 
     */
    public static ReviewRaw parseItem(List<String> row, Map<String, Integer> indexMap) {
        //Skip empty rows and rows with incorrect value count. These are invalid rows and will not be read. 
        if (row == null || row.isEmpty() || row.size() != indexMap.size()) {
            return null;
        }

        //Return the ReviewRaw object, with each field cleaned.
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

    /**
     * Takes a String value and "cleans" it. 
     * "Cleaning" refers to 
     * - setting it null if its empty
     * - trimming it
     * @param value String to clean.
     * @return cleaned String object. 
     */
    private static String clean(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
