package de.unileipzig.dbpraktikum.loader.model;

import java.sql.Date;

/**
 * Validated model for one review.
 */
public record Review(
    String productId,
    String userName,
    int rating,
    Date reviewDate,
    String reviewText,
    String summary
) {
}