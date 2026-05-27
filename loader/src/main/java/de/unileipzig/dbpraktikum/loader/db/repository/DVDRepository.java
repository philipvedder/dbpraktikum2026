package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.DVD;

/**
 * Repository to interact with the DB Tables:
 * dvd
 * dvd_beteiligung
 */
public class DVDRepository {
    private final PersonRepository personRepository;
    private final ProductRepository productRepository;

    /**
     * Initialize required Repositories. 
     */
    public DVDRepository() {
        this.personRepository = new PersonRepository();
        this.productRepository = new ProductRepository();
    }

    /**
     * Checks if a DVD with the specified ID already exists.
     * @param con DB Connection Obj. 
     * @param id String. The Product Id.
     * @return boolean. True if entry exists, False otherwise. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean exists(Connection con, String id) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.dvd
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
     * Inserts a new DVD object into the Database. 
     * Procedure:
     * 1. Add base data to Product Table
     * 2. Add specific data to DVD Table
     * 3. Find or create entry in Person Table for each Participation
     * 4. Add Relationships to DVD_Participation Table
     * @param con DB Connection Obj. 
     * @param p The DVD to insert.
     * @throws SQLException thrown on SQL execution problems.
     */
    public void insert(Connection con, DVD p) throws SQLException {
        productRepository.insert(con, p);
        insertBase(con, p);
        insertParticipations(con, p);
    }

    /**
     * Inserts the specific DVD data into the DVD Table.
     * @param con DB Connection Obj. 
     * @param p The DVD to insert.
     * @throws SQLException thrown on SQL execution problems.
     */
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

    /**
     * Find or create all Participations of a DVD object in the Person Table, and reference them to a DVD using the DVD_Participation Table. 
     * Each Participation is given a Role from the corresponding ENUM. 
     * @param con DB Connection Obj. 
     * @param p The DVD to insert.
     * @throws SQLException thrown on SQL execution problems.
     */
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
            for (String person : p.getActorNames()) {
                Long personId = personRepository.findOrCreate(con, person);

                stmt.setString(1, p.getAsin());
                stmt.setLong(2, personId);
                stmt.setString(3, "ACTOR");
                stmt.addBatch();
            }

            for (String person : p.getCreatorNames()) {
                Long personId = personRepository.findOrCreate(con, person);

                stmt.setString(1, p.getAsin());
                stmt.setLong(2, personId);
                stmt.setString(3, "CREATOR");
                stmt.addBatch();
            }

            for (String person : p.getDirectorNames()) {
                Long personId = personRepository.findOrCreate(con, person);

                stmt.setString(1, p.getAsin());
                stmt.setLong(2, personId);
                stmt.setString(3, "DIRECTOR");
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
}
