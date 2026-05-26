package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.Category;

public class CategoryRepository {
    public boolean exists(Connection con, String name, Long parentId) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.kategorie
            WHERE name = ? AND parent_kategorie_id = ?
        """;

        String sqlNoParent = """
            SELECT 1
            FROM media_store.kategorie
            WHERE name = ? AND parent_kategorie_id = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(parentId == null ? sqlNoParent : sql)) {
            stmt.setString(1, name);

            if (parentId != null) {
                stmt.setLong(2, parentId);
            }

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    public boolean itemExists(Connection con, String productId, Long categoryId) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.produkt_kategorie
            WHERE produkt_nr = ? AND kategorie_id = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, productId);
            stmt.setLong(2, categoryId);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    public boolean insertItem(Connection con, String productId, Long categoryId) throws SQLException {
        String sql = """
            INSERT INTO media_store.produkt_kategorie (
                produkt_nr,
                kategorie_id
            )
            VALUES (?, ?)
            RETURNING produkt_nr
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, productId);
            stmt.setLong(2, categoryId);

            try (ResultSet r= stmt.executeQuery()) {
                if (!r.next()) {
                    throw new SQLException("Could not insert Kategorie item " + productId + " into category id " + categoryId);
                }

                return true;
            }
        }
    }

    public Long findOrCreate(Connection con, Category c, Long parentId) throws SQLException {
        Long existingId = findIdByNameAndParent(con, c.name(), parentId);

        if (existingId != null) return existingId;

        return insert(con, c.name(), parentId);
    }

    public Long findIdByNameAndParent(Connection con, String name, Long parentId) throws SQLException {
        String sql = """
            SELECT kategorie_id
            FROM media_store.kategorie
            WHERE name = ? AND parent_kategorie_id = ?       
        """;

        String sqlNoParent = """
            SELECT kategorie_id
            FROM media_store.kategorie
            WHERE name = ? AND parent_kategorie_id IS NULL       
        """;

        try (PreparedStatement stmt = con.prepareStatement(parentId == null ? sqlNoParent : sql)) {
            stmt.setString(1, name);

            if (parentId != null) {
                stmt.setLong(2, parentId);
            }

            try (ResultSet r = stmt.executeQuery()) {
                if (r.next()) {
                    return r.getLong("kategorie_id");
                }
                
                return null;
            }
        }
    }

    public long insert(Connection con, String name, Long parentId) throws SQLException {
        String sql = """
            INSERT INTO media_store.kategorie (
                name,
                parent_kategorie_id
            )
            VALUES (?, ?)
            RETURNING kategorie_id
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, name);

            if (parentId == null) {
                stmt.setNull(2, java.sql.Types.BIGINT);
            } else {
                stmt.setLong(2, parentId);
            }

            try (ResultSet r= stmt.executeQuery()) {
                if (!r.next()) {
                    throw new SQLException("Could not insert Kategorie " + name + " with parent id " + parentId);
                }

                return r.getLong("kategorie_id");
            }
        }
    }
}
