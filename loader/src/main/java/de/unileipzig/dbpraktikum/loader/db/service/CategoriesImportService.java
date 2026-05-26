package de.unileipzig.dbpraktikum.loader.db.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import de.unileipzig.dbpraktikum.exception.DuplicateException;
import de.unileipzig.dbpraktikum.exception.NotExistException;
import de.unileipzig.dbpraktikum.exception.UnknownSQLException;
import de.unileipzig.dbpraktikum.loader.db.repository.CategoryRepository;
import de.unileipzig.dbpraktikum.loader.db.repository.ProductRepository;
import de.unileipzig.dbpraktikum.loader.logger.ErrorLogger;
import de.unileipzig.dbpraktikum.loader.model.Category;

/**
 * Service class to import validated Category data into the Database. 
 * Uses the repository classes to interact with the DB. 
 * Writes Exceptions which occur during this process to the Error log.
 */
public class CategoriesImportService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Initialize all required Repos
     */
    public CategoriesImportService() {
        this.productRepository = new ProductRepository();
        this.categoryRepository = new CategoryRepository();
    }

    //TODO: ADD some stats for printing here. 

    /**
     * Import of a single validated Category obj into the DB. 
     * Recursive on child Categories. 
     * @param con DB Connection Obj. 
     * @param c The Category obj to import
     * @param parentId Id of parent Category
     * @throws SQLException thrown on SQL execution problems.
     */
    private void importCategory(Connection con, Category c, Long parentId) throws SQLException {
        //Check if Category already exists. Unique if same name and parent. 
        Long catId = this.categoryRepository.findOrCreate(con, c, parentId);

        //Insert all items for Category
        for (String pId : c.itemIds()) {
            //Exceptions for Duplicate and Missing Items. 
            //We do not throw these, as we do not want this to be terminating.
            if (!productRepository.exists(con, pId)) { //Product is not in DB
                ErrorLogger.reportErrors("Category with ID " + catId.toString(), List.of(new NotExistException("Product", pId)));
                continue;
            }

            if (categoryRepository.itemExists(con, pId, catId)) { //Product already in Category
                ErrorLogger.reportErrors("Category with ID " + catId.toString(), List.of(new DuplicateException("Category Entry", pId)));
                continue;
            }

            //Insert new item
            categoryRepository.insertItem(con, pId, catId);
        }

        //Handle all child categories the same way, recursively
        for (Category child : c.childCategories()) {
            importCategory(con, child, catId);
        }
    }

    /**
     * Import a List of Categories into DB. 
     * @param con DB Connection Obj. 
     * @param categories List<Category> to import. 
     */
    public void importCategories(Connection con, List<Category> categories) {
        System.out.println("Starting DB insertions...");
    
        //Handle all Categories on their own. 
        for (Category category : categories) {
            try {
                importCategory(con, category, null);
            } catch (SQLException ex) {
                //Error while executing SQL
                ErrorLogger.reportErrors(category.name() + " - Category", List.of(new UnknownSQLException(category.name(), ex.getMessage())));
            }
        }
    }
}
