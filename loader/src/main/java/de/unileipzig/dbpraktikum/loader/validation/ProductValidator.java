package de.unileipzig.dbpraktikum.loader.validation;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import de.unileipzig.dbpraktikum.exception.BlankException;
import de.unileipzig.dbpraktikum.exception.ListEmptyException;
import de.unileipzig.dbpraktikum.exception.MultipleValidationException;
import de.unileipzig.dbpraktikum.exception.NotANonNegativeIntegerException;
import de.unileipzig.dbpraktikum.exception.NotAValidDateFormatException;
import de.unileipzig.dbpraktikum.exception.NotAnDoubleException;
import de.unileipzig.dbpraktikum.exception.NotAnIntegerException;
import de.unileipzig.dbpraktikum.exception.NotAnNonNegativeDoubleException;
import de.unileipzig.dbpraktikum.exception.NullException;
import de.unileipzig.dbpraktikum.exception.StringMaxLengthException;
import de.unileipzig.dbpraktikum.exception.ValidationException;
import de.unileipzig.dbpraktikum.loader.logger.ErrorLogger;
import de.unileipzig.dbpraktikum.loader.model.Book;
import de.unileipzig.dbpraktikum.loader.model.DVD;
import de.unileipzig.dbpraktikum.loader.model.Music;
import de.unileipzig.dbpraktikum.loader.model.Offer;
import de.unileipzig.dbpraktikum.loader.model.Product;
import de.unileipzig.dbpraktikum.loader.model.enums.ProductType;
import de.unileipzig.dbpraktikum.loader.model.raw.BookRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.BookSpecRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.DVDRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.DVDSpecRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.MusicRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.MusicSpecRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.PriceRaw;
import de.unileipzig.dbpraktikum.loader.model.raw.ProductRaw;

public class ProductValidator {
    public static List<Product> validateAll(List<ProductRaw> products) {
        System.out.println("Validating " + products.size() + " products...");

        List<Product> results = new ArrayList<>();
        int invalidCounter = 0;

        for (ProductRaw productRaw : products) {
            try {
                Product p = validate(productRaw);
                if (p != null)
                    results.add(p);
                
            } catch (MultipleValidationException e) {
                ErrorLogger.reportErrors(productRaw.getAsin(), productRaw.getType(), e.getExceptions());
                invalidCounter++;
            }
        }

        System.out.println(products.size() - invalidCounter + " valid Products");
        System.out.println(invalidCounter + " invalid Products");
        return results;
    }

    public static Product validate(ProductRaw p) throws MultipleValidationException {
        List<ValidationException> exceptions = new ArrayList<>(); //List of all Exceptions which occur during the validation.

        //Validation of all general Product fields
        ProductType type = requireNotNull(p.getType(), "pgroup", exceptions);

        String asin = requireNonBlank(p.getAsin(), "asin", exceptions);
        asin = requireStringMaxLength(asin, 10, "asin", exceptions);

        String title = requireNonBlank(p.getTitle(), "title", exceptions);
        String imgUrl = (p.getImgUrl() != null && !p.getImgUrl().isBlank()) ? p.getImgUrl().trim() : null; //optional
        Integer salesrank = requireNonNegativeInt(p.getSalesrank(), "salesrank", null); //optional
        List<String> similarIds = cleanList(p.getSimilarProductIds());

        Offer offer = validateOffer(p.getOffer(), exceptions);

        //Specific fields for the different types
        Product finalProduct = null;
        switch (type) {
            case BOOK:
                Book book = validateBook((BookRaw) p, exceptions);
                if (book != null)
                    book.lateSetProductData(asin, type, title, salesrank, imgUrl, similarIds, offer);
                finalProduct = null;
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

    private static Offer validateOffer(PriceRaw p, List<ValidationException> exceptions) {
        if (p == null) return null; // Items without prices are allowed by design

        Integer price = requireNonNegativeInt(p.price(), "price:value", null);
        if (price == null) return null; // Items without prices are allowed by design

        String currency = (p.currency() != null && !p.currency().isBlank()) ? p.currency().trim() : null;
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

    // Validation Methods
    private static Date requireDate(String s, String name, List<ValidationException> exceptions) {
        s = requireNonBlank(s, name, exceptions);
        Date result = null;

        try {
            result = Date.valueOf(s);
        } catch (IllegalArgumentException e) {
            if (exceptions != null) exceptions.add(new NotAValidDateFormatException(name, s));
            return null;
        }

        return result;
    }

    private static <T> T getFirstFromList(List<T> list, String name, List<ValidationException> exceptions) {
        if (list == null || list.size() == 0) {
            if (exceptions != null) exceptions.add(new ListEmptyException(name));
            return null;
        }

        return list.get(0);
    }

    private static List<String> cleanList(List<String> list) {
        if (list == null) return null;

        List<String> result = list.stream()
                                .filter((s) -> (s != null && !s.isBlank()))
                                .map((s) -> s.trim())
                                .distinct()
                                .toList();

        return result;
    }

    private static Integer requireInt(String s, String name, List<ValidationException> exceptions) {
        s = requireNonBlank(s, name, exceptions);
        Integer result = null;

        try {
            result = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            if (exceptions != null) exceptions.add(new NotAnIntegerException(name, s));
            return null;
        }

        return result;
    }

    private static Integer requireNonNegativeInt(String s, String name, List<ValidationException> exceptions) {
        Integer result = requireInt(s, name, exceptions);
        if (result == null) return null;

        if (result < 0) {
            if (exceptions != null) exceptions.add(new NotANonNegativeIntegerException(name, s));
        }

        return result;
    }

    private static Double requireDouble(String s, String name, List<ValidationException> exceptions) {
        s = requireNonBlank(s, name, exceptions);
        Double result = null;

        try {
            result = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            if (exceptions != null) exceptions.add(new NotAnDoubleException(name, s));
            return null;
        }

        return result;
    }

    private static Double requireNonNegativeDouble(String s, String name, List<ValidationException> exceptions) {
        Double result = requireDouble(s, name, exceptions);
        if (result == null) return null;

        if (result < 0) {
            if (exceptions != null) exceptions.add(new NotAnNonNegativeDoubleException(name, s));
        }

        return result;
    }

    private static <T> T requireNotNull(T o, String name, List<ValidationException> exceptions) {
        if (o == null) {
            if (exceptions != null) exceptions.add(new NullException(name));
        }

        return o;
    }

    private static String requireNonBlank(String s, String name, List<ValidationException> exceptions) {
        s = requireNotNull(s, name, exceptions);
        if (s == null) return null;

        if (s.isBlank()) {
            if (exceptions != null) exceptions.add(new BlankException(name));
            return null;
        }

        return s.trim();
    }

    private static String requireStringMaxLength(String s, int numberOfChars, String name, List<ValidationException> exceptions) {
        if (s == null) return null;

        if (s.length() > numberOfChars) {
            if (exceptions != null) exceptions.add(new StringMaxLengthException(name, s, numberOfChars));
            return null;
        }

        return s;
    }
}
