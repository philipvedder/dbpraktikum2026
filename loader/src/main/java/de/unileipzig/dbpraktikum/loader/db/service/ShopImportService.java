package de.unileipzig.dbpraktikum.loader.db.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.unileipzig.dbpraktikum.loader.exception.*;
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

/**
 * Service class to import validated Shop data into the Database. 
 * Uses the repository classes to interact with the DB. 
 * Writes Exceptions which occur during this process to the Error log.
 */
public class ShopImportService {
    private final ShopRepository shopRepository;
    private final OfferRepository offerRepository;
    private final MusicRepository musicRepository;
    private final DVDRepository dvdRepository;
    private final BookRepository bookRepository;
    private final ProductRepository productRepository;
    private final SimilarRepository similarRepository;

    /**
     * Initialize all required Repos
     */
    public ShopImportService() {
        this.shopRepository = new ShopRepository();
        this.offerRepository = new OfferRepository();
        this.productRepository = new ProductRepository();
        this.bookRepository = new BookRepository();
        this.musicRepository = new MusicRepository();
        this.dvdRepository = new DVDRepository();
        this.similarRepository = new SimilarRepository();
    }

    /**
     * Import a Combination of two similar Product Ids into DB. Order of IDs does not matter. 
     * @param con DB Connection Obj. 
     * @param id1 First Product Id
     * @param id2 Second Product Id
     * @throws SQLException thrown on SQL execution problems.
     * @throws DuplicateException thrown if the Similarity relationship is already in the DB
     * @throws NotExistException thrown if one of the product ids corresponds to a product which is not in the DB. 
     */
    private void importSimilar(Connection con, String id1, String id2) throws SQLException, DuplicateException, NotExistException {
        //Check if realtionship already exists
        if (similarRepository.exists(con, id1, id2)) {
            throw new DuplicateException("Similar Product", id1 + "," + id2);
        }

        //check if product ids exist. 
        if (!productRepository.exists(con, id2)) {
            throw new NotExistException("Similar Product", id2);
        }

        if (!productRepository.exists(con, id1)) {
            throw new NotExistException("Similar Product", id1);
        }
    
        //Insert Relationship
        similarRepository.insert(con, id1, id2);
    }

    /**
     * Import a Product into the DB
     * @param con DB Connection Obj. 
     * @param p The Product to import. 
     * @param shopId The PK of the corresponding Shop object. 
     * @throws SQLException thrown on SQL execution problems.
     * @throws DuplicateException thrown if the Product is already in DB, and no new Offer was included. 
     */
    private void importProduct(Connection con, Product p, Long shopId) throws SQLException, DuplicateException {
        //Check if Product with ID already exists. 
        if (!this.productRepository.exists(con, p.getAsin())) {
            //Insert Product via corresponding Repo. 
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
        } else { //Product already in DB //TODO: Multiple OFFERS!
            if (p.getOffer() != null &&
                !offerRepository.exists(con, shopId, p.getAsin(), p.getOffer().state())) {
                //Even though the Product ID already is ion DB, the included Offer object is not. We will treat this as new information and do not throw a Exception. 
                System.out.println(
                    "WARNING: Product with ASIN " + p.getAsin() + " already in DB. " + 
                    "However, the Offer for this Shop is new and will be created. " +
                    "Therefore, this is not seen as an Error. Product insert is skipped. "
                );
            } else {
                //Product already in DB and no new Offer included. 
                throw new DuplicateException("Product", p.getAsin());
            }
        }

        //Insert new Offer for Shop / Product combination
        if (p.getOffer() != null) offerRepository.insert(con, shopId, p.getAsin(), p.getOffer());
    }

    /**
     * Import a Shop into the DB. Triggers the import of all corresponding Products, Similars, adn the Shop obj itself. 
     * Handles the Exceptions and write them to the ErrorLog. 
     * @param con DB Connection Obj. 
     * @param shop Shop object to import. 
     */
    public void importShop(Connection con, Shop shop) {
        System.out.println("Starting DB insertions...");

        //Get or create the Shop object
        Long shopId = null;
        try {
            shopId = shopRepository.findOrCreate(con, shop);
        } catch (SQLException ex) {
            //If the Shop obj cannot be created, we stop the execution early.
            //This will also be reported to the ErrorLog. 
            ErrorLogger.reportErrors("Shop " + shop.name(), List.of(new UnknownSQLException(shop.name(), ex.getMessage())));
            System.out.println("Error while creating Shop. Stopping early.");
            System.exit(1);
        }
    
        //Handle all Product objects. 
        int validCounter = 0;
        Map<String, String> similarIds = new HashMap<>(); //Map for all Similarities, as these can only be imported after all Products exist. 
        for (Product product : shop.products()) {
            try {
                //Import Product
                importProduct(con, product, shopId);
                validCounter++;

                //Add similarities to Map. 
                for (String similar : product.getSimilarProductIds()) {
                    similarIds.put(product.getAsin(), similar);
                }
            } catch (DuplicateException ex) {
                //Product already in DB 
                ErrorLogger.reportErrors(product.getAsin() + " - " + product.getType().name(), List.of(ex));
            } catch (SQLException ex) {
                //Error while executing SQL
                ErrorLogger.reportErrors(product.getAsin() + " - " + product.getType().name(), List.of(new UnknownSQLException(product.getAsin(), ex.getMessage())));
            }
        }

        //Handle all similar Relationships from Map, 
        for (Entry<String, String> similar : similarIds.entrySet()) {
            try {
                //import each Similatiry Relationship on its own. 
                importSimilar(con, similar.getKey(), similar.getValue());
            } catch (DuplicateException ex) {
                //Relationship already included
                ErrorLogger.reportErrors(similar.getKey() + " - Product Similarity", List.of(ex));
            } catch (NotExistException ex) {
                //At least one of the Product Ids is not in DB
                ErrorLogger.reportErrors(similar.getKey() + " - Product Similarity", List.of(ex));
            } catch (SQLException ex) {
                //Error while executing SQL
                ErrorLogger.reportErrors(similar.getKey() + " - Product Similarity", List.of(new UnknownSQLException(similar.getKey(), ex.getMessage())));
            }
        }

        //Stat report
        System.out.println(validCounter + " valid Products");
        System.out.println(shop.products().size() - validCounter + " invalid Products");
    }
}
