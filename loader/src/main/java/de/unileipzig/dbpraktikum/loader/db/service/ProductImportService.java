package de.unileipzig.dbpraktikum.loader.db.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import de.unileipzig.dbpraktikum.exception.DuplicateException;
import de.unileipzig.dbpraktikum.exception.UnknownSQLException;
import de.unileipzig.dbpraktikum.loader.db.repository.BookRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.DVDRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.MusicRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.ProductRepository;
import de.unileipzig.dbpraktikum.loader.logger.ErrorLogger;
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
        this.productRepository = new ProductRepository();
        this.bookRepository = new BookRepository();
        this.musicRepository = new MusicRepository();
        this.dvdRepository = new DVDRepository();
    }

    public void importSingle(Connection con, Product p) throws SQLException, DuplicateException {
        if (this.productRepository.exists(con, p.getAsin())) {
            throw new DuplicateException(p.getAsin());
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

    public void importAll(Connection con, List<Product> products) {
        for (Product product : products) {
            try {
                importSingle(con, product);
            } catch (DuplicateException ex) {
                ErrorLogger.reportErrors(product.getAsin(), product.getType(), List.of(ex));
            } catch (SQLException ex) {
                ErrorLogger.reportErrors(product.getAsin(), product.getType(), List.of(new UnknownSQLException(product.getAsin(), ex.getMessage())));
            }
        }
    }
}
