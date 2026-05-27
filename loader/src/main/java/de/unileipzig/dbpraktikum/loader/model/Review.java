package de.unileipzig.dbpraktikum.loader.model;

import java.sql.Timestamp;

/**
 * Validated model for one review.
 */
public record Review(
    String productId,
    String userName,
    int rating,
    Timestamp reviewTimestamp,
    String reviewText
) {
}