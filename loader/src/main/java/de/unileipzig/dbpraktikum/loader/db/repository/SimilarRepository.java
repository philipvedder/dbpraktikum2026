package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository to interact with the DB Tables:
 * aehnliches_produkt
 */
public class SimilarRepository {

    /**
     * Checks if a Similarity with the specified Product ID combination (id1, id2) already exists.
     * The order of the IDs is not relevant. Both will be checked. 
     * @param con DB Connection Obj. 
     * @param id1 String. The Product Id of the first Product
     * @param id2 String. The Product Id of the second Product
     * @return boolean. True if entry exists, False otherwise. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean exists(Connection con, String id1, String id2) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.aehnliches_produkt
            WHERE (produkt_nr_1 = ? AND produkt_nr_2 = ?) OR (produkt_nr_2 = ? AND produkt_nr_1 = ?)
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, id1);
            stmt.setString(2, id2);
            stmt.setString(3, id1);
            stmt.setString(4, id2);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    /**
     * Insert a new Similarity into DB. 
     * @param con DB Connection Obj. 
     * @param id_1 String. The Product Id of the first Product
     * @param id_2 String. The Product Id of the second Product
     * @return boolean. True if successful. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean insert(Connection con, String id_1, String id_2) throws SQLException {
        String sql = """
            INSERT INTO media_store.aehnliches_produkt (
                produkt_nr_1,
                produkt_nr_2
            )
            VALUES (?, ?)
            RETURNING produkt_nr_1
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, id_1);
            stmt.setString(2, id_2);

            try (ResultSet r= stmt.executeQuery()) {
                if (!r.next()) {
                    throw new SQLException("Could not insert similarity " + id_1 + " : " + id_2);
                }

                return true;
            }
        }
    }
}
