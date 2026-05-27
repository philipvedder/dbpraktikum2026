package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.Music;

/**
 * Repository to interact with the DB Tables:
 * musik_cd
 * musik_cd_kuenstler 
 * musik_cd_titel
 */
public class MusicRepository {
    private final LabelRepository labelRepository;
    private final PersonRepository personRepository;
    private final ProductRepository productRepository;

    /**
     * Initialize required Repositories. 
     */
    public MusicRepository() {
        this.labelRepository = new LabelRepository();
        this.personRepository = new PersonRepository();
        this.productRepository = new ProductRepository();
    }

    /**
     * Checks if a MusicCD with the specified ID already exists.
     * @param con DB Connection Obj. 
     * @param id String. The Product Id.
     * @return boolean. True if entry exists, False otherwise. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean exists(Connection con, String id) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.musik_cd
            WHERE produkt_nr = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    /**
     * Inserts a new MusicCD object into the Database. 
     * Procedure:
     * 1. Add base data to Product Table
     * 2. Find or create entry in Label Table 
     * 3. Add specific data to MusicCD Table
     * 4. Find or create entry in Person Table for each Artist
     * 5. Add Relationships to Music_artists Table
     * 6. Create entry in MusicCD_Tracks Table for each Track. 
     * @param con DB Connection Obj. 
     * @param p The MusicCD to insert.
     * @throws SQLException thrown on SQL execution problems.
     */
    public void insert(Connection con, Music p) throws SQLException {
        productRepository.insert(con, p);
        insertBase(con, p);
        insertArtists(con, p);
        insertTracks(con, p);
    }

    /**
     * Inserts the specific MusicCD data into the MusicCD Table.
     * Also finds or creates the corresponding Label Entry and references it on MusicCD Table. 
     * @param con DB Connection Obj. 
     * @param p The MusicCD to insert.
     * @throws SQLException thrown on SQL execution problems.
     */
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

    /**
     * Find or create all Artists of a MusicCD object in the Person Table, and reference them to a MusicCD using the MusicCD_Artists Table. 
     * @param con DB Connection Obj. 
     * @param p The MusicCD to insert.
     * @throws SQLException thrown on SQL execution problems.
     */
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

    /**
     * Create all Tracks of a MusicCD object in the Track Table. 
     * @param con DB Connection Obj. 
     * @param p The MusicCD to insert.
     * @throws SQLException thrown on SQL execution problems.
     */
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
