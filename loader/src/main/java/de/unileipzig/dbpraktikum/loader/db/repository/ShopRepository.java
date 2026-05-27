package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.Shop;

public class ShopRepository {
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

    public Long findOrCreate(Connection con, Shop shop) throws SQLException {
        Long existingId = findIdByName(con, shop.name());

        if (existingId != null) return existingId;

        return insert(con, shop);
    }

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
