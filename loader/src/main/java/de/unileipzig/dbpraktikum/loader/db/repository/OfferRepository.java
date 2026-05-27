package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.Offer;

/**
 * Repository to interact with the DB Tables:
 * angebot
 */
public class OfferRepository {
    /**
     * Checks if a Offer for the specified combination of (shopId, productId) already exists.
     * @param con DB Connection Obj. 
     * @param shopId Long. The Id of the corresponding Shop.
     * @param productNr String. The Product Id to offer. 
     * @return boolean. True if entry exists, False otherwise. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean exists(Connection con, Long shopId, String productNr) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.angebot
            WHERE filiale_id = ? AND produkt_nr = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setLong(1, shopId);
            stmt.setString(2, productNr);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    /**
     * Insert a new Offer into DB. 
     * Returns the PK of the created Entry. 
     * @param con DB Connection Obj. 
     * @param shopId Long. Id of the corresponding Shop. Will be referenced. 
     * @param productId String. Id of the corresponding Product. Will be referenced. 
     * @param offer Offer obj to insert. 
     * @return boolean. True on success. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean insert(Connection con, Long shopId, String productId, Offer offer) throws SQLException {
        String sql = """
            INSERT INTO media_store.angebot (
                filiale_id, 
                produkt_nr,
                preis,
                waehrung,
                zustand
            )
            VALUES (?, ?, ?, ?, ?)
            RETURNING filiale_id, produkt_nr
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setLong(1, shopId);
            stmt.setString(2, productId);
            stmt.setDouble(3, offer.price());
            stmt.setString(4, offer.currency());
            stmt.setString(5, offer.state());

            try (ResultSet r= stmt.executeQuery()) {
                if (!r.next()) {
                    throw new SQLException("Could not insert Offer with Shop " + shopId + " and Product " + productId);
                }

                return true;
            }
        }
    }
}
