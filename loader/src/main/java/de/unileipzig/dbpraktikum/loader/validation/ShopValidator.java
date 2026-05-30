package de.unileipzig.dbpraktikum.loader.validation;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.exception.*;
import de.unileipzig.dbpraktikum.loader.logger.ErrorLogger;
import de.unileipzig.dbpraktikum.loader.model.*;
import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;
import de.unileipzig.dbpraktikum.loader.model.raw.*;

/**
 * Validator class for Shop XML content. 
 * Checks and converts RAW objects into correctly-typed objects. 
 */
public class ShopValidator extends Validator {
    /**
     * Validates the input shop object by validating and converting all its variables.
     * Exits the Program early if the shop information is already invalid. 
     * Otherwise, validates all included products, and logs Errors which occur on any product. 
     * @param shop ShopRaw input, where all variabels are of Type String. 
     * @return Shop object with correct types.
     */
    public static Shop validate(ShopRaw shop) {
        System.out.println("Validating Shop with " + shop.products().size() + " products...");

        //Shop object Validation
        List<ValidationException> shopExceptions = new ArrayList<>(); //List of all Exceptions which occur during the validation.
        String name = requireNonBlank(shop.name(), "name", shopExceptions);
        name = requireStringMaxLength(name, 256, "name", shopExceptions);
        String street = requireNonBlank(shop.street(), "street", shopExceptions);
        street = requireStringMaxLength(street, 512, "street", shopExceptions);
        String zip = requireNonBlank(shop.zip(), "zip", shopExceptions);
        zip = requireStringMaxLength(zip, 16, "zip", shopExceptions);

        //Exit early if shop object is not valid
        if (!shopExceptions.isEmpty()) {
            ErrorLogger.reportErrors("Shop " + shop.name(), shopExceptions);
            System.out.println("Shop encoding invalid. Stopping early.");
            System.exit(1);
        }

        //Product objects Validation
        List<Product> productResults = new ArrayList<>();
        int invalidCounter = 0;

        for (ProductRaw productRaw : shop.products()) {
            try {
                Product p = validateProduct(productRaw);
                if (p != null)
                    productResults.add(p);
                
            } catch (MultipleValidationException e) {
                //Log all Errors that occur for each product
                ErrorLogger.reportErrors(productRaw.getAsin() + " - " + productRaw.getType().name(), e.getExceptions());
                invalidCounter++;
            }
        }

        //Result
        System.out.println(shop.products().size() - invalidCounter + " valid Products");
        System.out.println(invalidCounter + " invalid Products");
        return new Shop(name, street, zip, productResults);
    }

    /**
     * Validates the input ProductRaw object by validating and converting all its variables.
     * We allow null values for the image URL, the salesrank, and the price.
     * Throws if any ValidationErrors occur on the Product or its content.  
     * @param p ProductRaw input, where all variabels are of Type String. 
     * @return Object of Subtype of Product with correct Types and validated. 
     * @throws MultipleValidationException, if any Validation threw an error. MultipleValidationException contains a list of all ValidationExceptions that occured on this Product.
     */
    private static Product validateProduct(ProductRaw p) throws MultipleValidationException {
        if (p == null) return null;
        List<ValidationException> exceptions = new ArrayList<>(); //List of all Exceptions which occur during the validation.

        //Validation of all general Product fields
        ProductType type = requireNotNull(p.getType(), "pgroup", exceptions);

        String asin = requireNonBlank(p.getAsin(), "asin", exceptions);
        asin = requireStringMaxLength(asin, 10, "asin", exceptions);

        String title = requireNonBlank(p.getTitle(), "title", exceptions);
        String imgUrl = (p.getImgUrl() != null && !p.getImgUrl().isBlank()) ? p.getImgUrl().trim() : null; //optional
        Integer salesrank = requireNonNegativeInt(p.getSalesrank(), "salesrank", null); //optional
        List<String> similarIds = validateSimilars(asin, p.getSimilarProductIds());

        Offer offer = validateOffer(p.getOffer(), exceptions);

        //Specific fields for the different SubTypes. 
        //The validated Product data is set via a lateSet method. 
        //Returns null if exceptions occured. 
        Product finalProduct = null;
        switch (type) {
            case BOOK:
                Book book = validateBook((BookRaw) p, exceptions);
                if (book != null)
                    book.lateSetProductData(asin, type, title, salesrank, imgUrl, similarIds, offer);
                finalProduct = book;
                break;

            case MUSIC_CD:
                Music music = validateMusic((MusicRaw) p, exceptions);
                if (music != null)
                    music.lateSetProductData(asin, type, title, salesrank, imgUrl, similarIds, offer);
                finalProduct = music;
                break;
                
            case DVD:
                DVD dvd = validateDVD((DVDRaw) p, exceptions);
                if (dvd != null)
                    dvd.lateSetProductData(asin, type, title, salesrank, imgUrl, similarIds, offer);
                finalProduct = dvd;
                break;

            default:
                finalProduct = null;
        }

        //Throw combined Exception for Validation Errors if existent
        if (!exceptions.isEmpty()) {
            throw new MultipleValidationException(exceptions);
        }

        //Return the final Product
        return finalProduct;
    }

