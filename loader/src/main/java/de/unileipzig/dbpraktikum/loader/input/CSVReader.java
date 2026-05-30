package de.unileipzig.dbpraktikum.loader.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV Reader class to read a CSV file from a given Path to a List<List<String>> object. 
 * The returned object is a List of CSV Rows, where each Row is a List of Strings. 
 */
public class CSVReader {

    /**
     * Takes a Path to a .csv File, and reads its content into a List of rows, where each row is a List of Strings containing the field content. 
     * ',' is used as the CSV seperator. Furthermore, quotes are correctly identified and seperation only happens outside of them. 
     * @param csvFile Path to the CSV File. 
     * @return List<List<String>> List of Rows, where each Row is a list of Strings containing the row data. 
     * @throws IOException thrown on Fiel read errors. 
     */
    public static List<List<String>> readCsv(Path csvFile) throws IOException {
        System.out.println("Lese CSV-Datei: " + csvFile);

        //Open a BufferedReaderm which can read the file line by line, and char by char. 
        try (BufferedReader reader = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8)) {
            String line;
            List<List<String>> records = new ArrayList<>();

            //Iteratore over each row
            while ((line = reader.readLine()) != null) {
                List<String> result = new ArrayList<>(); //The resulting List for this row. 
                StringBuilder current = new StringBuilder(); //Builds the content for each field of the row. 
                boolean inQuotes = false; //For storing if the current pos is inside or outside quotes. 

                //Iterate over each char in line
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);

                    if (c == '"') { //quote found
                        if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') { //this is not an ending quote. Add as normal char to StringBuild. 
                            current.append('"');
                            i++;
                        } else { //this is a starting or ending quote. Do not add to StringBuild, but toggle quotes. 
                            inQuotes = !inQuotes;
                        }
                    } else if (c == ',' && !inQuotes) { //',' found, and not inside quotes, so split here. 
                        //Splitting is done by reseting the StringBuild and adding the content as a row field. 
                        result.add(current.toString());
                        current.setLength(0);
                    } else { //normal character to add to StringBuild. 
                        current.append(c); 
                    }
                }

                //Add last content column, and add row to record List.
                result.add(current.toString());
                records.add(result);
            }

            //Stat print and return. 
            System.out.println("CSV erfolgreich gelesen. Anzahl Datensätze: " + (records.size() - 1));
            return records;
        } 
    }
}
