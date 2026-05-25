package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.DVD;

public class DVDRepository {
    private final PersonRepository personRepository;
    private final ProductRepository productRepository;

    public DVDRepository() {
        this.personRepository = new PersonRepository();
        this.productRepository = new ProductRepository();
    }

    public boolean exists(Connection con, String asin) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.dvd
            WHERE produkt_nr = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, asin);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    public void insert(Connection con, DVD p) throws SQLException {
        productRepository.insert(con, p);
        insertBase(con, p);
        insertParticipations(con, p);
    }

    public void insertBase(Connection con, DVD p) throws SQLException {
        String sql = """
            INSERT INTO media_store.dvd (
                produkt_nr, 
                format,
                laufzeit_minuten,
                region_code
            )
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, p.getAsin());
            stmt.setString(2, p.getFormat());
            stmt.setInt(3, p.getRunningtime());
            stmt.setInt(4, p.getRegioncode());

            stmt.executeUpdate();
        }
    }

    public void insertParticipations(Connection con, DVD p) throws SQLException {
        String sql = """
            INSERT INTO media_store.dvd_beteiligung (
                produkt_nr, 
                person_id,
                rolle
            )
            VALUES (?, ?, ?::media_store.dvd_rolle_enum)
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            for (String actor : p.getActorNames()) {
                Long personId = personRepository.findOrCreate(con, actor);

                stmt.setString(1, p.getAsin());
                stmt.setLong(2, personId);
                stmt.setString(3, "ACTOR");
                stmt.addBatch();
            }

            for (String actor : p.getCreatorNames()) {
                Long personId = personRepository.findOrCreate(con, actor);

                stmt.setString(1, p.getAsin());
                stmt.setLong(2, personId);
                stmt.setString(3, "CREATOR");
                stmt.addBatch();
            }

            for (String actor : p.getDirectorNames()) {
                Long personId = personRepository.findOrCreate(con, actor);

                stmt.setString(1, p.getAsin());
                stmt.setLong(2, personId);
                stmt.setString(3, "DIRECTOR");
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
}
