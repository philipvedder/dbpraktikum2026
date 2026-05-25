package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.Music;

public class MusicRepository {
    private final LabelRepository labelRepository;
    private final PersonRepository personRepository;
    private final ProductRepository productRepository;

    public MusicRepository() {
        this.labelRepository = new LabelRepository();
        this.personRepository = new PersonRepository();
        this.productRepository = new ProductRepository();
    }

    public boolean exists(Connection con, String asin) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.musik_cd
            WHERE produkt_nr = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, asin);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    public void insert(Connection con, Music p) throws SQLException {
        productRepository.insert(con, p);
        insertBase(con, p);
        insertArtists(con, p);
        insertTracks(con, p);
    }

    public void insertBase(Connection con, Music p) throws SQLException {
        String sql = """
            INSERT INTO media_store.musik_cd (
                produkt_nr, 
                label_id,
                erscheinungsdatum
            )
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            Long labelId = labelRepository.findOrCreate(con, p.getLabelName());

            stmt.setString(1, p.getAsin());
            stmt.setLong(2, labelId);
            stmt.setDate(3, p.getReleaseDate());

            stmt.executeUpdate();
        }
    }

    public void insertArtists(Connection con, Music p) throws SQLException {
        String sql = """
            INSERT INTO media_store.musik_cd_kuenstler (
                produkt_nr, 
                person_id
            )
            VALUES (?, ?)
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            for (String artist : p.getArtistNames()) {
                Long personId = personRepository.findOrCreate(con, artist);

                stmt.setString(1, p.getAsin());
                stmt.setLong(2, personId);
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }

    public void insertTracks(Connection con, Music p) throws SQLException {
        String sql = """
            INSERT INTO media_store.musik_cd_titel (
                produkt_nr,
                name
            )
            VALUES (?, ?)
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            for (String track : p.getTrackNames()) {
                stmt.setString(1, p.getAsin());
                stmt.setString(2, track);
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
}
