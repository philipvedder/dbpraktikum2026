package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository to interact with the DB Table:
 * Kunde
 */
public class CustomerRepository {

    /**
     * Checks if a Customer with the specified ID already exists.
     * @param con DB Connection Obj. 
     * @param id String. The Customer Id to search for.
     * @return boolean. True if entry exists, False otherwise. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean exists(Connection con, Long customerId) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.kunde
            WHERE kunde_id = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setLong(1, customerId);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    /**
     * Finds a existent Customer with the specified name, or creates one if none was found. 
     * Then returns the Id of that Customer. 
     * @param con DB Connection Obj. 
     * @param name String. Name of Customer to find or insert. 
     * @return Id of the found/created Customer
     * @throws SQLException thrown on SQL execution problems.
     */
    public Long findOrCreate(Connection con, String name) throws SQLException {
        Long existingId = findIdByName(con, name);

        if (existingId != null) return existingId;

        return insert(con, name);
    }

    /**
     * Finds the Id of a Customer from a given name. Returns NULL otherwise. 
     * @param con DB Connection Obj. 
     * @param name String. Name of the Customer to find. 
     * @return Long. Id of the corresponding Entry. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public Long findIdByName(Connection con, String name) throws SQLException {
        String sql = """
            SELECT kunde_id
            FROM media_store.kunde
            WHERE name = ?
            LIMIT 1
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet r = stmt.executeQuery()) {
                if (r.next()) {
                    return r.getLong("kunde_id");
                }

                return null;
            }
        }
    }

    /**
     * Insert a new Customer with the given Name into DB. 
     * Returns the PK of the created Entry. 
     * @param con DB Connection Obj. 
     * @param name String. Name of Customer to create. 
     * @return Long. The Id of the created Entry. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public Long insert(Connection con, String name) throws SQLException {
        String sql = """
            INSERT INTO media_store.kunde (
                name
            )
            VALUES (?)
            RETURNING kunde_id
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet r = stmt.executeQuery()) {
                if (!r.next()) {
                    throw new SQLException("Could not insert Kunde " + name);
                }

                return r.getLong("kunde_id");
            }
        }
    }
}