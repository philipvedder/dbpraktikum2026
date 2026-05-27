package de.unileipzig.dbpraktikum.loader.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * CSV Reader class to read a CSV file from a given Path to a List<List<String>> object. 
 * The returned object is a List of CSV Rows, where each Row is a List of Strings. 
 */
public class CSVReader {
    public static List<List<String>> readCsv(Path csvFile) throws NoSuchElementException, IOException {
        System.out.println("Lese CSV-Datei: " + csvFile);

         try (BufferedReader reader = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8)) {
            String line;
            List<List<String>> records = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }

            //System.out.println("CSV Header:");
            //records.get(0).forEach(header -> System.out.println("  - " + header));

            //Debugging stuff...
            // System.out.println();
            // System.out.println("Datensätze:");

            // long count = 0;

            // for (List<String> record : records) {
            //     if (count == 0) { // Skip Header line
            //         count++;
            //         continue;
            //     }
            //     System.out.println("Record #" + count++);

            //     for (String val : record) {
            //         System.out.printf("  %s", val);
            //     }

            //     System.out.println();
            // }

            System.out.println("CSV erfolgreich gelesen. Anzahl Datensätze: " + (records.size() - 1));
            return records;
        } 
    }
}
