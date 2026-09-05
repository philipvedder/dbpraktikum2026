package de.unileipzig.dbpraktikum.cli_interface.ui;

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
import com.googlecode.lanterna.gui2.table.Table;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.model.dto.ProductListEntry;
import de.unileipzig.dbpraktikum.cli_interface.ui.components.ProductListEntryTableComponent;

public class TopProductsScreen {
    private final WindowBasedTextGUI gui;
    private final DBInterface db;
    
    private ProductListEntryTableComponent productTable;
    private Label status;

    public TopProductsScreen(WindowBasedTextGUI gui, DBInterface db) {
        this.gui = gui;
        this.db = db;
        this.productTable = new ProductListEntryTableComponent(gui, db);
    }

    public void show() {
        // Setup terminal and screen layers
        BasicWindow window = new BasicWindow("Top Products");

        // Create panel to hold components
        Panel root = new Panel();
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        // --- Add input for the requested number of products
        Panel limitPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        TextBox limitBox = new TextBox(new TerminalSize(10, 1));
        limitBox.setText("25");
        limitPanel.addComponent(new Label("Number of products: "));
        limitPanel.addComponent(limitBox);
        limitPanel.addComponent(new Button("Load", () -> load(limitBox.getText())));

        status = new Label("");

        // --- Add Product Table
        Table<String> pTable = productTable.getTable(120, 30);

        // --- Add bottom buttons
        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new Button("Back", window::close));

        //Add root components
        root.addComponent(limitPanel);
        root.addComponent(status);
        root.addComponent(pTable);
        root.addComponent(buttons);

        // Load the default number of top products
        load(limitBox.getText());

        // Show window
        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    private void load(String input) {
        final int amount;
        try {
            amount = Integer.parseInt(input.trim());
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
            List<ProductListEntry> result = db.getTopProducts(amount);
            productTable.update(result);
            status.setText("Showing the top " + result.size() + " products.");
        } catch (RuntimeException ex) {
            status.setText("Could not load top products.");
            ex.printStackTrace();
            MessageDialog.showMessageDialog(gui, "Database error",
                "Could not load top products.\nSee the terminal for details.", MessageDialogButton.OK);
        }
    }
}
