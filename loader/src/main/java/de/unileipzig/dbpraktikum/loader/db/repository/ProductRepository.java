package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.Product;

/**
 * Repository to interact with the DB Tables:
 * produkt
 */
public class ProductRepository {
    /**
     * Checks if a Product with the specified ID already exists.
     * @param con DB Connection Obj. 
     * @param id String. The Product Id to search for.
     * @return boolean. True if entry exists, False otherwise. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean exists(Connection con, String id) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.produkt
            WHERE produkt_nr = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    /**
     * Insert a Product into DB. 
     * @param con DB Connection Obj. 
     * @param p Product to insert. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public void insert(Connection con, Product p) throws SQLException {
        String sql = """
            INSERT INTO media_store.produkt (
                produkt_nr, 
                produkttyp,
                titel,
                verkaufsrang,
                bild_url
            )
            VALUES (?, ?::media_store.produkttyp_enum, ?, ?, ?)
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, p.getAsin());
            stmt.setString(2, p.getType().name().toUpperCase());
            stmt.setString(3, p.getTitle());

            if (p.getSalesrank() != null) {
                stmt.setInt(4, p.getSalesrank());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            stmt.setString(5, p.getImgUrl());

            stmt.executeUpdate();
        }
    }
}
