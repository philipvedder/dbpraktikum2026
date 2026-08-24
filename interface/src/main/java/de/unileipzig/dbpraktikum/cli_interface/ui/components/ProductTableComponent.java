package de.unileipzig.dbpraktikum.cli_interface.ui.components;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.table.Table;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.model.dto.ProductListEntry;
import de.unileipzig.dbpraktikum.cli_interface.ui.ProductDetailScreen;

public class ProductTableComponent {
    private final WindowBasedTextGUI gui;
    private final DBInterface db;
    
    private List<ProductListEntry> currentProducts = new ArrayList<>();
    private Table<String> productTable;

    public ProductTableComponent(WindowBasedTextGUI gui, DBInterface db) {
        this.gui = gui;
        this.db = db;
    }

    public Table<String> getTable(int columns, int rows) {
        // Build Table with correct size and header
        productTable = new Table<>("Product ID", "Title", "Type", "Rating");
        productTable.setPreferredSize(new TerminalSize(columns, rows));
        productTable.setCellSelection(false); //No independent cell selection, just rows

        // Select action to open products
        productTable.setSelectAction(() -> openSelectedProduct());

        return productTable;
    }

    public void update(List<ProductListEntry> products) {
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
