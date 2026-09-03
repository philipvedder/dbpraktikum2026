package de.unileipzig.dbpraktikum.cli_interface.ui;

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

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.model.Product;

public class ProductIdScreen {
    private final WindowBasedTextGUI gui;
    private final DBInterface db;

    private Label errorLabel;
    
    public ProductIdScreen(WindowBasedTextGUI gui, DBInterface db) {
        this.gui = gui;
        this.db = db;
    }

    public void show() {
        // Setup terminal and screen layers
        BasicWindow window = new BasicWindow("Product List");

        // Create panel to hold components
        Panel root = new Panel();
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        // --- Add Text box for ID
        TextBox idTextBox = new TextBox(new TerminalSize(35, 1));
        Panel idPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        idPanel.addComponent(new Label("Product ID: "));
        idPanel.addComponent(idTextBox);
        idPanel.addComponent(new Button("Search", () -> findProduct(idTextBox.getText())));

        // --- Add Label for errors
        errorLabel = new Label("");

        // --- Add bottom buttons
        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new Button("Back", window::close));

        //Add root components
        root.addComponent(idPanel);
        root.addComponent(errorLabel);
        root.addComponent(new EmptySpace(new TerminalSize(1, 1)));
        root.addComponent(buttons);

        // Show window
        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    private void findProduct(String id) {
        Product p = db.getProduct(id.trim());

        if (p == null) {
            errorLabel.setText("Product with ID " + id + " not found!");
            return;
        }

        new ProductDetailScreen(gui, db, p).show();
    }
}
