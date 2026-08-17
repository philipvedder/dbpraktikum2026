package de.unileipzig.dbpraktikum.cli_interface.db_interface;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.unileipzig.dbpraktikum.cli_interface.model.Book;
import de.unileipzig.dbpraktikum.cli_interface.model.CD;
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
            t.commit();
        }

        return p;
    }

    @Override
    public void getProducts(String pattern) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProducts'");
    }

    @Override
    public void getCategoryTree() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCategoryTree'");
    }

    @Override
    public void getProductsByCategoryPath(String path) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProductsByCategoryPath'");
    }

    @Override
    public void getTopProducts(int k) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTopProducts'");
    }

    @Override
    public void getSimilarCheaperProduct(String pid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSimilarCheaperProduct'");
    }

    @Override
    public void addNewReview(String pid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addNewReview'");
    }

    @Override
    public void getTrolls(float f) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTrolls'");
    }

    @Override
    public void getOffers(String pid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOffers'");
    }

    // Helpers
    private void checkInitialized() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            throw new IllegalStateException("No DB Session initialized. Call init() first.");
        }
    }
    
}