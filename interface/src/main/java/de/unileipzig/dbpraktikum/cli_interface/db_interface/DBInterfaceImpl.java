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
import jakarta.persistence.NoResultException;

public class DBInterfaceImpl implements DBInterface {
    private SessionFactory sessionFactory;

    @Override
    public void init() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            return;
        }

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

            // Trigger Lazy loads
            p.getSimilarProducts();
            p.getOffers();
            p.getReviews();

            t.commit();
        }

        return p;
    }

    @Override
    public List<Product> getProducts(String pattern) {
        checkInitialized();

        List<Product> products = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            products = session.createSelectionQuery(
                "from Product p where lower(p.title) like lower(:pattern)",
                Product.class
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
                "from Product p ORDER BY avgRating DESC NULLS LAST, ratingQuantity DESC, id limit :amount",
                Product.class
            )
            .setParameter("amount", k)
            .getResultList();

            t.commit();
        }

        return products;
    }

    @Override
    public List<Product> getSimilarCheaperProducts(Product p) {
        checkInitialized();

        List<Product> result = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();

            Product managed = (Product) session.get(Product.class, p.getId());

            // Get min price for product
            Optional<BigDecimal> originalMinPrice = managed.getOffers().stream()
                .map(Offer::getPrice)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo);

            // No offers for product, so no cheaper products available
            if (originalMinPrice.isEmpty()) {
                return List.of();
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

        Review newReview = null;

        try (Session session = sessionFactory.openSession()) {
            Transaction t = session.beginTransaction();
            Customer user = null;

            try {
                // Get user if exists
                user = session.createSelectionQuery(
                    "from Customer c where name = :username",
                    Customer.class
                )
                .setParameter("username", username)
                .getSingleResult();
            } catch (NoResultException e) {
                //Create user
                user = new Customer();
                user.setName(username);
                session.persist(user);
                session.flush();
            }

            // Create Review for user
            newReview = new Review();
            newReview.setCustomer(user);
            newReview.setDate(new Timestamp(System.currentTimeMillis() - 100));
            newReview.setPoints(points);
            newReview.setText(text);
            newReview.setProduct(p);

            session.persist(newReview);

            t.commit();
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