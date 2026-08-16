package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.Review;

/**
 * Repository to interact with the DB Table:
 * Rezension
 */
public class ReviewRepository {
    private final CustomerRepository customerRepository;

    /**
     * Initialize required Repositories.
     */
    public ReviewRepository() {
        this.customerRepository = new CustomerRepository();
    }

    /**
     * Inserts a new Review into DB. 
     * To do so, at first, the referenced Customer is found or created. 
     * Then the Review is inserted. 
     * @param con DB Connection Obj. 
     * @param review Review. The Review to insert.
     * @throws SQLException thrown on SQL execution problems.
     */
    public void insert(Connection con, Review review) throws SQLException {
        String sql = """
            INSERT INTO media_store.rezension (
                kunde_id,
                produkt_nr,
                rezensionszeitpunkt,
                punkte,
                rezensionstext
            )
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            Long customerId = customerRepository.findOrCreate(con, review.userName());

            stmt.setLong(1, customerId);
            stmt.setString(2, review.productId());
            stmt.setDate(3, review.reviewDate());
            stmt.setInt(4, review.rating());
            stmt.setString(5, review.reviewText());
            //stmt.setString(6, review.summary()); UNUSED

            stmt.executeUpdate();
        }
    }
}