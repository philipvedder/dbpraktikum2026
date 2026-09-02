package de.unileipzig.dbpraktikum.cli_interface.ui;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.table.Table;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.model.dto.ProductListEntry;
import de.unileipzig.dbpraktikum.cli_interface.ui.components.ProductTableComponent;

public class ProductListScreen {
    private final WindowBasedTextGUI gui;
    private final DBInterface db;
    
    private ProductTableComponent productTable;

    public ProductListScreen(WindowBasedTextGUI gui, DBInterface db) {
        this.gui = gui;
        this.db = db;
        this.productTable = new ProductTableComponent(gui, db);
    }

    public void show() {
        // Setup terminal and screen layers
        BasicWindow window = new BasicWindow("Product List");

        // Create panel to hold components
        Panel root = new Panel();
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        // --- Add Search bar
        Panel searchPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        TextBox searchBox = new TextBox(new TerminalSize(35, 1));

        // Listen to text changes
        searchBox.setTextChangeListener((String text, boolean changedByUserInteraction) -> onTextChange(text));
        
        //Add search components
        searchPanel.addComponent(new Label("Search for Title: "));
        searchPanel.addComponent(searchBox);

        // --- Add Product Table
        Table<String> pTable = productTable.getTable(120, 30);

        // --- Add bottom buttons
        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new Button("Back", window::close));

        //Add root components
        root.addComponent(searchPanel);
        root.addComponent(new EmptySpace(new TerminalSize(1, 1)));
        root.addComponent(pTable);
        root.addComponent(buttons);

        // Load initial (full) product list
        onTextChange("");

        // Show window
        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    private void onTextChange(String text) {
        // The DB interface accepts SQL patterns. The UI turns plain search text into
        // a substring search and uses null to request the complete product list.
        String pattern = text == null || text.isEmpty() ? null : "%" + text + "%";
        List<ProductListEntry> result = db.getProducts(pattern);
        productTable.update(result);
    }
}
