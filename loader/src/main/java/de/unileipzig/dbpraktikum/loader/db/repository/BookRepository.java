package de.unileipzig.dbpraktikum.loader.db.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.model.Book;

public class BookRepository {
    private final PersonRepository personRepository;
    private final PublisherRepository publisherRepository;
    private final ProductRepository productRepository;

    public BookRepository() {
        this.personRepository = new PersonRepository();
        this.publisherRepository = new PublisherRepository();
        this.productRepository = new ProductRepository();
    }

    public boolean exists(Connection con, String asin) throws SQLException {
        String sql = """
            SELECT 1
            FROM media_store.buch
            WHERE produkt_nr = ?
        """;

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, asin);

            try (ResultSet r = stmt.executeQuery()) {
                return r.next();
            }
        }
    }

    public void insert(Connection con, Book p) throws SQLException {
        productRepository.insert(con, p);
        insertBase(con, p);
        insertAuthors(con, p);
    }


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
