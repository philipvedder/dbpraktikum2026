package de.unileipzig.dbpraktikum.cli_interface.db_interface;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.SelectionQuery;

import de.unileipzig.dbpraktikum.cli_interface.model.Book;
import de.unileipzig.dbpraktikum.cli_interface.model.BookTrack;
import de.unileipzig.dbpraktikum.cli_interface.model.CD;
import de.unileipzig.dbpraktikum.cli_interface.model.Category;
import de.unileipzig.dbpraktikum.cli_interface.model.Customer;
import de.unileipzig.dbpraktikum.cli_interface.model.DVD;
import de.unileipzig.dbpraktikum.cli_interface.model.DVDParticipation;
import de.unileipzig.dbpraktikum.cli_interface.model.Format;
import de.unileipzig.dbpraktikum.cli_interface.model.Label;
import de.unileipzig.dbpraktikum.cli_interface.model.Offer;
import de.unileipzig.dbpraktikum.cli_interface.model.Person;
import de.unileipzig.dbpraktikum.cli_interface.model.Product;
import de.unileipzig.dbpraktikum.cli_interface.model.PurchaseOrder;
import de.unileipzig.dbpraktikum.cli_interface.model.Publisher;
import de.unileipzig.dbpraktikum.cli_interface.model.Review;
import de.unileipzig.dbpraktikum.cli_interface.model.Shop;
import de.unileipzig.dbpraktikum.cli_interface.model.MusicTrack;
import de.unileipzig.dbpraktikum.cli_interface.model.dto.ProductListEntry;
import jakarta.persistence.NoResultException;

public class DBInterfaceImpl implements DBInterface {
    /**
     * Implementation of the DBInterface, using Hibernate to interact with PostgreSQL. 
     */
    private SessionFactory sessionFactory;

    /**
     * Initializes a DB instance from given Hibernate properties.
     * @param properties Properties for DB instance creation
     * @throws IllegalArgumentException if properties is null
     * @throws HibernateException on an illegal configuration
     */
    @Override
    public void init(Properties properties) {
        // Ensure sessionfactory is not already initialized
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            return;
        }

        if (properties == null) {
            throw new IllegalArgumentException("Database properties are required.");
        }

        // Construct configuration from the properties supplied by the application.
        Configuration config = new Configuration();
        config.addProperties(properties);

        config.addAnnotatedClass(Product.class);
        config.addAnnotatedClass(Book.class);
        config.addAnnotatedClass(CD.class);
        config.addAnnotatedClass(Customer.class);
        config.addAnnotatedClass(DVD.class);
        config.addAnnotatedClass(DVDParticipation.class);
        config.addAnnotatedClass(Format.class);
        config.addAnnotatedClass(Label.class);
        config.addAnnotatedClass(Offer.class);
        config.addAnnotatedClass(Person.class);
        config.addAnnotatedClass(Publisher.class);
        config.addAnnotatedClass(PurchaseOrder.class);
        config.addAnnotatedClass(Review.class);
        config.addAnnotatedClass(Shop.class);
        config.addAnnotatedClass(MusicTrack.class);
        config.addAnnotatedClass(BookTrack.class);
        config.addAnnotatedClass(Category.class);

