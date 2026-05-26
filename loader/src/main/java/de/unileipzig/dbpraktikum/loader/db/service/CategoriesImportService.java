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

public class CategoriesImportService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public CategoriesImportService() {
        this.productRepository = new ProductRepository();
        this.categoryRepository = new CategoryRepository();
    }

    public void importCategory(Connection con, Category c, Long parentId) throws SQLException {
        //Check if Category already exists. 
        //We do not allow Categories with the same name and parent, but instead add to them. 
        Long catId = this.categoryRepository.findOrCreate(con, c, parentId);

        //Insert all items for Category
        for (String pId : c.itemIds()) {
            //Exceptions for Duplicate and Missing Items. 
            //We do not throw these, as we do not want this to be terminating.
            if (!productRepository.exists(con, pId)) {
                ErrorLogger.reportErrors("Category with ID " + catId.toString(), List.of(new NotExistException("Product", pId)));
                continue;
            }

            if (categoryRepository.itemExists(con, pId, catId)) {
                ErrorLogger.reportErrors("Category with ID " + catId.toString(), List.of(new DuplicateException("Category Entry", pId)));
                continue;
            }

            categoryRepository.insertItem(con, pId, catId);
        }

        //Handle all child categories the same way
        for (Category child : c.childCategories()) {
            importCategory(con, child, catId);
        }
    }

    public void importCategories(Connection con, List<Category> categories) {
        System.out.println("Starting DB insertions...");
    
        //Handle every Category
        for (Category category : categories) {
            try {
                importCategory(con, category, null);
            } catch (SQLException ex) {
                ErrorLogger.reportErrors(category.name() + " - Category", List.of(new UnknownSQLException(category.name(), ex.getMessage())));
            }
        }
    }
}
