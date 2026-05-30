package de.unileipzig.dbpraktikum.loader.validation;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.exception.MultipleValidationException;
import de.unileipzig.dbpraktikum.loader.exception.ValidationException;
import de.unileipzig.dbpraktikum.loader.logger.ErrorLogger;
import de.unileipzig.dbpraktikum.loader.model.Review;
import de.unileipzig.dbpraktikum.loader.model.raw.ReviewRaw;

/**
 * Validator class for ReviewRaw objects
 * Checks each variable of each review object and returns validated, typed Objects 
 */
public class ReviewValidator extends Validator {

    /**
     * Validates a List of ReviewRaw objects. 
     * All errors that occur for each Review will be logged. 
     * @param rawReviews List<ReviewRaw> the raw review objects to validate
     * @return The validated List<Review> of correctly-typed objects
     */
    public static List<Review> validateAll(List<ReviewRaw> rawReviews) {
        List<Review> results = new ArrayList<>();
        int invalidCounter = 0;

        //Nothing to do on empty lists. 
        if (rawReviews == null || rawReviews.isEmpty()) {
            return results;
        }

        //Review object validation
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

    /**
     * Validates the input ReviewRaw object by validating and converting all its variables.
     * Throws if any ValidationErrors occur on the Review or its content.  
     * @param rawReview ReviewRaw input, where all variabels are of Type String. 
     * @return Object of Type Review with correct Types and validated. 
     * @throws MultipleValidationException, if any Validation threw an error. MultipleValidationException contains a list of all ValidationExceptions that occured on this Review.
     */
    public static Review validate(ReviewRaw rawReview) throws MultipleValidationException {
        if (rawReview == null) return null;
        List<ValidationException> exceptions = new ArrayList<>(); //List of all Exceptions which occur during the validation.

        //Validation of all review fields. 
        String productId = requireNonBlank(rawReview.product(), "productId", exceptions);
        productId = requireStringMaxLength(productId, 10, "productId", exceptions);

        String userName = requireNonBlank(rawReview.user(), "username", exceptions);
        userName = requireStringMaxLength(userName, 256, "username", exceptions);
        String summary = requireNonBlank(rawReview.summary(), "summary", exceptions);

        String content = requireNonBlank(rawReview.content(), "content", exceptions);
        Integer rating = requireIntBetween(rawReview.rating(), 1, 5, "rating", exceptions);
        Date date = requireDateNotInFuture(rawReview.reviewDate(), "reviewDate", exceptions);

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
