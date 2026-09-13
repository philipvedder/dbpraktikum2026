package de.unileipzig.dbpraktikum.cli_interface.ui;

import java.util.List;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.table.Table;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.model.Product;
import de.unileipzig.dbpraktikum.cli_interface.ui.components.ProductTableComponent;

public class CheaperSimilarProductsScreen {
    /**
     * Lanterna TUI Screen for viewing cheaper similar products
     */

    // Lanterna TUI and DB Interface
    private final WindowBasedTextGUI gui;
    private final DBInterface db;

    // Current product
    private final Product p;

    // Component storage
    private ProductTableComponent productTable;

    public CheaperSimilarProductsScreen(WindowBasedTextGUI gui, DBInterface db, Product p) {
        this.gui = gui;
        this.db = db;
        this.p = p; // B000ALCG06
        this.productTable = new ProductTableComponent(gui, db);
    }

    /**
     * Constructs and shows the TUI window
     */
    public void show() {
        // Setup terminal and screen layers
        BasicWindow window = new BasicWindow("Product List");

        // Create panel to hold components
        Panel root = new Panel();
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        // --- Add Product Table
        Table<String> pTable = productTable.getTable(120, 30);

        // --- Add bottom buttons
        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new Button("Back", window::close));

        //Add root components
        root.addComponent(pTable);
        root.addComponent(buttons);

        // Load initial (full) product list
        load();

        // Show window
        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    /**
     * Loads the list of cheaper similar products from db and updates the table
     */
    private void load() {
        // Fetch and update table
        List<Product> result = db.getSimilarCheaperProducts(p);
        productTable.update(result);
    }
}
