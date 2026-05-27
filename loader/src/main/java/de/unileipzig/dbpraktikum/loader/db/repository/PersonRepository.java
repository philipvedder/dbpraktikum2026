package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository to interact with the DB Tables:
 * person
 */
public class PersonRepository {

    /**
     * Checks if a Person with the specified ID already exists.
     * @param con DB Connection Obj. 
     * @param id String. The Person Id to search for.
     * @return boolean. True if entry exists, False otherwise. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean exists(Connection con, Long id) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.person
            WHERE person_id = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    /**
     * Finds a existent Person with the specified name, or creates one if none was found. 
     * Then returns the Id of that Person. 
     * @param con DB Connection Obj. 
     * @param name String. Name of Person to find or insert. 
     * @return Id of the found/created Person
     * @throws SQLException thrown on SQL execution problems.
     */
    public Long findOrCreate(Connection con, String name) throws SQLException {
        Long existingId = findIdByName(con, name);

        if (existingId != null) return existingId;

        return insert(con, name);
    }

    /**
     * Finds the Id of a Person from a given name. Returns NULL otherwise. 
     * @param con DB Connection Obj. 
     * @param name String. Name of the Person to find. 
     * @return Long. Id of the corresponding Entry. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public Long findIdByName(Connection con, String name) throws SQLException {
        String sql = """
            SELECT person_id
            FROM media_store.person
            WHERE name = ?        
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet r = stmt.executeQuery()) {
                if (r.next()) {
                    return r.getLong("person_id");
                }
                
                return null;
            }
        }
    }

    /**
     * Insert a new Person with the given Name into DB. 
     * Returns the PK of the created Entry. 
     * @param con DB Connection Obj. 
     * @param name String. Name of Person to create. 
     * @return Long. The Id of the created Entry. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public long insert(Connection con, String name) throws SQLException {
        String sql = """
            INSERT INTO media_store.person (
                name
            )
            VALUES (?)
            RETURNING person_id
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet r= stmt.executeQuery()) {
                if (!r.next()) {
                    throw new SQLException("Could not insert Person " + name);
                }

                return r.getLong("person_id");
            }
        }
    }
}
