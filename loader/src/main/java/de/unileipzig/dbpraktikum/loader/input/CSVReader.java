package de.unileipzig.dbpraktikum.loader.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

            //Iteratore over each line
            while ((line = reader.readLine()) != null) {
                List<String> result = new ArrayList<>();

                //split at all ',' characters, but not inside quotes
                StringBuilder current = new StringBuilder();
                boolean inQuotes = false;

                //Iterate over each char in line
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);

                    if (c == '"') { //quote found
                        if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') { //this is not an ending quote. Add as normal char. 
                            current.append('"');
                            i++;
                        } else { //this is a starting or ending quote. Do not add to content. 
                            inQuotes = !inQuotes;
                        }
                    } else if (c == ',' && !inQuotes) { //',' found, and not inside quotes, so split here. 
                        result.add(current.toString());
                        current.setLength(0);
                    } else { //normal character to add to result. 
                        current.append(c); 
                    }
                }

                //Add last content column, and add row to records.
                result.add(current.toString());
                records.add(result);
            }

            System.out.println("CSV erfolgreich gelesen. Anzahl Datensätze: " + (records.size() - 1));
            return records;
        } 
    }
}
