package de.unileipzig.dbpraktikum.loader.db.service;

import java.sql.Connection;
import java.sql.SQLException;

import de.unileipzig.dbpraktikum.loader.db.repository.BookRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.DVDRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.MusicRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.ProductRepository;
import de.unileipzig.dbpraktikum.loader.model.Book;
import de.unileipzig.dbpraktikum.loader.model.DVD;
import de.unileipzig.dbpraktikum.loader.model.Music;
import de.unileipzig.dbpraktikum.loader.model.Product;

public class ProductImportService {
    private final MusicRepository musicRepository;
    private final DVDRepository dvdRepository;
    private final BookRepository bookRepository;
    private final ProductRepository productRepository;

    public ProductImportService() {
        this.bookRepository = new BookRepository();
        this.musicRepository = new MusicRepository();
        this.dvdRepository = new DVDRepository();
        this.productRepository = new ProductRepository();
    }

    public void importProduct(Connection con, Product p) throws SQLException {
        if (this.productRepository.exists(con, p.getAsin())) {
            throw new SQLException("Duplicate product with ASIN: " + p.getAsin());
        }

        switch (p.getType()) {
            case MUSIC_CD:
                musicRepository.insert(con, (Music) p);
                break;
            case BOOK:
                bookRepository.insert(con, (Book) p);
                break;
            case DVD:
                dvdRepository.insert(con, (DVD) p);
                break;
            default:
                break;
        }
    }
}
