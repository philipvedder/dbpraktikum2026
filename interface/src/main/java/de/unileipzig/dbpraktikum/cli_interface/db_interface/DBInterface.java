package de.unileipzig.dbpraktikum.cli_interface.db_interface;

import de.unileipzig.dbpraktikum.cli_interface.model.Product;

public interface DBInterface {
    public void init(); // Setup of DB Connection
    public void finish(); // Closed DB Connection, releases objs.
    public Product getProduct(String pid); // Get data for product 
    public void getProducts(String pattern); // Get all products which title matches pattern
    public void getCategoryTree(); //Get full Cat tree
    public void getProductsByCategoryPath(String path); //Gets all products for a given Category
    public void getTopProducts(int k); //Get list of top k products, based on rating
    public void getSimilarCheaperProduct(String pid); //Get list of similar but cheaper products
    public void addNewReview(String pid); //Add a new Review for a given Product
    public void getTrolls(float f); //Get all users which have an avgRating below f
    public void getOffers(String pid); //Get all offers for product id
}