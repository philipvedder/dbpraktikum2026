package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SimilarRepository {
    public boolean exists(Connection con, String id_1, String id_2) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.aehnliches_produkt
            WHERE (produkt_nr_1 = ? AND produkt_nr_2 = ?) OR (produkt_nr_2 = ? AND produkt_nr_1 = ?)
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, id_1);
            stmt.setString(2, id_2);
            stmt.setString(3, id_1);
            stmt.setString(4, id_2);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

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
