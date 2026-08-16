package de.unileipzig.dbpraktikum.loader.model.enums;

/**
 * Enum for all possible Product types. 
 */
public enum ProductType {
    BOOK,
    MUSIC_CD,
    DVD;

    /**
     * Helper function to get the corresponding ProductType from a String value. Handles a few different anmes / typos. 
     * @param value String value to parse to type. 
     * @return Matched ProductType, or null if not found. 
     */
    public static ProductType fromXmlValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return switch (value.trim().toLowerCase()) {
            case "book", "buch" -> BOOK;
            case "music", "music_cd", "musik", "musical" -> MUSIC_CD;
            case "dvd" -> DVD;
            default -> null;
        };
    }
}