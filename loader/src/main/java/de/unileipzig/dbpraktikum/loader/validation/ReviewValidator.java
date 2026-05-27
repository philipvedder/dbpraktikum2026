package de.unileipzig.dbpraktikum.loader.validation;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.model.Review;
import de.unileipzig.dbpraktikum.loader.model.raw.ReviewRaw;

public class ReviewValidator {

    public static List<Review> validateAll(List<ReviewRaw> rawReviews) {
        List<Review> results = new ArrayList<>();

        if (rawReviews == null || rawReviews.isEmpty()) {
            return results;
        }

        int invalidCounter = 0;

        for (ReviewRaw rawReview : rawReviews) {
            Review review = validate(rawReview);

            if (review != null) {
                results.add(review);
            } else {
                invalidCounter++;
            }
        }

        System.out.println(results.size() + " valid reviews");
        System.out.println(invalidCounter + " invalid reviews");

        return results;
    }

    public static Review validate(ReviewRaw rawReview) {
        if (rawReview == null) {
            return null;
        }

        String productId = clean(rawReview.product());
        String userName = clean(rawReview.user());
        String summary = clean(rawReview.summary());
        String content = clean(rawReview.content());

        if (productId.isEmpty()) {
            System.out.println("Invalid review: missing product id");
            return null;
        }

        if (userName.isEmpty()) {
            System.out.println("Invalid review for product " + productId + ": missing user");
            return null;
        }

        int rating;

        try {
            rating = Integer.parseInt(clean(rawReview.rating()));
        } catch (NumberFormatException ex) {
            System.out.println("Invalid review for product " + productId + ": invalid rating");
            return null;
        }

        if (rating < 1 || rating > 5) {
            System.out.println("Invalid review for product " + productId + ": rating out of range");
            return null;
        }

        Timestamp reviewTimestamp;

        try {
            LocalDate reviewDate = LocalDate.parse(clean(rawReview.reviewDate()));
            reviewTimestamp = Timestamp.valueOf(reviewDate.atStartOfDay());
        } catch (DateTimeParseException ex) {
            System.out.println("Invalid review for product " + productId + ": invalid review date");
            return null;
        }

        String reviewText = buildReviewText(summary, content);

        return new Review(
            productId,
            userName,
            rating,
            reviewTimestamp,
            reviewText
        );
    }

    private static String buildReviewText(String summary, String content) {
        if (summary.isEmpty()) {
            return content;
        }

        if (content.isEmpty()) {
            return summary;
        }

        return summary + "\n\n" + content;
    }

    private static String clean(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}
