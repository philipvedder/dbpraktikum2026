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
import de.unileipzig.dbpraktikum.loader.db.service.ReviewsImportService;
import de.unileipzig.dbpraktikum.loader.model.Review;
import de.unileipzig.dbpraktikum.loader.model.raw.ReviewRaw;
import de.unileipzig.dbpraktikum.loader.parser.CSVReviewParser;
import de.unileipzig.dbpraktikum.loader.validation.ReviewValidator;
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


/**
 * Main Class of Media Store XML and CSV Loader. 
 * 
 * Run with from /loader directory with
 * mvn exec:java -Dexec.mainClass=de.unileipzig.dbpraktikum.loader.MediaStoreLoader -Dexec.args="../data/FILEPATH"
 */
public class MediaStoreLoader {

    /**
     * Loading procedure for <categories> XML Files
     * @param rootElement <categories> XML root element
     */
    private static void loadCategoryData(Element rootElement) {
        //Read file content to objects. Here, every filetype is still a String, and nothing is validated.
        List<Category> rawCategories = XMLCategoryParser.parseXmlRoot(rootElement);
        //Validate to typed Objects.
        List<Category> valCategories = CategoryValidator.validateAll(rawCategories);

        //Initialize DB Connection and Import Service
        DB db = new DB();
        CategoriesImportService categoriesImportService = new CategoriesImportService();

        //Establish DB connection and insert all new Elements
        try (Connection connection = db.openConnection()) {
            categoriesImportService.importCategories(connection, valCategories);
        } catch (Exception ex) {
            System.out.println("ERROR: Error while establishing SQL Connection.");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Loading procedure for <shop> XML Files
     * @param rootElement <shop ...> XML root element
     */
    private static void loadShopData(Element rootElement) {
        //Read file content to objects. Here, every filetype is still a String, and nothing is validated.
        ShopRaw shopRaw = XMLShopParser.parseXmlRoot(rootElement);
        //Validate to typed Objects.
        Shop shopVal = ShopValidator.validate(shopRaw);
        
        //Initialize DB Connection and Import Service
        DB db = new DB();
        ShopImportService shopImportService = new ShopImportService();

        //Establish DB connection and insert all new Elements
        try (Connection connection = db.openConnection()) {
            shopImportService.importShop(connection, shopVal);
        } catch (Exception ex) {
            System.out.println("ERROR: Error while establishing SQL Connection.");
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Loading procedure for review CSV files
     * @param inputFile path to CSV file
     */
    private static void loadReviewData(Path inputFile) throws Exception {
        //Read CSV file
        List<List<String>> csvRecords = CSVReader.readCsv(inputFile);
        if (csvRecords.size() == 0) return;

        //Read file content to objects. Here, every filetype is still a String, and nothing is validated.
        List<ReviewRaw> rawReviews = CSVReviewParser.parseCsvRecords(csvRecords);

        //Validate to typed Objects.
        List<Review> valReviews = ReviewValidator.validateAll(rawReviews);

        //Initialize DB Connection and Import Service
        DB db = new DB();
        ReviewsImportService reviewsImportService = new ReviewsImportService();

        //Establish DB connection and insert all new Elements
        try (Connection connection = db.openConnection()) {
            reviewsImportService.importReviews(connection, valReviews);
        } catch (Exception ex) {
            System.out.println("ERROR: Error while establishing SQL Connection.");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Entry method for Loader. Checks if input file exists and then calls specific CSV or XML methods on it.  
     * @param args Index 0: Path to XML or CSV File
     */
    public static void main(String[] args) {
        //Check if Path it given
        if (args.length != 1) {
            System.out.println("ERROR: Please execute with a filepath to a CSV or XML file as first argument.");
            System.exit(1);
        }
        Path inputFile = Path.of(args[0]);

        //Clear Error TXT
        ErrorLogger.clear();

        //Check if File is Valid
        if (!Files.isRegularFile(inputFile)) {
            System.err.println("ERROR: File does not exist or is not a regular file: " + inputFile);
            System.exit(1);
        }

        String fileName = inputFile.getFileName().toString().toLowerCase(Locale.ROOT);

        //Load specific methods on File, depending on Filename and input type
        try {
            if (fileName.endsWith(".csv")) {
                loadReviewData(inputFile);

            } else if (fileName.endsWith(".xml")) {
                //Read XML File
                Element rootElement = XMLReader.readXmlFile(inputFile);

                //Call loader method, depending on rootElement TagName. 
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

        //Print Error summary
        ErrorLogger.printSummary();
    }
}
