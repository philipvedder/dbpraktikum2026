package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository to interact with the DB Tables:
 * format
 */
public class FormatRepository {

    /**
     * Checks if a Format with the specified ID already exists.
     * @param con DB Connection Obj. 
     * @param id String. The Format Id to search for.
     * @return boolean. True if entry exists, False otherwise. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean exists(Connection con, Long id) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.format
            WHERE format_id = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    /**
     * Finds a existent Format with the specified name, or creates one if none was found. 
     * Then returns the Id of that Format. 
     * @param con DB Connection Obj. 
     * @param name Strin. Name of Format to find or insert. 
     * @return Id of the found/created Format
     * @throws SQLException thrown on SQL execution problems.
     */
    public Long findOrCreate(Connection con, String name) throws SQLException {
        Long existingId = findIdByName(con, name);

        if (existingId != null) return existingId;

        return insert(con, name);
    }

    /**
     * Finds the Id of a Format from a given name. Returns NULL otherwise. 
     * @param con DB Connection Obj. 
     * @param name String. Name of the Format to find. 
     * @return Loong. Id of the corresponding Entry. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public Long findIdByName(Connection con, String name) throws SQLException {
        String sql = """
            SELECT format_id
            FROM media_store.format
            WHERE name = ?        
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet r = stmt.executeQuery()) {
                if (r.next()) {
                    return r.getLong("format_id");
                }
                
                return null;
            }
        }
    }

    /**
     * Insert a new Format with the given Name into DB. 
     * Returns the PK of the created Entry. 
     * @param con DB Connection Obj. 
     * @param name String. Name of Format to create. 
     * @return Long. The Id of the created Entry. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public long insert(Connection con, String name) throws SQLException {
        String sql = """
            INSERT INTO media_store.format (
                name
            )
            VALUES (?)
            RETURNING format_id
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet r= stmt.executeQuery()) {
                if (!r.next()) {
                    throw new SQLException("Could not insert format " + name);
                }

                return r.getLong("format_id");
            }
        }
    }
}
