package de.unileipzig.dbpraktikum.cli_interface.db_interface;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.unileipzig.dbpraktikum.cli_interface.model.Book;
import de.unileipzig.dbpraktikum.cli_interface.model.CD;
import de.unileipzig.dbpraktikum.cli_interface.model.Category;
import de.unileipzig.dbpraktikum.cli_interface.model.Customer;
import de.unileipzig.dbpraktikum.cli_interface.model.DVD;
import de.unileipzig.dbpraktikum.cli_interface.model.Format;
import de.unileipzig.dbpraktikum.cli_interface.model.Label;
import de.unileipzig.dbpraktikum.cli_interface.model.Offer;
import de.unileipzig.dbpraktikum.cli_interface.model.Person;
import de.unileipzig.dbpraktikum.cli_interface.model.Product;
import de.unileipzig.dbpraktikum.cli_interface.model.Publisher;
import de.unileipzig.dbpraktikum.cli_interface.model.Review;
import de.unileipzig.dbpraktikum.cli_interface.model.Shop;
import de.unileipzig.dbpraktikum.cli_interface.model.Track;
import de.unileipzig.dbpraktikum.cli_interface.model.dto.ProductListEntry;
import jakarta.persistence.NoResultException;

public class DBInterfaceImpl implements DBInterface {
    private SessionFactory sessionFactory;

    @Override
    public void init() {
        // Ensure sessionfactory is not already initialized
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            return;
        }

        // Construct configuration from hibernate.properties file, with all models included
        Configuration config = new Configuration();

        config.addAnnotatedClass(Product.class);
        config.addAnnotatedClass(Book.class);
        config.addAnnotatedClass(CD.class);
        config.addAnnotatedClass(Customer.class);
        config.addAnnotatedClass(DVD.class);
        config.addAnnotatedClass(Format.class);
        config.addAnnotatedClass(Label.class);
        config.addAnnotatedClass(Offer.class);
        config.addAnnotatedClass(Person.class);
        config.addAnnotatedClass(Publisher.class);
        config.addAnnotatedClass(Review.class);
        config.addAnnotatedClass(Shop.class);
        config.addAnnotatedClass(Track.class);
        config.addAnnotatedClass(Category.class);

        sessionFactory = config.buildSessionFactory();
    }

    @Override
    public void finish() {
        // Close sessionfactory if available
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }   

        sessionFactory = null;
    }

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
            Hibernate.initialize(p.getOffers());
            Hibernate.initialize(p.getReviews());
            Hibernate.initialize(p.getCategories());

            t.commit();
        }

        return p;
    }

    @Override
    public List<ProductListEntry> getProducts(String pattern) {
        checkInitialized();

        List<ProductListEntry> products = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            products = session.createSelectionQuery(
                "select new de.unileipzig.dbpraktikum.cli_interface.model.dto.ProductListEntry(p.id, p.title, p.type, p.avgRating) from Product p where lower(p.title) like lower(:pattern) order by p.title",
                ProductListEntry.class
            )
            .setParameter("pattern", "%" + pattern + "%")
            .getResultList();

            t.commit();
        }

        return products;
    }

    @Override
    public List<Category> getCategoryTree() {
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

        return roots;
    }

    @Override
    public List<Product> getProductsByCategory(Category c) {
        checkInitialized();

        List<Product> products = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            // Get managed Category
            Category managed = session.get(Category.class, c.getId());

            // Trigger Lazy loading of products
            products = new ArrayList<>(managed.getProducts());

            t.commit();
        }

        return products;
    }

    @Override
    public List<Product> getTopProducts(int k) {
        checkInitialized();

        List<Product> products = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            // Select all products, ordered by rating average and quntity
            products = session.createSelectionQuery(
                "from Product p ORDER BY p.avgRating DESC NULLS LAST, p.ratingQuantity DESC, p.id",
                Product.class
            )
            .setMaxResults(k)
            .getResultList();

            t.commit();
        }

        return products;
    }

    @Override
    public List<Product> getSimilarCheaperProducts(Product p) {
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

            // Get min price for product
            Optional<BigDecimal> originalMinPrice = managed.getOffers().stream()
                .map(Offer::getPrice)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo);

            // No offers for product, so no cheaper products available
            if (originalMinPrice.isEmpty()) {
                t.commit();
                return result;
            }

            // Get Price
            BigDecimal price = originalMinPrice.get();

            // Get similar products
            Set<Product> similars = managed.getSimilarProducts();

            // Fitler for cheaper similars
            for (Product sim : similars) {
                Optional<BigDecimal> simMinPrice = sim.getOffers().stream()
                    .map(Offer::getPrice)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo);

                    if (simMinPrice.isPresent() && simMinPrice.get().compareTo(price) < 0) {
                        result.add(sim);
                    }
            }

            t.commit();
        }

        return result;
    }

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

    @Override
    public List<Offer> getOffers(Product p) {
        checkInitialized();

        List<Offer> result = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            // Get manager product
            Product managed = (Product) session.get(Product.class, p.getId());

            // Get offers
            result = managed.getOffers();

            t.commit();
        }

        return result;
    }

    // Helpers
    private void checkInitialized() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            throw new IllegalStateException("No DB Session initialized. Call init() first.");
        }
    }

    private void initializeCategoryTree(Category category) {
        Hibernate.initialize(category.getChilds());

        for (Category child : category.getChilds()) {
            initializeCategoryTree(child);
        }
    }
    
}
