package de.unileipzig.dbpraktikum.cli_interface.ui;

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

/** Opens the detail view for one exact product ID. */
public class ProductByIdScreen {
    private final WindowBasedTextGUI gui;
    private final DBInterface db;

    private Label status;

    public ProductByIdScreen(WindowBasedTextGUI gui, DBInterface db) {
        this.gui = gui;
        this.db = db;
    }

    public void show() {
        BasicWindow window = new BasicWindow("Product by ID");
        Panel root = new Panel(new LinearLayout(Direction.VERTICAL));

        root.addComponent(new Label("Enter the exact product ID."));

        Panel search = new Panel(new LinearLayout(Direction.HORIZONTAL));
        TextBox productIdBox = new TextBox(new TerminalSize(25, 1));
        search.addComponent(new Label("Product ID: "));
        search.addComponent(productIdBox);
        search.addComponent(new Button("Open", () -> openProduct(productIdBox.getText())));
        root.addComponent(search);

        status = new Label("Enter an ID, then choose Open.");
        root.addComponent(status);
        root.addComponent(new Button("Back", window::close));

        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    private void openProduct(String input) {
        String productId = input == null ? "" : input.trim();
        if (productId.isEmpty()) {
            status.setText("Enter a product ID.");
            MessageDialog.showMessageDialog(gui, "Missing product ID",
                "Enter the exact product ID before choosing Open.", MessageDialogButton.OK);
            return;
        }

        try {
            Product product = db.getProduct(productId);
            if (product == null) {
                status.setText("No product found with ID " + productId + ".");
                MessageDialog.showMessageDialog(gui, "Product not found",
                    "No product found with ID " + productId + ".", MessageDialogButton.OK);
                return;
            }

            status.setText("Opened product " + productId + ".");
            new ProductDetailScreen(gui, db, product).show();
        } catch (RuntimeException ex) {
            status.setText("Could not load product " + productId + ".");
            ex.printStackTrace();
            MessageDialog.showMessageDialog(gui, "Database error",
                "Could not load the product.\nSee the terminal for details.", MessageDialogButton.OK);
        }
    }
}
