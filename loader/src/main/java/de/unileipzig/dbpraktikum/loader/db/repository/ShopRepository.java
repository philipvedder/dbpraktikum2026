package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.Shop;

/**
 * Repository to interact with the DB Tables:
 * filiale
 */
public class ShopRepository {
    /**
     * Checks if a Shop with the specified ID already exists.
     * @param con DB Connection Obj. 
     * @param id String. The Shop Id to search for.
     * @return boolean. True if entry exists, False otherwise. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean exists(Connection con, Long id) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.filiale
            WHERE filiale_id = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    /**
     * Finds a existent Shop with the specified name, or creates one if none was found. 
     * Then returns the Id of that Shop. 
     * @param con DB Connection Obj. 
     * @param name Strin. Name of Shop to find or insert. 
     * @return Id of the found/created Shop
     * @throws SQLException thrown on SQL execution problems.
     */
    public Long findOrCreate(Connection con, Shop shop) throws SQLException {
        Long existingId = findIdByName(con, shop.name());

        if (existingId != null) return existingId;

        return insert(con, shop);
    }

    /**
     * Finds the Id of a Shop from a given name. Returns NULL otherwise. 
     * @param con DB Connection Obj. 
     * @param name String. Name of the Shop to find. 
     * @return Loong. Id of the corresponding Entry. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public Long findIdByName(Connection con, String name) throws SQLException {
        String sql = """
            SELECT filiale_id
            FROM media_store.filiale
            WHERE name = ?        
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet r = stmt.executeQuery()) {
                if (r.next()) {
                    return r.getLong("filiale_id");
                }
                
                return null;
            }
        }
    }

    /**
     * Insert a new Shop with the given Name into DB. 
     * Returns the PK of the created Entry. 
     * @param con DB Connection Obj. 
     * @param name String. Name of Shop to create. 
     * @return Long. The Id of the created Entry. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public long insert(Connection con, Shop shop) throws SQLException {
        String sql = """
            INSERT INTO media_store.filiale (
                name,
                strasse,
                plz
            )
            VALUES (?, ?, ?)
            RETURNING filiale_id
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, shop.name());
            stmt.setString(2, shop.street());
            stmt.setString(3, shop.zip());

            try (ResultSet r= stmt.executeQuery()) {
                if (!r.next()) {
                    throw new SQLException("Could not insert Shop " + shop.name());
                }

                return r.getLong("filiale_id");
            }
        }
    }
}