        sessionFactory = config.buildSessionFactory();
    }

    /**
     * Closes the held DB Connections and releases memory
     */
    @Override
    public void finish() {
        // Close sessionfactory if available
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }   

        sessionFactory = null;
    }

    /**
     * Get Product data for a given product id. 
     * Returns null if product does not exist.
     * @param pid Product id
     * @return The loaded Product
     * @throws IllegalStateException if db was not initialized
     */
    @Override
    public Product getProduct(String pid) {
        checkInitialized();

        Product p = null;

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            p = (Product) session.get(Product.class, pid);

            if (p == null) {
                t.commit();
                return null;
            }

            // Trigger Lazy loads
            Hibernate.initialize(p.getSimilarProducts());
            Hibernate.initialize(p.getReviews());
            Hibernate.initialize(p.getCategories());

            t.commit();
        }

        return p;
    }

    /**
     * Get ProductListEntry's which name matches the given pattern.
     * @param pattern name pattern to search for
     * @return List of ProductListEntry's. 
     * @throws IllegalStateException if db was not initialized
     */
    @Override
    public List<ProductListEntry> getProducts(String pattern) {
        checkInitialized();

        List<ProductListEntry> products = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            String queryString =
                "select new de.unileipzig.dbpraktikum.cli_interface.model.dto.ProductListEntry(p.id, p.title, p.type, p.avgRating, p.ratingQuantity) from Product p";

            if (pattern != null) {
                queryString += " where lower(p.title) like lower(:pattern)";
            }
            queryString += " order by p.title";

            SelectionQuery<ProductListEntry> query = session.createSelectionQuery(queryString, ProductListEntry.class);
            if (pattern != null) {
                query.setParameter("pattern", pattern);
            }
            products = query.getResultList();

            t.commit();
        }

        return products;
    }

    /**
     * Get the full Category tree, meaning the root categories and recursively all subcategories. 
     * @return One artificial root Category object, holding the root Categories. 
     * @throws IllegalStateException if db was not initialized
     */
    @Override
    public Category getCategoryTree() {
        checkInitialized();

        List<Category> roots = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            // Get Roots
            roots = session.createSelectionQuery(
                "from Category c where parent IS NULL",
                Category.class
            )
            .getResultList();

            // Trigger Lazy Loading of Childs, recursively
            for (Category root : roots) {
                initializeCategoryTree(root);
            }

            t.commit();
        }

        // The database can contain several top-level categories. The interface contract exposes one complete tree, so they are grouped below a non-persistent root.
        return Category.createTreeRoot(roots);
    }

    /**
     * Get all Products that are in the Category, given as a path given by a list of Strings, f.e.
     * "dvds", "under 1 EUR", "german" -> DVDS>>Under 1 EUR>>GERMAN
     * @param categoryPath Category Path as list of Strings
     * @return List of Products in Category
     * @throws IllegalStateException if db was not initialized
     * @throws IllegalArgumentException for invalid paths
     */
    @Override
    public List<Product> getProductsByCategoryPath(List<String> categoryPath) {
        checkInitialized();

        if (categoryPath == null || categoryPath.isEmpty()) {
            throw new IllegalArgumentException("A category path is required.");
        }
        for (String part : categoryPath) {
            if (part == null || part.trim().isEmpty()) {
                throw new IllegalArgumentException("The category path contains an empty name.");
            }
        }

        List<Product> products = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            List<Category> currentLevel = session.createSelectionQuery(
                "from Category c where c.parent is null",
                Category.class
            )
            .getResultList();

            Category selected = null;
            for (String pathPart : categoryPath) {
                selected = null;
                for (Category candidate : currentLevel) {
                    if (Objects.equals(candidate.getName(), pathPart)) {
                        selected = candidate;
                        break;
                    }
                }

                if (selected == null) {
                    t.commit();
                    return products;
                }

                Hibernate.initialize(selected.getChilds());
                currentLevel = selected.getChilds();
            }

            Hibernate.initialize(selected.getProducts());
            products = new ArrayList<>(selected.getProducts());

            t.commit();
        }

        return products;
    }

    /**
     * Get the k top products, measured and ordered by avg_rating and rating_quantity.
     * @param k number of Top products to return
     * @return List of ProductListEntries for the found products
     * @throws IllegalStateException if db was not initialized
     * @throws IllegalArgumentException for non-positive k
     */
    @Override
    public List<ProductListEntry> getTopProducts(int k) {
        checkInitialized();

        if (k <= 0) {
            throw new IllegalArgumentException("The number of products must be positive.");
        }

        List<ProductListEntry> products = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            // Select the requested number of products, ordered by rating average and quantity
            products = session.createSelectionQuery(
                "select new de.unileipzig.dbpraktikum.cli_interface.model.dto.ProductListEntry(p.id, p.title, p.type, p.avgRating, p.ratingQuantity) from Product p ORDER BY p.avgRating DESC NULLS LAST, p.ratingQuantity DESC, p.id",
                ProductListEntry.class
            )
            .setMaxResults(k)
            .getResultList();

            t.commit();
        }

        return products;
    }

    /**
     * Get all similar products of a given products, which include a cheaper offer. 
     * @param p Product to search for
     * @return List of all similar Products with cheaper offers
     * @throws IllegalStateException if db was not initialized
     * @throws IllegalArgumentException for empty p
     */
    @Override
    public List<Product> getSimilarCheaperProduct(Product p) {
        checkInitialized();

        if (p == null || p.getId() == null) {
            throw new IllegalArgumentException("A product is required.");
        }

        List<Product> result = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            Product managed = (Product) session.get(Product.class, p.getId());

            if (managed == null) {
                t.commit();
                return result;
            }

            Map<String, BigDecimal> originalMinPrices = getMinimumPricesByCurrency(managed);

            // No offers for product, so no cheaper products available
            if (originalMinPrices.isEmpty()) {
                t.commit();
                return result;
            }

            // Get similar products
            Set<Product> similars = managed.getSimilarProducts();

            // Prices can only be compared when their currencies match.
            for (Product sim : similars) {
                if (hasCheaperOffer(sim, originalMinPrices)) {
                    result.add(sim);
                }
            }

            t.commit();
        }

        return result;
    }

    /**
     * Add a new Review to a Product
     * @param p Product
     * @param username Username. Will create user if not found. 
     * @param points Number of Points between 1 and 5
     * @param text Optional review text
     * @return The added review if successful
     * @throws IllegalStateException if db was not initialized
     * @throws IllegalArgumentException for empty p
     * @throws IllegalArgumentException for empty or longer than 256 chars username
     * @throws IllegalArgumentException for points outside of range 1-5
     */
    @Override
    public Review addNewReview(Product p, String username, int points, String text) {
        checkInitialized();

        if (p == null || p.getId() == null) {
            throw new IllegalArgumentException("A product is required.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Enter a username.");
        }
        if (username.trim().length() > 256) {
            throw new IllegalArgumentException("The username must not exceed 256 characters.");
        }
        if (points < 1 || points > 5) {
            throw new IllegalArgumentException("Points must be between 1 and 5.");
        }

        Review newReview = null;

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            try {
                Product managedProduct = session.get(Product.class, p.getId());
                if (managedProduct == null) {
                    throw new IllegalArgumentException("The product does not exist.");
                }

                Customer user;
                try {
                    // Get user if exists
                    user = session.createSelectionQuery(
                        "from Customer c where name = :username",
                        Customer.class
                    )
                    .setParameter("username", username.trim())
                    .getSingleResult();
                } catch (NoResultException e) {
                    // Create user
                    user = new Customer();
                    user.setName(username.trim());
                    session.persist(user);
                    session.flush();
                }

                // Create Review for user
                Timestamp databaseTimestamp = session.createSelectionQuery(
                    "select current_timestamp from Product p where p.id = :productId",
                    Timestamp.class
                )
                .setParameter("productId", managedProduct.getId())
                .getSingleResult();

                newReview = new Review();
                newReview.setCustomer(user);
                newReview.setDate(databaseTimestamp);
                newReview.setPoints(points);
                newReview.setText(text);
                newReview.setProduct(managedProduct);

                session.persist(newReview);
                t.commit();
            } catch (RuntimeException ex) {
                if (t.isActive()) {
                    t.rollback();
                }
                throw ex;
            }
        }

        return newReview;
    }

    /**
     * Get customers with an average rating below a given threshold
     * @param f Rating threshold
     * @return List of all selected Customers
     * @throws IllegalStateException if db was not initialized
     */
    @Override
    public List<Customer> getTrolls(float f) {
        checkInitialized();

        List<Customer> result = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            // Get all users with avg rating below f
            result = session.createSelectionQuery(
                    "from Customer c join c.reviews r group by c having avg(r.points) < :limit",
                    Customer.class
                )
                .setParameter("limit", f)
                .getResultList();

            t.commit();
        }

        return result;
    }

    /**
     * Get all Offers for a given Product
     * @param p Product to get offers for
     * @return List of Offers
     * @throws IllegalStateException if db was not initialized
     * @throws IllegalArgumentException for empty p
     */
    @Override
    public List<Offer> getOffers(Product p) {
        checkInitialized();

        if (p == null || p.getId() == null) {
            throw new IllegalArgumentException("A product is required.");
        }

        List<Offer> result = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            // Load the offers and the referenced entities before the session is closed.
            result = session.createSelectionQuery(
                "select o from Offer o join fetch o.shop join fetch o.product "
                    + "where o.product.id = :productId order by o.price, o.id",
                Offer.class
            )
            .setParameter("productId", p.getId())
            .getResultList();

            t.commit();
        }

        return result;
    }

    // Helpers
    /**
     * Checks if db is currently initialized and ready for sessions
     * @throws IllegalStateException If not ready
     */
    private void checkInitialized() throws IllegalStateException {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            throw new IllegalStateException("No DB Session initialized. Call init() first.");
        }
    }

    /**
     * Recursively trigger lazy loading of all categories
     * @param category root category 
     */
    private void initializeCategoryTree(Category category) {
        Hibernate.initialize(category.getChilds());

        for (Category child : category.getChilds()) {
            initializeCategoryTree(child);
        }
    }

    private static Map<String, BigDecimal> getMinimumPricesByCurrency(Product product) {
        Map<String, BigDecimal> minimumPrices = new HashMap<>();
        for (Offer offer : product.getOffers()) {
            if (offer.getCurrency() == null || offer.getPrice() == null) {
                continue;
            }
            minimumPrices.merge(offer.getCurrency(), offer.getPrice(), BigDecimal::min);
        }
        return minimumPrices;
    }

    private static boolean hasCheaperOffer(Product product, Map<String, BigDecimal> originalMinPrices) {
        return getMinimumPricesByCurrency(product).entrySet().stream()
            .anyMatch(entry -> {
                BigDecimal originalPrice = originalMinPrices.get(entry.getKey());
                return originalPrice != null && entry.getValue().compareTo(originalPrice) < 0;
            });
    }
    
}
