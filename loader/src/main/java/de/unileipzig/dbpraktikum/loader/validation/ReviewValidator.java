package de.unileipzig.dbpraktikum.loader.validation;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.exception.MultipleValidationException;
import de.unileipzig.dbpraktikum.loader.exception.ValidationException;
import de.unileipzig.dbpraktikum.loader.logger.ErrorLogger;
import de.unileipzig.dbpraktikum.loader.model.Review;
import de.unileipzig.dbpraktikum.loader.model.raw.ReviewRaw;

public class ReviewValidator extends Validator {

    public static List<Review> validateAll(List<ReviewRaw> rawReviews) {
        List<Review> results = new ArrayList<>();
        int invalidCounter = 0;

        if (rawReviews == null || rawReviews.isEmpty()) {
            return results;
        }

        //Review obj validation
        for (ReviewRaw rawReview : rawReviews) {
            try {
                Review review = validate(rawReview);
                if (review != null)
                    results.add(review);
                
            } catch (MultipleValidationException e) {
                //Log all Errors that occur for each Review
                ErrorLogger.reportErrors(rawReview.product() + " - Rating", e.getExceptions());
                invalidCounter++;
            }
        }

        //Result
        System.out.println(results.size() - invalidCounter + " valid reviews");
        System.out.println(invalidCounter + " invalid reviews");
        return results;
    }

    public static Review validate(ReviewRaw rawReview) throws MultipleValidationException {
        if (rawReview == null) return null;
        
        List<ValidationException> exceptions = new ArrayList<>(); //List of all Exceptions which occur during the validation.

        String productId = requireNonBlank(rawReview.product(), "productId", exceptions);
        productId = requireStringMaxLength(productId, 10, "productId", exceptions);

        String userName = requireNonBlank(rawReview.user(), "user", exceptions);
        String summary = requireNonBlank(rawReview.summary(), "user", exceptions);

        String content = requireNonBlank(rawReview.content(), "user", exceptions);
        Integer rating = requireIntBetween(rawReview.rating(), 1, 5, "rating", exceptions);
        Date date = requireDate(rawReview.reviewDate(), "reviewDate", exceptions);

        //Throw combined Exception for Validation Errors if existent
        if (!exceptions.isEmpty()) {
            throw new MultipleValidationException(exceptions);
        }

        //Return final Review
        return new Review(
            productId,
            userName,
            rating,
            date,
            content,
            summary
        );
    }
}
