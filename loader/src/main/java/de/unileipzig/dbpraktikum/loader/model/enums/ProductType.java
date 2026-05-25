package de.unileipzig.dbpraktikum.loader.model.enums;

public enum ProductType {
    BOOK,
    MUSIC_CD,
    DVD;

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