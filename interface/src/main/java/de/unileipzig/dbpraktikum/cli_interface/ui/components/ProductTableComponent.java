package de.unileipzig.dbpraktikum.cli_interface.ui.components;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.table.Table;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.model.Offer;
import de.unileipzig.dbpraktikum.cli_interface.model.Product;
import de.unileipzig.dbpraktikum.cli_interface.ui.ProductDetailScreen;
import de.unileipzig.dbpraktikum.cli_interface.util.FormatUtil;

public class ProductTableComponent {
    /**
     * Lanterna TUI Component for easy construction of an interactive Table of Product objects
     */

    // Lanterna TUI and DB Interface
    private final WindowBasedTextGUI gui;
    private final DBInterface db;
    
    // Internal
    private List<Product> currentProducts = new ArrayList<>();
    private Table<String> productTable;

    public ProductTableComponent(WindowBasedTextGUI gui, DBInterface db) {
        this.gui = gui;
        this.db = db;
    }

    /**
     * Return the Table Component in a given size
     * @param columns number of terminal colums (width of table)
     * @param rows number of terminal rows (height of table)
     * @return The Lanterna Component
     */
    public Table<String> getTable(int columns, int rows) {
        // Build Table with correct size and header
        productTable = new Table<>("Product ID", "Title", "Type", "Rating", "Cheapest Offer");
        productTable.setPreferredSize(new TerminalSize(columns, rows));
        productTable.setCellSelection(false); //No independent cell selection, just rows

        // Select action to open products
        productTable.setSelectAction(() -> openSelectedProduct());

        return productTable;
    }

    /**
     * Update the Table component with a given List of Product.
     * @param products List of Product
     */
    public void update(List<Product> products) {
        // Remove old entries and add new ones
        currentProducts.clear();
        currentProducts.addAll(products);
        productTable.getTableModel().clear();

        // Build a table entry for each Product
        for (Product p : products) {
            //Get cheapest offer for product
            Optional<BigDecimal> cheapestOffer = p.getOffers().stream()
                    .map(Offer::getPrice)
                    .filter(Objects::nonNull)
                    .min(BigDecimal::compareTo);

            // Add Table row
            productTable.getTableModel().addRow(
                p.getId(),
                FormatUtil.trunc(p.getTitle(), 70),
                p.getType().name(),
                FormatUtil.formatDecimal(p.getAvgRating()),
                FormatUtil.formatDecimal(cheapestOffer.get())
            );
        }
    }

    /**
     * Open the ProductDetailScreen for the currently selected table row.
     */
    private void openSelectedProduct() {
        // Ensure list is not empty
        if (currentProducts.isEmpty()) {
            return;
        }

        // Get PID
        int row = productTable.getSelectedRow();
        Product selected = currentProducts.get(row);

        // Open Screen
        new ProductDetailScreen(gui, db, selected.getId()).show();
    }
}
