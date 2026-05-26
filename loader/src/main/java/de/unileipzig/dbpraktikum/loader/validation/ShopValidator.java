package de.unileipzig.dbpraktikum.loader.validation;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import de.unileipzig.dbpraktikum.exception.*;
import de.unileipzig.dbpraktikum.loader.logger.ErrorLogger;
import de.unileipzig.dbpraktikum.loader.model.*;
import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;
import de.unileipzig.dbpraktikum.loader.model.raw.*;

public class ShopValidator extends Validator {
    public static Shop validate(ShopRaw shop) {
        System.out.println("Validating Shop with " + shop.products().size() + " products...");

        //Shop obj Validation
        List<ValidationException> shopExceptions = new ArrayList<>(); //List of all Exceptions which occur during the validation.
        String name = requireNonBlank(shop.name(), "name", shopExceptions);
        String street = requireNonBlank(shop.street(), "street", shopExceptions);
        String zip = requireNonBlank(shop.zip(), "zip", shopExceptions);

        if (!shopExceptions.isEmpty()) {
            ErrorLogger.reportErrors("Shop " + shop.name(), shopExceptions);
            System.out.println("Shop encoding invalid. Stopping early.");
            System.exit(1);
        }

        //Product obj Validation
        List<Product> productResults = new ArrayList<>();
        int invalidCounter = 0;

        for (ProductRaw productRaw : shop.products()) {
            try {
                Product p = validateProduct(productRaw);
                if (p != null)
                    productResults.add(p);
                
            } catch (MultipleValidationException e) {
                ErrorLogger.reportErrors(productRaw.getAsin() + " - " + productRaw.getType().name(), e.getExceptions());
                invalidCounter++;
            }
        }

        //Result
        System.out.println(shop.products().size() - invalidCounter + " valid Products");
        System.out.println(invalidCounter + " invalid Products");
        return new Shop(name, street, zip, productResults);
    }

    public static Product validateProduct(ProductRaw p) throws MultipleValidationException {
        List<ValidationException> exceptions = new ArrayList<>(); //List of all Exceptions which occur during the validation.

        //Validation of all general Product fields
        ProductType type = requireNotNull(p.getType(), "pgroup", exceptions);

        String asin = requireNonBlank(p.getAsin(), "asin", exceptions);
        asin = requireStringMaxLength(asin, 10, "asin", exceptions);

        String title = requireNonBlank(p.getTitle(), "title", exceptions);
        String imgUrl = (p.getImgUrl() != null && !p.getImgUrl().isBlank()) ? p.getImgUrl().trim() : null; //optional
        Integer salesrank = requireNonNegativeInt(p.getSalesrank(), "salesrank", null); //optional
        List<String> similarIds = validateSimilars(asin, p.getSimilarProductIds(), exceptions);

        Offer offer = validateOffer(p.getOffer(), exceptions);

        //Specific fields for the different types
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

    private static List<String> validateSimilars(String productId, List<String> similarIds, List<ValidationException> exceptions) {
        similarIds = cleanList(similarIds);
        if (similarIds.contains(productId)) {
            similarIds.remove(productId);
        }

        return similarIds;
    }

    private static Offer validateOffer(PriceRaw p, List<ValidationException> exceptions) {
        if (p == null) return null; // Items without prices are allowed
        if (p.price() == null || p.price().isBlank()) return null; // Items without prices are allowed

        Integer price = requirePositiveInt(p.price(), "price:value", exceptions);
        String currency = requireNonBlank(p.currency(), "price:currency", exceptions);
        String state = requireNonBlank(p.state(), "price:state", exceptions);
        Double mult = requireNonNegativeDouble(p.mult(), "price:mult", exceptions);

        if (!exceptions.isEmpty()) return null;

        return new Offer(mult * price, currency, state);
    }

    private static DVD validateDVD(DVDRaw p, List<ValidationException> exceptions) {
        List<String> directors = cleanList(p.getDirectors());
        List<String> actors = cleanList(p.getActors());
        List<String> creators = cleanList(p.getCreators());

        DVDSpecRaw spec = requireNotNull(p.getDvdSpec(), "dvdspec", exceptions);
        String format = requireNonBlank(spec.format(), "dvdspec:format", exceptions);
        Integer regioncode = requireNonNegativeInt(spec.regioncode(), "dvdspec:regioncode", exceptions);
        Integer runningtime = requireNonNegativeInt(spec.runningtime(), "dvdspec:runningtime", exceptions);

        if (!exceptions.isEmpty()) return null;

        return new DVD(directors, actors, creators, format, runningtime, regioncode);
    }

    private static Music validateMusic(MusicRaw p, List<ValidationException> exceptions) {
        List<String> labels = cleanList(p.getLabels());
        String label = getFirstFromList(labels, "labels", exceptions);

        List<String> artists = cleanList(p.getLabels());
        getFirstFromList(artists, "artists", exceptions); // Implicitly checks if there is at least one item in the list. 

        List<String> tracks = cleanList(p.getLabels());

        MusicSpecRaw spec = requireNotNull(p.getMusicSpec(), "musicspec", exceptions);
        Date releaseDate = requireDate(spec.releasedate(), "musicspec:releasedate", exceptions);

        if (!exceptions.isEmpty()) return null;

        return new Music(label, artists, tracks, releaseDate);
    }

    private static Book validateBook(BookRaw p, List<ValidationException> exceptions) {
        List<String> publisherNames = cleanList(p.getPublishers());
        String publisherName = getFirstFromList(publisherNames, "publisher", exceptions);

        List<String> authorNames = cleanList(p.getAuthors());

        BookSpecRaw spec = requireNotNull(p.getBookSpec(), "bookspec", exceptions);
        
        String isbn = requireNonBlank(spec.isbn(), "bookspec:isbn", exceptions);
        isbn = requireStringMaxLength(isbn, 10, "bookspec:isbn", exceptions);

        Integer pages = requireNonNegativeInt(spec.pages(), "bookspec:pages", exceptions);
        Date publication = requireDate(spec.publication(), "bookspec:publication", exceptions);

        if (!exceptions.isEmpty()) return null;

        return new Book(publisherName, authorNames, isbn, pages, publication);
    }
}
