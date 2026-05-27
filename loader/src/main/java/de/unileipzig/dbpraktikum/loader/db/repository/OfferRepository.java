package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.Offer;

public class OfferRepository {
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
