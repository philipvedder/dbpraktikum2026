package de.unileipzig.dbpraktikum.cli_interface.db_interface;

import java.util.List;
import java.util.Properties;

import de.unileipzig.dbpraktikum.cli_interface.model.Category;
import de.unileipzig.dbpraktikum.cli_interface.model.Customer;
import de.unileipzig.dbpraktikum.cli_interface.model.Offer;
import de.unileipzig.dbpraktikum.cli_interface.model.Product;
import de.unileipzig.dbpraktikum.cli_interface.model.Review;
import de.unileipzig.dbpraktikum.cli_interface.model.dto.ProductListEntry;

public interface DBInterface {
    /**
     * Initializes a DB instance from given Hibernate properties.
     * @param properties Properties for DB instance creation
     * @throws IllegalArgumentException if properties is null
     * @throws HibernateException on an illegal configuration
     */
    public void init(Properties properties);

    /**
     * Closes the held DB Connections and releases memory
     */
    public void finish();

    /**
     * Get Product data for a given product id. 
     * Returns null if product does not exist.
     * @param pid Product id
     * @return The loaded Product
     * @throws IllegalStateException if db was not initialized
     */
    public Product getProduct(String pid);

    /**
     * Get ProductListEntry's which name matches the given pattern.
     * @param pattern name pattern to search for
     * @return List of ProductListEntry's. 
     * @throws IllegalStateException if db was not initialized
     */
    public List<ProductListEntry> getProducts(String pattern); 

    /**
     * Get the full Category tree, meaning the root categories and recursively all subcategories. 
     * @return One artificial root Category object, holding the root Categories. 
     * @throws IllegalStateException if db was not initialized
     */
    public Category getCategoryTree();

    /**
     * Get all Products that are in the Category, given as a path given by a list of Strings, f.e.
     * "dvds", "under 1 EUR", "german" -> DVDS>>Under 1 EUR>>GERMAN
     * @param categoryPath Category Path as list of Strings
     * @return List of Products in Category
     * @throws IllegalStateException if db was not initialized
     */
    public List<Product> getProductsByCategoryPath(List<String> categoryPath);

    /**
     * Get the k top products, measured and ordered by avg_rating and rating_quantity.
     * @param k number of Top products to return
     * @return List of ProductListEntries for the found products
     * @throws IllegalStateException if db was not initialized
     */
    public List<ProductListEntry> getTopProducts(int k);

    /**
     * Get all similar products of a given products, which include a cheaper offer. 
     * @param p Product to search for
     * @return List of all similar Products with cheaper offers
     * @throws IllegalStateException if db was not initialized
     */
    public List<Product> getSimilarCheaperProducts(Product p);

    /**
     * Add a new Review to a Product
     * @param p Product
     * @param username Username. Will create user if not found. 
     * @param points Number of Points between 1 and 5
     * @param text Optional review text
     * @return The added review if successful
     * @throws IllegalStateException if db was not initialized
     */
    public Review addNewReview(Product p, String username, int points, String text);

    /**
     * Get customers with an average rating below a given threshold
     * @param f Rating threshold
     * @return List of all selected Customers
     * @throws IllegalStateException if db was not initialized
     */
    public List<Customer> getTrolls(float f); 

    /**
     * Get all Offers for a given Product
     * @param p Product to get offers for
     * @return List of Offers
     * @throws IllegalStateException if db was not initialized
     */
    public List<Offer> getOffers(Product p); //Get all offers for product id
}
