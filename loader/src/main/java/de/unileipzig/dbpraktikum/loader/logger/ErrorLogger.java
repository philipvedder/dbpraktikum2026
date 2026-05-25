package de.unileipzig.dbpraktikum.loader.logger;

import java.util.List;

import de.unileipzig.dbpraktikum.exception.ValidationException;
import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;

public class ErrorLogger {
    public static void reportErrors(String name, ProductType type, List<ValidationException> exceptions) {
        if (exceptions.isEmpty()) return;

        System.out.println("Error: " + type.name() + " - " + name);
        for (ValidationException e : exceptions) {
            System.out.println(e.getMessage());
        }
        System.out.println("================");
    }
}
