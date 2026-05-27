package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository to interact with the DB Tables:
 * label
 */
public class LabelRepository {

    /**
     * Checks if a Label with the specified ID already exists.
     * @param con DB Connection Obj. 
     * @param id String. The Label Id to search for.
     * @return boolean. True if entry exists, False otherwise. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean exists(Connection con, Long id) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.label
            WHERE label_id = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    /**
     * Finds a existent Label with the specified name, or creates one if none was found. 
     * Then returns the Id of that Label. 
     * @param con DB Connection Obj. 
     * @param name Strin. Name of Label to find or insert. 
     * @return Id of the found/created Label
     * @throws SQLException thrown on SQL execution problems.
     */
    public Long findOrCreate(Connection con, String name) throws SQLException {
        Long existingId = findIdByName(con, name);

        if (existingId != null) return existingId;

        return insert(con, name);
    }

    /**
     * Finds the Id of a Label from a given name. Returns NULL otherwise. 
     * @param con DB Connection Obj. 
     * @param name String. Name of the Label to find. 
     * @return Loong. Id of the corresponding Entry. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public Long findIdByName(Connection con, String name) throws SQLException {
        String sql = """
            SELECT label_id
            FROM media_store.label
            WHERE name = ?        
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet r = stmt.executeQuery()) {
                if (r.next()) {
                    return r.getLong("label_id");
                }
                
                return null;
            }
        }
    }

    /**
     * Insert a new Label with the given Name into DB. 
     * Returns the PK of the created Entry. 
     * @param con DB Connection Obj. 
     * @param name String. Name of Label to create. 
     * @return Long. The Id of the created Entry. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public long insert(Connection con, String name) throws SQLException {
        String sql = """
            INSERT INTO media_store.label (
                name
            )
            VALUES (?)
            RETURNING label_id
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet r= stmt.executeQuery()) {
                if (!r.next()) {
                    throw new SQLException("Could not insert label " + name);
                }

                return r.getLong("label_id");
            }
        }
    }
}
