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

public class ProductListScreen {
    private final WindowBasedTextGUI gui;
    private final DBInterface db;
    
    private List<ProductListEntry> currentProducts = new ArrayList<>();
    private Table<String> productTable;

    public ProductListScreen(WindowBasedTextGUI gui, DBInterface db) {
        this.gui = gui;
        this.db = db;
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
        productTable = new Table<>("Product ID", "Title", "Type", "Rating");
        productTable.setPreferredSize(new TerminalSize(120, 30));
        productTable.setCellSelection(false); //No independent cell selection

        // Select action to open products
        productTable.setSelectAction(() -> openSelectedProduct());

        // --- Add bottom buttons
        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new Button("Back", window::close));

        //Add root components
        root.addComponent(searchPanel);
        root.addComponent(new EmptySpace(new TerminalSize(1, 1)));
        root.addComponent(productTable);
        root.addComponent(buttons);

        // Load initial product list
        onTextChange("");

        // Show window
        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    private void openSelectedProduct() {
        // Ensure list is not empty
        if (currentProducts.isEmpty()) {
            return;
        }

        // Get PID
        int row = productTable.getSelectedRow();
        String selectedPID = currentProducts.get(row).getId();

        // Open Screen
        new ProductDetailScreen(gui, db, selectedPID).show();
    }

    private void onTextChange(String text) {
        // Fetch
        List<ProductListEntry> result = db.getProducts(text);
        this.updateTable(result);
    }

    private void updateTable(List<ProductListEntry> products) {
        currentProducts.clear();
        currentProducts.addAll(products);

        productTable.getTableModel().clear();

        for (ProductListEntry p : products) {
            productTable.getTableModel().addRow(
                p.getId(),
                trunc(p.getTitle(), 70),
                p.getType().name(),
                formatDecimal(p.getAvgRating())
            );
        }
    }

    // Helper
    private String formatDecimal(BigDecimal d) {
        if (d == null) {
            return "-";
        }

        return String.format("%.2f", d);
    }

    private String trunc(String s, int length) {
        return s.substring(0, Math.min(length, s.length()));
    }
}
