package de.unileipzig.dbpraktikum.loader.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.unileipzig.dbpraktikum.exception.ValidationException;

public class ErrorLogger {
    private static final Path ERROR_LOG_PATH = Path.of("error-log.txt");
    private static final Map<String, Integer> exceptionCounts = new LinkedHashMap<>();

    public static void clear() {
        try {
            Files.deleteIfExists(ERROR_LOG_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Could not clear error log file", e);
        }
    }

    public static void reportErrors(String name, List<ValidationException> exceptions) {
        if (exceptions.isEmpty()) return;

        StringBuilder builder = new StringBuilder();
        builder.append("ERROR: ").append(name).append(System.lineSeparator());
      
        for (ValidationException e : exceptions) {
            countException(e);
            builder.append(e.getMessage()).append(System.lineSeparator());
        }

        builder.append("================").append(System.lineSeparator());

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

    public static void printSummary() {
        System.out.println();
        System.out.println("ERROR SUMMARY");
        System.out.println("================");

        for (Map.Entry<String, Integer> entry : exceptionCounts.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    private static void countException(ValidationException e) {
        String name = e.getClass().getSimpleName();
        exceptionCounts.merge(name, 1, Integer::sum);
    }
}
