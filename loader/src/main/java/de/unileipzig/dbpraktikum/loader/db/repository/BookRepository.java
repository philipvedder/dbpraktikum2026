package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.Book;

/**
 * Repository to interact with the DB Tables:
 * Buch
 * Buch_Autor 
 */
public class BookRepository {
    private final PersonRepository personRepository;
    private final PublisherRepository publisherRepository;
    private final ProductRepository productRepository;

    /**
     * Initialize required Repositories. 
     */
    public BookRepository() {
        this.personRepository = new PersonRepository();
        this.publisherRepository = new PublisherRepository();
        this.productRepository = new ProductRepository();
    }

    /**
     * Checks if a Book with the specified ID already exists.
     * @param con DB Connection Obj. 
     * @param id String. The Product Id.
     * @return boolean. True if entry exists, False otherwise. 
     * @throws SQLException thrown on SQL execution problems.
     */
    public boolean exists(Connection con, String id) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.buch
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
     * Inserts a new Book object into the Database. 
     * Procedure:
     * 1. Add base data to Product Table
     * 2. Find or create entry in Publisher Table 
     * 3. Add specific data to Book Table
     * 4. Find or create entry in Person Table for each Author
     * 5. Add Relationships to Book_Author Table
     * @param con DB Connection Obj. 
     * @param p The Book to insert.
     * @throws SQLException thrown on SQL execution problems.
     */
    public void insert(Connection con, Book p) throws SQLException {
        productRepository.insert(con, p);
        insertBase(con, p);
        insertAuthors(con, p);
    }

    /**
     * Inserts the specific Book data into the Book Table.
     * Also finds or creates the corresponding Publisher Entry and references it on Book Table. 
     * @param con DB Connection Obj. 
     * @param p The Book to insert.
     * @throws SQLException thrown on SQL execution problems.
     */
    public void insertBase(Connection con, Book p) throws SQLException {
        String sql = """
            INSERT INTO media_store.buch (
                produkt_nr, 
                seitenzahl,
                erscheinungsdatum,
                isbn,
                verlag_id
            )
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            Long publisherId = publisherRepository.findOrCreate(con, p.getPublisherName());

            stmt.setString(1, p.getAsin());
            stmt.setInt(2, p.getPages());
            stmt.setDate(3, p.getPublication());
            stmt.setString(4, p.getIsbn());
            stmt.setLong(5, publisherId);

            stmt.executeUpdate();
        }
    }

    /**
     * Find or create all Authors of a Book object in the Person Table, and reference them to a Book using the Book_Author Table. 
     * @param con DB Connection Obj. 
     * @param p The Book to insert.
     * @throws SQLException thrown on SQL execution problems.
     */
    public void insertAuthors(Connection con, Book p) throws SQLException {
        String sql = """
            INSERT INTO media_store.buch_autor (
                produkt_nr, 
                person_id
            )
            VALUES (?, ?)
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            for (String author : p.getAuthorNames()) {
                Long personId = personRepository.findOrCreate(con, author);

                stmt.setString(1, p.getAsin());
                stmt.setLong(2, personId);
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
}
