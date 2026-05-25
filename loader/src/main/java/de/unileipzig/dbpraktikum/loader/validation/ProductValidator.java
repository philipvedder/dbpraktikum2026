package de.unileipzig.dbpraktikum.loader.validation;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Product> results = new ArrayList<>();
        int acceptedCounter = 0;

        for (ProductRaw productRaw : products) {
            Product p = validate(productRaw);
            if (p != null) {
                results.add(p);
                acceptedCounter++;
            }
        }

        System.out.println(acceptedCounter + " valid Products");
        System.out.println(products.size() - acceptedCounter + " invalid Products");
        return results;
    }

    public static Product validate(ProductRaw p) {
        Map<String, String> errors = new HashMap<>();

        ProductType type = requireNotNull(p.getType(), "pgroup", errors);

        String asin = requireNonBlank(p.getAsin(), "asin", errors);
        asin = requireStringMaxLength(asin, 10, "asin", errors);

        String title = requireNonBlank(p.getTitle(), "title", errors);
        String imgUrl = (p.getImgUrl() != null && !p.getImgUrl().isBlank()) ? p.getImgUrl().trim() : null;
        Integer salesrank = requireNonNegativeInt(p.getSalesrank(), "salesrank", null); //optional
        List<String> similarIds = filterBlanksFromList(p.getSimilarProductIds()); //TODO: length? name of function?

        Offer offer = validateOffer(p.getOffer(), errors);

        switch (type) {
            case BOOK:
                Book book = validateBook((BookRaw) p, errors);
                if (book == null) return null;
                book.lateSetProductData(asin, type, title, salesrank, imgUrl, similarIds, offer);
                return book;
            case MUSIC_CD:
                Music music = validateMusic((MusicRaw) p, errors);
                if (music == null) return null;
                music.lateSetProductData(asin, type, title, salesrank, imgUrl, similarIds, offer);
                return music;
            case DVD:
                DVD dvd = validateDVD((DVDRaw) p, errors);
                if (dvd == null) return null;
                dvd.lateSetProductData(asin, type, title, salesrank, imgUrl, similarIds, offer);
                return dvd;
            default:
                reportErrors(p, errors);
                return null;
        }
    }

    private static Offer validateOffer(PriceRaw p, Map<String, String> errors) {
        if (p == null) return null; // Items without prices are allowed by design

        Integer price = requireNonNegativeInt(p.price(), "price:value", null);
        if (price == null) return null; // Items without prices are allowed by design

        String currency = (p.currency() != null && !p.currency().isBlank()) ? p.currency().trim() : null;
        String state = requireNonBlank(p.state(), "price:state", errors);
        Double mult = requireNonNegativeDouble(p.mult(), "price:mult", errors);

        if (!errors.isEmpty()) return null;

        return new Offer(mult * price, currency, state);
    }

    private static DVD validateDVD(DVDRaw p, Map<String, String> errors) {
        List<String> directors = filterBlanksFromList(p.getDirectors());
        List<String> actors = filterBlanksFromList(p.getActors());
        List<String> creators = filterBlanksFromList(p.getCreators());

        DVDSpecRaw spec = requireNotNull(p.getDvdSpec(), "dvdspec", errors);
        String format = requireNonBlank(spec.format(), "dvdspec:format", errors);
        Integer regioncode = requireNonNegativeInt(spec.regioncode(), "dvdspec:regioncode", errors);
        Integer runningtime = requireNonNegativeInt(spec.runningtime(), "dvdspec:runningtime", errors);

        if (errors.keySet().size() > 0) {
            reportErrors(p, errors);
            return null;
        }

        return new DVD(directors, actors, creators, format, runningtime, regioncode);
    }

    private static Music validateMusic(MusicRaw p, Map<String, String> errors) {
        List<String> labels = filterBlanksFromList(p.getLabels());
        String label = getFirstFromList(labels, "labels", errors);

        List<String> artists = filterBlanksFromList(p.getLabels());
        getFirstFromList(artists, "artists", errors); // Implicitly checks if there is at least one item in the list. 

        List<String> tracks = filterBlanksFromList(p.getLabels());

        MusicSpecRaw spec = requireNotNull(p.getMusicSpec(), "musicspec", errors);
        Date releaseDate = requireDate(spec.releasedate(), "musicspec:releasedate", errors);

        if (errors.keySet().size() > 0) {
            reportErrors(p, errors);
            return null;
        }

        return new Music(label, artists, tracks, releaseDate);
    }

    private static Book validateBook(BookRaw p, Map<String, String> errors) {
        List<String> publisherNames = filterBlanksFromList(p.getPublishers());
        String publisherName = getFirstFromList(publisherNames, "publisher", errors);

        List<String> authorNames = filterBlanksFromList(p.getAuthors());

        BookSpecRaw spec = requireNotNull(p.getBookSpec(), "bookspec", errors);
        
        String isbn = requireNonBlank(spec.isbn(), "bookspec:isbn", errors);
        isbn = requireStringMaxLength(isbn, 10, "bookspec:isbn", errors);

        Integer pages = requireNonNegativeInt(spec.pages(), "bookspec:pages", errors);
        Date publication = requireDate(spec.publication(), "bookspec:publication", errors);

        if (errors.keySet().size() > 0) {
            reportErrors(p, errors);
            return null;
        }

        return new Book(publisherName, authorNames, isbn, pages, publication);
    }

    private static void reportErrors(ProductRaw p, Map<String, String> errors) {
        //TODO: write to file

        if (errors.isEmpty()) return;

        System.out.println("Error: " + p.getType().name() + " - " + p.getAsin());
        for (String key : errors.keySet()) {
            System.out.println(key + " : " + errors.get(key));
        }
        System.out.println("================");
    }

    // Validation Methods
    private static Date requireDate(String s, String name, Map<String, String> errors) {
        s = requireNonBlank(s, name, errors);
        Date result = null;

        try {
            result = Date.valueOf(s);
        } catch (IllegalArgumentException e) {
            if (errors != null) errors.put(name, "Value must be in valid timestamp format yyyy-[m]m-[d]d. Got: " + s);
            return null;
        }

        return result;
    }

    private static <T> T getFirstFromList(List<T> list, String name, Map<String, String> errors) {
        if (list == null || list.size() == 0) {
            if (errors != null) errors.put(name, "List is empty.");
            return null;
        }

        return list.get(0);
    }

    private static List<String> filterBlanksFromList(List<String> list) {
        if (list == null) return null;

        List<String> result = list.stream()
                                .filter((s) -> (s != null && !s.isBlank()))
                                .map((s) -> s.trim())
                                .toList();

        return result;
    }

    private static Integer requireInt(String s, String name, Map<String, String> errors) {
        s = requireNonBlank(s, name, errors);
        Integer result = null;

        try {
            result = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            if (errors != null) errors.put(name, "Value must be Integer. Got: " + s);
            return null;
        }

        return result;
    }

    private static Integer requireNonNegativeInt(String s, String name, Map<String, String> errors) {
        Integer result = requireInt(s, name, errors);
        if (result == null) return null;

        if (result < 0) {
            if (errors != null) errors.put(name, "Value must be >= 0. Got: " + s);
        }

        return result;
    }

    private static Double requireDouble(String s, String name, Map<String, String> errors) {
        s = requireNonBlank(s, name, errors);
        Double result = null;

        try {
            result = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            if (errors != null) errors.put(name, "Value must be a floating point value. Got: " + s);
            return null;
        }

        return result;
    }

    private static Double requireNonNegativeDouble(String s, String name, Map<String, String> errors) {
        Double result = requireDouble(s, name, errors);
        if (result == null) return null;

        if (result < 0) {
            if (errors != null) errors.put(name, "Value must be >= 0.0. Got: " + s);
        }

        return result;
    }

    private static <T> T requireNotNull(T o, String name, Map<String, String> errors) {
        if (o == null) {
            if (errors != null) errors.put(name, "Value must not be NULL");
        }

        return o;
    }

    private static String requireNonBlank(String s, String name, Map<String, String> errors) {
        if (s == null || s.isBlank()) {
            if (errors != null) errors.put(name, "Value must not be empty");
            return null;
        }

        return s.trim();
    }

    private static String requireStringMaxLength(String s, int numberOfChars, String name, Map<String, String> errors) {
        if (s == null) return null;

        if (s.length() > numberOfChars) {
            if (errors != null) errors.put(name, "Value must not have more than " + numberOfChars + " characters. Has: " + s.length());
            return null;
        }

        return s;
    }
}
