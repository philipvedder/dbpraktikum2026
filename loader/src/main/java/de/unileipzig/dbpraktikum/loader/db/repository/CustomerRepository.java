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

    public Long findOrCreate(Connection con, String name) throws SQLException {
        Long existingId = findIdByName(con, name);

        if (existingId != null) return existingId;

        return insert(con, name);
    }

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