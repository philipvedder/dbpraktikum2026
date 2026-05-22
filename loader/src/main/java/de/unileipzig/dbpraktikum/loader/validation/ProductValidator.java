package de.unileipzig.dbpraktikum.loader.validation;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unileipzig.dbpraktikum.loader.model.Book;
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
    public static void validate(ProductRaw p) {
        Map<String, String> errors = new HashMap<>();

        ProductType type = requireNotNull(p.getType(), "pgroup", errors);

        String asin = requireNonBlank(p.getAsin(), "asin", errors);
        String title = requireNonBlank(p.getTitle(), "title", errors);
        String imgUrl = p.getImgUrl().trim();
        Integer salesrank = requireNonNegativeInt(p.getSalesrank(), "salesrank", errors);
        List<String> similarIds = filterBlanksFromList(p.getSimilarProductIds());

        PriceRaw offer = requireNotNull(p.getOffer(), "price", errors);

        switch (type) {
            case BOOK:
                return validateBook((BookRaw) p, errors);
            case MUSIC:
                return validateMusic((MusicRaw) p, errors);
            case DVD:
                return validateDVD((DVDRaw) p, errors);
            default:
                reportErrors(p, errors);
                return null;
        }
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
            return null;
        }

        return new DVD();
    }

    private static void validateMusic(MusicRaw p, Map<String, String> errors) {
        List<String> labels = filterBlanksFromList(p.getLabels());
        List<String> artists = filterBlanksFromList(p.getLabels());
        List<String> tracks = filterBlanksFromList(p.getLabels());

        MusicSpecRaw spec = requireNotNull(p.getMusicSpec(), "musicspec", errors);
        Timestamp releaseDate = requireTimestamp(spec.releasedate(), "musicspec:releasedate", errors);

        if (errors.keySet().size() > 0) {
            reportErrors(p, errors);
            return null;
        }

        return new Music();
    }

    private static void validateBook(BookRaw p, Map<String, String> errors) {
        List<String> publisherNames = filterBlanksFromList(p.getPublishers());
        List<String> authorNames = filterBlanksFromList(p.getAuthors());

        BookSpecRaw spec = requireNotNull(p.getBookSpec(), "bookspec", errors);
        String isbn = requireNonBlank(spec.isbn(), "bookspec:isbn", errors);
        Integer pages = requireNonNegativeInt(spec.pages(), "bookspec:pages", errors);
        Timestamp publication = requireTimestamp(spec.publication(), "bookspec:publication", errors);

        if (errors.keySet().size() > 0) {
            reportErrors(p, errors);
            return null;
        }

        return new Book();
    }

    private static void reportErrors(ProductRaw p, Map<String, String> errors) {

    }

    // Validation Methods

    private static Timestamp requireTimestamp(String s, String name, Map<String, String> errors) {
        s = requireNonBlank(s, name, errors);
        Timestamp result = null;

        try {
            result = Timestamp.valueOf(s);
        } catch (IllegalArgumentException e) {
            errors.put(name, "Value must be in valid timestamp format yyyy-[m]m-[d]d hh:mm:ss[.f...]");
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
            errors.put(name, "Value must be Integer");
            return null;
        }

        return result;
    }

    private static Integer requireNonNegativeInt(String s, String name, Map<String, String> errors) {
        Integer result = requireInt(s, name, errors);
        if (result == null) return null;

        if (result < 0) {
            errors.put(name, "Value must be >= 0");
        }

        return result;
    }

    private static <T> T requireNotNull(T o, String name, Map<String, String> errors) {
        if (o == null) {
            errors.put(name, "Value must not be NULL");
        }

        return o;
    }

    private static String requireNonBlank(String s, String name, Map<String, String> errors) {
        if (s == null || s.isBlank()) {
            errors.put(name, "Value must not be empty");
        }

        return s.trim();
    }
}
