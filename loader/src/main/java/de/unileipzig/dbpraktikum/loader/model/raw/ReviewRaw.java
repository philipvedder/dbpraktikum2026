package de.unileipzig.dbpraktikum.loader.model.raw;

/**
 * Raw model for one review row from the CSV file.
 * All values are still Strings and will be validated later.
 */
public record ReviewRaw(
    String product,
    String rating,
    String helpful,
    String reviewDate,
    String user,
    String summary,
    String content
) {
}