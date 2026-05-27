package de.unileipzig.dbpraktikum.loader.db.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import de.unileipzig.dbpraktikum.loader.exception.NotExistException;
import de.unileipzig.dbpraktikum.loader.exception.UnknownSQLException;
import de.unileipzig.dbpraktikum.loader.db.repository.ProductRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.ReviewRepository;
import de.unileipzig.dbpraktikum.loader.logger.ErrorLogger;
import de.unileipzig.dbpraktikum.loader.model.Review;

/**
 * Service class to import validated Review data into the Database.
 * Uses the repository classes to interact with the DB.
 * Writes Exceptions which occur during this process to the Error log.
 */
public class ReviewsImportService {
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    /**
     * Initialize all required Repos
     */
    public ReviewsImportService() {
        this.productRepository = new ProductRepository();
        this.reviewRepository = new ReviewRepository();
    }

    /**
     * Import a single Review into the DB.
     * @param con DB Connection Obj.
     * @param review The Review to import.
     * @throws SQLException thrown on SQL execution problems.
     * @throws NotExistException thrown if the product does not exist in the DB.
     */
    private void importReview(Connection con, Review review) throws SQLException, NotExistException {
        //Check if Product exists.
        if (!productRepository.exists(con, review.productId())) {
            throw new NotExistException("Product", review.productId());
        }

        //Insert Review
        reviewRepository.insert(con, review);
    }

    /**
     * Import a List of Reviews into DB.
     * @param con DB Connection Obj.
     * @param reviews List<Review> to import.
     */
    public void importReviews(Connection con, List<Review> reviews) {
        System.out.println("Starting DB insertions...");

        int validCounter = 0;

        //Handle all Reviews on their own.
        for (Review review : reviews) {
            try {
                importReview(con, review);
                validCounter++;
            } catch (NotExistException ex) {
                //Product is not in DB
                ErrorLogger.reportErrors(review.productId() + " - Review", List.of(ex));
            } catch (SQLException ex) {
                //Error while executing SQL
                ErrorLogger.reportErrors(
                    review.productId() + " - Review",
                    List.of(new UnknownSQLException(review.productId(), ex.getMessage()))
                );
            }
        }

        //Stat report
        System.out.println(validCounter + " valid Reviews");
        System.out.println(reviews.size() - validCounter + " invalid Reviews");
    }
}
