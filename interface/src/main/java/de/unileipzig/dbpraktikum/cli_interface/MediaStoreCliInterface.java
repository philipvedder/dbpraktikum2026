package de.unileipzig.dbpraktikum.cli_interface;

import java.util.List;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.*;
import de.unileipzig.dbpraktikum.cli_interface.model.Category;
import de.unileipzig.dbpraktikum.cli_interface.model.Customer;
import de.unileipzig.dbpraktikum.cli_interface.model.Product;
import de.unileipzig.dbpraktikum.cli_interface.model.Review;

public class MediaStoreCliInterface {
    public static void main(String[] args) {

        DBInterface db = new DBInterfaceImpl();

        db.init();
        Product p = db.getProduct("3407788738");
        List<Product> products = db.getProducts("test");
        List<Category> roots = db.getCategoryTree();
        List<Product> prodsInCat = db.getProductsByCategory(roots.getFirst());
        List<Product> topProducts = db.getTopProducts(5);
        List<Product> simCheaper = db.getSimilarCheaperProducts(p);
        Review r = db.addNewReview(p, "test", 3, "Test");
        List<Customer> trolls = db.getTrolls(2);

        db.finish();

        System.out.println(p.getTitle());
        System.out.println(products.size());

        System.out.println("Root Categories");
        for (Category c : roots) {
            System.out.println(c.getName());
        }

        System.out.println(prodsInCat.size());
        System.out.println(topProducts.size());
        System.out.println(simCheaper.size());
        System.out.println(trolls.size());
    }
}
