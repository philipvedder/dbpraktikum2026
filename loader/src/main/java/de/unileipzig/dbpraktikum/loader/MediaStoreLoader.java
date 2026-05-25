package de.unileipzig.dbpraktikum.loader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
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
            for (Product product : valProducts) {
                try {
                    productImportService.importProduct(connection, product);
                } catch (SQLException ex) {
                    System.out.println(ex);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Bitte aufrufen mit einem Dateipfad zu einer XML oder CSV Datei als Argument.");
            System.exit(1);
        }

        Path inputFile = Path.of(args[0]);

        if (!Files.isRegularFile(inputFile)) {
            System.err.println("ERROR: Datei existiert nicht oder ist keine reguläre Datei: " + inputFile);
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
                System.err.println("ERROR: Nicht unterstütztes Dateiformat. Erwartet: .csv oder .xml");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("ERROR: Datei konnte nicht gelesen werden.");
            System.err.println("Grund: " + e.getMessage());
            System.exit(1);
        }
    }
}