    /**
     * Validates a List of Similar Product IDs. This includes the cleanList procedure and the removal of the parent product id. 
     * @param productId Id of parent Product
     * @param similarIds List of similar Product IDs.
     * @return Validated and cleaned List of similar IDS.
     */
    private static List<String> validateSimilars(String productId, List<String> similarIds) {
        similarIds = cleanList(similarIds);
        if (similarIds.contains(productId)) {
            similarIds.remove(productId);
        }

        return similarIds;
    }

    /**
     * Validates the <price> content of the XML file. Returns a typed Offer object, or null if errors occur. 
     * @param p PriceRaw object with raw String objects. 
     * @param exceptions List<ValidationException> to add ValidationExceptions to. 
     * @return Typed and validated Offer object or NULL
     */
    private static Offer validateOffer(PriceRaw p, List<ValidationException> exceptions) {
        if (p == null) return null; // Items without prices are allowed
        if (p.price() == null || p.price().isBlank()) return null; // Items without prices are allowed

        Integer price = requirePositiveInt(p.price(), "price:value", exceptions);
        String currency = requireNonBlank(p.currency(), "price:currency", exceptions);
        currency = requireStringMaxLength(currency, 8, "price:currency", exceptions);
        String state = requireNonBlank(p.state(), "price:state", exceptions);
        Double mult = requireNonNegativeDouble(p.mult(), "price:mult", exceptions);

        if (!exceptions.isEmpty()) return null;

        return new Offer(mult * price, currency, state);
    }

    /**
     * Validates the DVD specific content of the XML file. Returns a typed DVD object, or null if errors occur. 
     * @param p DVDRaw object with raw String objects. 
     * @param exceptions List<ValidationException> to add ValidationExceptions to. 
     * @return Typed and validated DVD object or NULL
     */
    private static DVD validateDVD(DVDRaw p, List<ValidationException> exceptions) {
        List<String> directors = cleanList(p.getDirectors());
        List<String> actors = cleanList(p.getActors());
        List<String> creators = cleanList(p.getCreators());

        DVDSpecRaw spec = requireNotNull(p.getDvdSpec(), "dvdspec", exceptions);
        
        //Format is converted to a List by splitting the String at ',' chars. 
        String format = requireNonBlank(spec.format(), "dvdspec:format", exceptions);
        String[] formatArray = format.split(",");
        List<String> formats = cleanList(List.of(formatArray));
        getFirstFromList(formats, "dvdspec:format", exceptions); //Implicit check if List is not empty. 
        
        Integer regioncode = requireNonNegativeInt(spec.regioncode(), "dvdspec:regioncode", exceptions);
        Integer runningtime = requirePositiveInt(spec.runningtime(), "dvdspec:runningtime", exceptions);

        if (!exceptions.isEmpty()) return null;

        return new DVD(directors, actors, creators, formats, runningtime, regioncode);
    }

    /**
     * Validates the Music specific content of the XML file. Returns a typed Music object, or null if errors occur. 
     * @param p MusicRaw object with raw String objects. 
     * @param exceptions List<ValidationException> to add ValidationExceptions to. 
     * @return Typed and validated Music object or NULL
     */
    private static Music validateMusic(MusicRaw p, List<ValidationException> exceptions) {
        List<String> labels = cleanList(p.getLabels());
        String label = getFirstFromList(labels, "labels", exceptions);

        List<String> artists = cleanList(p.getArtists());
        getFirstFromList(artists, "artists", exceptions); // Implicitly checks if there is at least one item in the list. 

        List<String> tracks = cleanList(p.getTracks());
        getFirstFromList(tracks, "tracks", exceptions); // Implicitly checks if there is at least one item in the list. 

        MusicSpecRaw spec = requireNotNull(p.getMusicSpec(), "musicspec", exceptions);
        Date releaseDate = requireDate(spec.releasedate(), "musicspec:releasedate", exceptions);

        if (!exceptions.isEmpty()) return null;

        return new Music(label, artists, tracks, releaseDate);
    }

    /**
     * Validates the Book specific content of the XML file. Returns a typed Book object, or null if errors occur. 
     * @param p BookRaw object with raw String objects. 
     * @param exceptions List<ValidationException> to add ValidationExceptions to. 
     * @return Typed and validated Book object or NULL
     */
    private static Book validateBook(BookRaw p, List<ValidationException> exceptions) {
        List<String> publisherNames = cleanList(p.getPublishers());
        String publisherName = getFirstFromList(publisherNames, "publisher", exceptions); //We take only the first object in the list. 

        List<String> authorNames = cleanList(p.getAuthors());
        getFirstFromList(authorNames, "authors", exceptions); // Implicitly checks if there is at least one item in the list. 

        BookSpecRaw spec = requireNotNull(p.getBookSpec(), "bookspec", exceptions);
        
        String isbn = requireNonBlank(spec.isbn(), "bookspec:isbn", exceptions);
        isbn = requireStringMaxLength(isbn, 10, "bookspec:isbn", exceptions); //ISBN are max length 10

        Integer pages = requirePositiveInt(spec.pages(), "bookspec:pages", exceptions);
        Date publication = requireDate(spec.publication(), "bookspec:publication", exceptions);

        if (!exceptions.isEmpty()) return null;

        return new Book(publisherName, authorNames, isbn, pages, publication);
    }
}
