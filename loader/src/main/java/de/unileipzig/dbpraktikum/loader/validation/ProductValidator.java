package de.unileipzig.dbpraktikum.loader.validation;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unileipzig.dbpraktikum.loader.model.Book;
import de.unileipzig.dbpraktikum.loader.model.Offer;
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
    public static void validateAll(List<ProductRaw> products) {
        for (ProductRaw productRaw : products) {
            validate(productRaw);
        }
    }

    public static void validate(ProductRaw p) {
        Map<String, String> errors = new HashMap<>();

        ProductType type = requireNotNull(p.getType(), "pgroup", errors);

        String asin = requireNonBlank(p.getAsin(), "asin", errors);
        String title = requireNonBlank(p.getTitle(), "title", errors);
        String imgUrl = (p.getImgUrl() != null && !p.getImgUrl().isBlank()) ? p.getImgUrl().trim() : null;

        Integer salesrank = requireNonNegativeInt(p.getSalesrank(), "salesrank", null);
        List<String> similarIds = filterBlanksFromList(p.getSimilarProductIds());

        Offer offer = validateOffer(p.getOffer(), errors);

        switch (type) {
            case BOOK:
                validateBook((BookRaw) p, errors);
                break;
            case MUSIC:
                validateMusic((MusicRaw) p, errors);
                break;
            case DVD:
                validateDVD((DVDRaw) p, errors);
                break;
            default:
                reportErrors(p, errors);
                //return null;
        }
    }

    private static Offer validateOffer(PriceRaw p, Map<String, String> errors) {
        if (p == null) return null; //Items without prices are allowed by design

        Integer price = requireNonNegativeInt(p.price(), "price:value", null);
        if (price == null) return null; //Items without prices are allowed by design

        String currency = (p.currency() != null && !p.currency().isBlank()) ? p.currency().trim() : null;
        String state = requireNonBlank(p.state(), "price:state", errors);
        Double mult = requireNonNegativeDouble(p.mult(), "price:mult", errors);

        if (!errors.isEmpty()) return null;

        return new Offer(mult * price, currency, state);
    }

    private static void validateDVD(DVDRaw p, Map<String, String> errors) {
        List<String> directors = filterBlanksFromList(p.getDirectors());
        List<String> actors = filterBlanksFromList(p.getActors());
        List<String> creators = filterBlanksFromList(p.getCreators());

        DVDSpecRaw spec = requireNotNull(p.getDvdSpec(), "dvdspec", errors);
        String format = requireNonBlank(spec.format(), "dvdspec:format", errors);
        Integer regioncode = requireNonNegativeInt(spec.regioncode(), "dvdspec:regioncode", errors);
        Integer runningtime = requireNonNegativeInt(spec.runningtime(), "dvdspec:runningtime", errors);

        if (errors.keySet().size() > 0) {
            reportErrors(p, errors);
            //return null;
        }

        //return new DVD();
    }

    private static void validateMusic(MusicRaw p, Map<String, String> errors) {
        List<String> labels = filterBlanksFromList(p.getLabels());
        List<String> artists = filterBlanksFromList(p.getLabels());
        List<String> tracks = filterBlanksFromList(p.getLabels());

        MusicSpecRaw spec = requireNotNull(p.getMusicSpec(), "musicspec", errors);
        Date releaseDate = requireDate(spec.releasedate(), "musicspec:releasedate", errors);

        if (errors.keySet().size() > 0) {
            reportErrors(p, errors);
            //return null;
        }

        //return new Music();
    }

    private static Book validateBook(BookRaw p, Map<String, String> errors) {
        List<String> publisherNames = filterBlanksFromList(p.getPublishers());
        List<String> authorNames = filterBlanksFromList(p.getAuthors());

        BookSpecRaw spec = requireNotNull(p.getBookSpec(), "bookspec", errors);
        String isbn = requireNonBlank(spec.isbn(), "bookspec:isbn", errors);
        Integer pages = requireNonNegativeInt(spec.pages(), "bookspec:pages", errors);
        Date publication = requireDate(spec.publication(), "bookspec:publication", errors);

        if (errors.keySet().size() > 0) {
            reportErrors(p, errors);
            return null;
        }

        return new Book();
    }

    private static void reportErrors(ProductRaw p, Map<String, String> errors) {
        //TODO: write to file

        if (errors.isEmpty()) return;

        System.out.println("Errors while validating product: " + p.getAsin() + " - " + p.getTitle());
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
}
