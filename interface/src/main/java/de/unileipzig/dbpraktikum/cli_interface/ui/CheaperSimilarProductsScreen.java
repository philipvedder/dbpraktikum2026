package de.unileipzig.dbpraktikum.cli_interface.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.model.Product;
import de.unileipzig.dbpraktikum.cli_interface.model.dto.ProductListEntry;
import de.unileipzig.dbpraktikum.cli_interface.ui.components.ProductTableComponent;

/** Displays products that are both similar to and cheaper than a product. */
public class CheaperSimilarProductsScreen {
    private final WindowBasedTextGUI gui;
    private final DBInterface db;
    private final Product product;

    public CheaperSimilarProductsScreen(WindowBasedTextGUI gui, DBInterface db, Product product) {
        this.gui = gui;
        this.db = db;
        this.product = product;
    }

    public void show() {
        BasicWindow window = new BasicWindow("Cheaper similar products");
        Panel root = new Panel(new LinearLayout(Direction.VERTICAL));

        root.addComponent(new Label("Product: " + product.getId() + " - " + product.getTitle()));

        ProductTableComponent productTable = new ProductTableComponent(gui, db);
        root.addComponent(productTable.getTable(120, 25));

        try {
            List<Product> products = new ArrayList<>(db.getSimilarCheaperProducts(product));
            products.sort(Comparator.comparing(Product::getTitle, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Product::getId));

            List<ProductListEntry> entries = new ArrayList<>();
            for (Product similar : products) {
                entries.add(new ProductListEntry(
                    similar.getId(), similar.getTitle(), similar.getType(), similar.getAvgRating()
                ));
            }

            productTable.update(entries);
            root.addComponent(new Label(entries.isEmpty()
                ? "No cheaper similar products found."
                : "Found " + entries.size() + " cheaper similar products."));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            productTable.update(new ArrayList<>());
            root.addComponent(new Label("Could not load cheaper similar products."));
            MessageDialog.showMessageDialog(gui, "Database error",
                "Could not load cheaper similar products.\nSee the terminal for details.",
                MessageDialogButton.OK);
        }

        root.addComponent(new Button("Back", window::close));
        window.setComponent(root);
        gui.addWindowAndWait(window);
    }
}
