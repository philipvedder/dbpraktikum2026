package de.unileipzig.dbpraktikum.cli_interface.ui;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.model.Product;
import de.unileipzig.dbpraktikum.cli_interface.model.dto.ProductListEntry;
import de.unileipzig.dbpraktikum.cli_interface.ui.components.ProductTableComponent;

/** Displays the top k products in the order returned by getTopProducts. */
public class TopProductsScreen {
    private final WindowBasedTextGUI gui;
    private final DBInterface db;

    private ProductTableComponent productTable;
    private Label status;

    public TopProductsScreen(WindowBasedTextGUI gui, DBInterface db) {
        this.gui = gui;
        this.db = db;
    }

    public void show() {
        BasicWindow window = new BasicWindow("Top Products");
        Panel root = new Panel(new LinearLayout(Direction.VERTICAL));

        Panel search = new Panel(new LinearLayout(Direction.HORIZONTAL));
        TextBox amountBox = new TextBox(new TerminalSize(12, 1));
        amountBox.setText("10");
        search.addComponent(new Label("Number of products: "));
        search.addComponent(amountBox);
        search.addComponent(new Button("Load", () -> loadProducts(amountBox.getText())));
        root.addComponent(search);

        status = new Label("Enter a positive whole number, then choose Load.");
        root.addComponent(status);

        productTable = new ProductTableComponent(gui, db);
        root.addComponent(productTable.getTable(120, 30));
        root.addComponent(new Button("Back", window::close));

        loadProducts(amountBox.getText());

        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    private void loadProducts(String input) {
        productTable.update(new ArrayList<>());

        final int amount;
        try {
            amount = Integer.parseInt(input == null ? "" : input.trim());
            if (amount <= 0) {
                throw new NumberFormatException("Amount must be positive");
            }
        } catch (NumberFormatException ex) {
            status.setText("Enter a positive whole number.");
            MessageDialog.showMessageDialog(gui, "Invalid amount",
                "Enter a positive whole number, for example 10.", MessageDialogButton.OK);
            return;
        }

        try {
            List<Product> products = db.getTopProducts(amount);
            List<ProductListEntry> entries = new ArrayList<>();
            for (Product product : products) {
                entries.add(new ProductListEntry(
                    product.getId(), product.getTitle(), product.getType(), product.getAvgRating()
                ));
            }
            productTable.update(entries);
            status.setText(entries.isEmpty()
                ? "No rated products found."
                : "Showing " + entries.size() + " of the requested " + amount + " products.");
        } catch (RuntimeException ex) {
            productTable.update(new ArrayList<>());
            status.setText("Could not load top products.");
            ex.printStackTrace();
            MessageDialog.showMessageDialog(gui, "Database error",
                "Could not load top products.\nSee the terminal for details.", MessageDialogButton.OK);
        }
    }
}
