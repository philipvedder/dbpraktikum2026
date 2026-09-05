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
    public void init(Properties properties); // Setup of DB Connection
    public void finish(); // Closed DB Connection, releases objs.
    public Product getProduct(String pid); // Get data for product 
    public List<ProductListEntry> getProducts(String pattern); // Get all products which title matches pattern
    public Category getCategoryTree(); //Get full Cat tree through one root node
    public List<Product> getProductsByCategoryPath(List<String> categoryPath); //Gets products for a full Category path
    public List<ProductListEntry> getTopProducts(int k); //Get list of top k products, based on rating
    public List<Product> getSimilarCheaperProducts(Product p); //Get list of similar but cheaper products
    public Review addNewReview(Product p, String username, int points, String text); //Add a new Review for a given Product
    public List<Customer> getTrolls(float f); //Get all users which have an avgRating below f
    public List<Offer> getOffers(Product p); //Get all offers for product id
}
