package de.unileipzig.dbpraktikum.loader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;

import de.unileipzig.dbpraktikum.loader.db.DB;
import de.unileipzig.dbpraktikum.loader.db.service.CategoriesImportService;
import de.unileipzig.dbpraktikum.loader.db.service.ShopImportService;
import de.unileipzig.dbpraktikum.loader.input.CSVReader;
import de.unileipzig.dbpraktikum.loader.input.XMLReader;
import de.unileipzig.dbpraktikum.loader.logger.ErrorLogger;
import de.unileipzig.dbpraktikum.loader.model.Shop;
import de.unileipzig.dbpraktikum.loader.model.Category;
import de.unileipzig.dbpraktikum.loader.model.raw.ShopRaw;
import de.unileipzig.dbpraktikum.loader.parser.XMLCategoryParser;
import de.unileipzig.dbpraktikum.loader.parser.XMLShopParser;
import de.unileipzig.dbpraktikum.loader.validation.CategoryValidator;
import de.unileipzig.dbpraktikum.loader.validation.ShopValidator;

public class MediaStoreLoader {

    private static void loadCategoryData(Element rootElement) {
        List<Category> rawCategories = XMLCategoryParser.parseXmlRoot(rootElement);
        List<Category> valCategories = CategoryValidator.validateAll(rawCategories);

        DB db = new DB();
        CategoriesImportService categoriesImportService = new CategoriesImportService();

        try (Connection connection = db.openConnection()) {
            categoriesImportService.importCategories(connection, valCategories);
        } catch (Exception ex) {
            System.out.println("ERROR: Error while establishing SQL Connection.");
            System.out.println(ex.getMessage());
        }
    }

    private static void loadShopData(Element rootElement) {
        ShopRaw shopRaw = XMLShopParser.parseXmlRoot(rootElement);
        Shop shopVal = ShopValidator.validate(shopRaw);
        
        DB db = new DB();
        ShopImportService shopImportService = new ShopImportService();

        try (Connection connection = db.openConnection()) {
            shopImportService.importShop(connection, shopVal);
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

        ErrorLogger.clear();

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

        ErrorLogger.printSummary();
    }
}
