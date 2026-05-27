package de.unileipzig.dbpraktikum.loader.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.unileipzig.dbpraktikum.loader.exception.ValidationException;

/**
 * Error logging class. 
 * Saves information from lists of ValidationException to a text file. 
 * Also can print a Summary of all Exceptions that occured. 
 */
public class ErrorLogger {
    //Path to output file
    private static final Path ERROR_LOG_PATH = Path.of("error-log.txt");

    //Counter Map for Error Summary
    private static final Map<String, Integer> exceptionCounts = new LinkedHashMap<>();

    /**
     * Deletes any existent error log file. 
     * @throws RuntimeException if File could not be deleted. 
     */
    public static void clear() {
        try {
            Files.deleteIfExists(ERROR_LOG_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Could not clear error log file", e);
        }
    }

    /**
     * Report a list of ValidationExceptions to the error log file. 
     * Accepts a name, under which the given Exceptions will be reported. This can be a Product ID + Type for example. 
     * @param name String name of the element, from which the ValidationExceptions originate. 
     * @param exceptions List<ValidationException> Exceptions to report to file.
     */
    public static void reportErrors(String name, List<ValidationException> exceptions) {
        if (exceptions.isEmpty()) return; //Nothing to do

        //StringBuilder for easy construction
        StringBuilder builder = new StringBuilder();
        //Add new Error entry for name. 
        builder.append("ERROR: ").append(name).append(System.lineSeparator());
      
        //Print all Exceptions, and also trigger error counting. 
        for (ValidationException e : exceptions) {
            countException(e);
            builder.append(e.getMessage()).append(System.lineSeparator());
        }

        //Add a seperator
        builder.append("================").append(System.lineSeparator());

        //Append to file if exists, otherwise create and append. 
        try {
            Files.writeString(
                    ERROR_LOG_PATH,
                    builder.toString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException("Could not write error log file", e);
        }
    }

    /**
     * Print a Summary of which ValidationExceptions occured how often to stdOut
     */
    public static void printSummary() {
        System.out.println();
        System.out.println("ERROR SUMMARY");
        System.out.println("================");

        for (Map.Entry<String, Integer> entry : exceptionCounts.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    /**
     * Adds a single ValidationExceptions to the error count summary. 
     * @param e ValidationException to count. 
     */
    private static void countException(ValidationException e) {
        String name = e.getClass().getSimpleName();
        exceptionCounts.merge(name, 1, Integer::sum);
    }
}
