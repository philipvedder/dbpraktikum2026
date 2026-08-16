package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.Category;

/**
 * Repository to interact with the DB Tables:
 * kategorie
 * produkt_kategorie 
 */
public class CategoryRepository {
    /**
     * Checks if a Category with the specified (name, parentId) already exists. 
     * @param con DB Connection Obj. 
     * @param name String. Name of Category. 
     * @param parentId Long. PK of parent ID. Can be NULL if Category has no parent. 
     * @return boolean. True if entry found, false otherwise. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean exists(Connection con, String name, Long parentId) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.kategorie
            WHERE name = ? AND parent_kategorie_id = ?
        """;

        //Safe SQL for NULL check. 
        String sqlNoParent = """
            SELECT 1
            FROM media_store.kategorie
            WHERE name = ? AND parent_kategorie_id IS NULL
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

    /**
     * Checks if a Category already contains the specified Product Id
     * @param con DB Connection Obj. 
     * @param productId String. Product Id to find. 
     * @param categoryId Long. PK of Category to search in.
     * @return boolean. True if found, false otherwise. 
     * @throws SQLException thrown on SQL execution problems.
     */
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

    /**
     * Inserts a new ProductId into a given Category. 
     * @param con DB Connection Obj. 
     * @param productId Product ID to add to Category
     * @param categoryId PK of Category to add to. 
     * @return boolean. True on successful insert. 
     * @throws SQLException thrown on SQL execution problems.
     */
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

    /**
     * Finds a existent Category with (name, parentId), or creates one if none was found. 
     * Then returns the Id of that category. 
     * @param con DB Connection Obj. 
     * @param category Category object to find or create
     * @param parentId PK of parent Category. Can be NULL. 
     * @return Id of the found/created Category
     * @throws SQLException thrown on SQL execution problems.
     */
    public Long findOrCreate(Connection con, Category category, Long parentId) throws SQLException {
        Long existingId = findIdByNameAndParent(con, category.name(), parentId);

        if (existingId != null) return existingId;

        return insert(con, category.name(), parentId);
    }

    /**
     * Returns the PK id of a Category from a given (name, parentId) combination. 
     * @param con DB Connection Obj. 
     * @param name String. name of Category. 
     * @param parentId Long. PK of parent Category. Can be NULL. 
     * @return The Id of the found category as Long. NULL if not found. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public Long findIdByNameAndParent(Connection con, String name, Long parentId) throws SQLException {
        String sql = """
            SELECT kategorie_id
            FROM media_store.kategorie
            WHERE name = ? AND parent_kategorie_id = ?       
        """;

        //Safe NULL specific SQL
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

    /**
     * Insert a new Category from a given (name, parentId) combination. 
     * @param con DB Connection Obj. 
     * @param name String. Name of the Category to insert. 
     * @param parentId Long. Id of the Parent Category. Can be NULL. 
     * @return long. Id of the inserted object. 
     * @throws SQLException thrown on SQL execution problems.
     */
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
