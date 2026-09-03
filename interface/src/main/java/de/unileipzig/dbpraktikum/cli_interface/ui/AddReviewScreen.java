package de.unileipzig.dbpraktikum.cli_interface.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.ComboBox;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.model.Product;
import de.unileipzig.dbpraktikum.cli_interface.model.Review;

public class AddReviewScreen {
    private final WindowBasedTextGUI gui;
    private final DBInterface db;    
    private Product product = null;

    BasicWindow window = null;
    Label errorLabel = null;

    public AddReviewScreen(WindowBasedTextGUI gui, DBInterface db, Product p) {
        this.gui = gui;
        this.db = db;
        this.product = p;
    }    

    public void show() {
        // Setup terminal and screen layers
        window = new BasicWindow("Product List");

        // Create panel to hold components
        Panel root = new Panel();
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        // --- Add Review fields
        Panel review = new Panel(new GridLayout(2));
        review.addComponent(new Label("Product ID"));
        review.addComponent(new Label(product.getId()));
        
        review.addComponent(new Label("Username"));
        TextBox usernameTextBox = new TextBox(new TerminalSize(35, 1));
        review.addComponent(usernameTextBox);

        review.addComponent(new Label("Points"));
        ComboBox<Integer> pointsPicker = new ComboBox<Integer>(1,2,3,4,5);
        review.addComponent(pointsPicker);

        review.addComponent(new Label("Text"));
        TextBox reviewTextBox = new TextBox(new TerminalSize(35, 10));
        review.addComponent(reviewTextBox);

        review.addComponent(new Label(""));
        review.addComponent(new Button("Save", () -> save(usernameTextBox.getText(), pointsPicker.getSelectedItem(), reviewTextBox.getText())));

        // --- Add bottom buttons
        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new Button("Back", window::close));

        // --- Add error label
        errorLabel = new Label("");

        //Add root components
        root.addComponent(review);
        root.addComponent(errorLabel);
        root.addComponent(buttons);

        // Show window
        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    private void save(String username, Integer points, String text) {
        // Validate
        String cleanUsername = username.trim().toLowerCase();

        if (cleanUsername.isBlank()) {
            errorLabel.setText("Please set a proper username");
            return;
        }

        // Save Review
        Review r = db.addNewReview(product, cleanUsername, points, text.trim());

        if (r != null) {
            window.close();
        } else {
            errorLabel.setText("Could not save Review.");
        }
    }
}
