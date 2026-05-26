package de.unileipzig.dbpraktikum.loader.db.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.unileipzig.dbpraktikum.exception.DuplicateException;
import de.unileipzig.dbpraktikum.exception.NotExistException;
import de.unileipzig.dbpraktikum.exception.UnknownSQLException;
import de.unileipzig.dbpraktikum.loader.db.repository.BookRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.DVDRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.MusicRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.OfferRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.ProductRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.ShopRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.SimilarRepository;
import de.unileipzig.dbpraktikum.loader.logger.ErrorLogger;
import de.unileipzig.dbpraktikum.loader.model.Book;
import de.unileipzig.dbpraktikum.loader.model.DVD;
import de.unileipzig.dbpraktikum.loader.model.Music;
import de.unileipzig.dbpraktikum.loader.model.Product;
import de.unileipzig.dbpraktikum.loader.model.Shop;

public class ShopImportService {
    private final ShopRepository shopRepository;
    private final OfferRepository offerRepository;
    private final MusicRepository musicRepository;
    private final DVDRepository dvdRepository;
    private final BookRepository bookRepository;
    private final ProductRepository productRepository;
    private final SimilarRepository similarRepository;

    public ShopImportService() {
        this.shopRepository = new ShopRepository();
        this.offerRepository = new OfferRepository();
        this.productRepository = new ProductRepository();
        this.bookRepository = new BookRepository();
        this.musicRepository = new MusicRepository();
        this.dvdRepository = new DVDRepository();
        this.similarRepository = new SimilarRepository();
    }

    public void importSimilar(Connection con, String id1, String id2) throws SQLException, DuplicateException, NotExistException {
        if (similarRepository.exists(con, id1, id2)) {
            throw new DuplicateException("Similar Product", id1 + "," + id2);
        }

        if (!productRepository.exists(con, id2)) {
            throw new NotExistException("Similar Product", id2);
        }

        if (!productRepository.exists(con, id1)) {
            throw new NotExistException("Similar Product", id1);
        }

        similarRepository.insert(con, id1, id2);
    }

    public void importProduct(Connection con, Product p, Long shopId) throws SQLException, DuplicateException {
        if (!this.productRepository.exists(con, p.getAsin())) {
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
        } else {
            if (p.getOffer() != null &&
                !offerRepository.exists(con, shopId, p.getAsin())) {
                System.out.println(
                    "WARNING: Product with ASIN " + p.getAsin() + " already in DB. " + 
                    "However, the Offer for this Shop is new and will be created. " +
                    "Therefore, this is not seen as an Error. Product insert is skipped. "
                );
            } else {
                throw new DuplicateException("Product", p.getAsin());
            }
        }

        //Insert new Offer for Shop / Product combination
        if (p.getOffer() != null) offerRepository.insert(con, shopId, p.getAsin(), p.getOffer());
    }

    public void importShop(Connection con, Shop shop) {
        System.out.println("Starting DB insertions...");

        //Get or create Shop obj
        Long shopId = null;
        try {
            shopId = shopRepository.findOrCreate(con, shop);
        } catch (SQLException ex) {
            ErrorLogger.reportErrors("Shop " + shop.name(), List.of(new UnknownSQLException(shop.name(), ex.getMessage())));
            System.out.println("Error while creating Shop. Stopping early.");
            System.exit(1);
        }
    
        //Handle every Product
        int validCounter = 0;
        Map<String, String> similarIds = new HashMap<>();
        for (Product product : shop.products()) {
            try {
                importProduct(con, product, shopId);
                validCounter++;

                for (String similar : product.getSimilarProductIds()) {
                    similarIds.put(product.getAsin(), similar);
                }
            } catch (DuplicateException ex) {
                ErrorLogger.reportErrors(product.getAsin() + " - " + product.getType().name(), List.of(ex));
            } catch (SQLException ex) {
                ErrorLogger.reportErrors(product.getAsin() + " - " + product.getType().name(), List.of(new UnknownSQLException(product.getAsin(), ex.getMessage())));
            }
        }

        for (Entry<String, String> similar : similarIds.entrySet()) {
            try {
                importSimilar(con, similar.getKey(), similar.getValue());
            } catch (DuplicateException ex) {
                ErrorLogger.reportErrors(similar.getKey(), List.of(ex)); //TODO: Type
            } catch (NotExistException ex) {
                ErrorLogger.reportErrors(similar.getKey(), List.of(ex));
            } catch (SQLException ex) {
                ErrorLogger.reportErrors(similar.getKey(), List.of(new UnknownSQLException(similar.getKey(), ex.getMessage())));
            }
        }

        System.out.println(validCounter + " valid Products");
        System.out.println(shop.products().size() - validCounter + " invalid Products");
    }
}
