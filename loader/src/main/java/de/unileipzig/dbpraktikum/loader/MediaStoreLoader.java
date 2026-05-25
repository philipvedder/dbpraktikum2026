package de.unileipzig.dbpraktikum.loader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;

import de.unileipzig.dbpraktikum.loader.db.DB;
import de.unileipzig.dbpraktikum.loader.db.service.ProductImportService;
import de.unileipzig.dbpraktikum.loader.input.CSVReader;
import de.unileipzig.dbpraktikum.loader.input.XMLReader;
import de.unileipzig.dbpraktikum.loader.model.Product;
import de.unileipzig.dbpraktikum.loader.model.Category;
import de.unileipzig.dbpraktikum.loader.model.raw.ProductRaw;
import de.unileipzig.dbpraktikum.loader.parser.XMLCategoryParser;
import de.unileipzig.dbpraktikum.loader.parser.XMLShopItemParser;
import de.unileipzig.dbpraktikum.loader.validation.CategoryValidator;
import de.unileipzig.dbpraktikum.loader.validation.ProductValidator;

public class MediaStoreLoader {

    private static void loadCategoryData(Element rootElement) {
        List<Category> rawCategories = XMLCategoryParser.parseXmlRoot(rootElement);
        List<Category> valCategories = CategoryValidator.validateAll(rawCategories);
    }

    private static void loadShopData(Element rootElement) {
        List<ProductRaw> rawProducts = XMLShopItemParser.parseXmlRoot(rootElement);
        List<Product> valProducts = ProductValidator.validateAll(rawProducts);
        
        DB db = new DB();
        ProductImportService productImportService = new ProductImportService();

        try (Connection connection = db.openConnection()) {
            productImportService.importAll(connection, valProducts);
        } catch (Exception ex) {
            System.out.println("ERROR: Error while establishing SQL Connection.");
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("ERROR: Please execute with a filepath to a CSV or XML file as first argument.");
            System.exit(1);
        }

        Path inputFile = Path.of(args[0]);

        if (!Files.isRegularFile(inputFile)) {
            System.err.println("ERROR: File does not exist or is not a regular file: " + inputFile);
            System.exit(1);
        }

        String fileName = inputFile.getFileName().toString().toLowerCase(Locale.ROOT);

        try {
            if (fileName.endsWith(".csv")) {
                CSVReader.readCsv(inputFile);

            } else if (fileName.endsWith(".xml")) {
                Element rootElement = XMLReader.readXmlFile(inputFile);

                switch (rootElement.getTagName()) {
                    case "shop":
                        loadShopData(rootElement);
                        break;
                
                    case "categories":
                        loadCategoryData(rootElement);
                        break;

                    default:
                        break;
                }

            } else {
                System.err.println("ERROR: Filetype not supported. Required: .csv or .xml");
                System.exit(1);
            }
        } catch (Exception ex) {
            System.err.println("ERROR: Could not read file.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }
}
