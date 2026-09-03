package de.unileipzig.dbpraktikum.cli_interface.ui;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.model.Review;

public class ShowReviewScreen {
    private final WindowBasedTextGUI gui;
    private final DBInterface db;    
    private Review review;

    BasicWindow window = null;

    public ShowReviewScreen(WindowBasedTextGUI gui, DBInterface db, Review r) {
        this.gui = gui;
        this.db = db;
        this.review = r;
    }    

    public void show() {
        // Setup terminal and screen layers
        window = new BasicWindow("Product List");

        // Create panel to hold components
        Panel root = new Panel();
        root.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        // --- Add Review fields
        Panel reviewPanel = new Panel(new GridLayout(2));
        reviewPanel.addComponent(new Label("Product ID"));
        reviewPanel.addComponent(new Label(review.getProduct().getId()));
        
        reviewPanel.addComponent(new Label("Username"));
        reviewPanel.addComponent(new Label(review.getCustomer().getName()));

        reviewPanel.addComponent(new Label("Points"));
        reviewPanel.addComponent(new Label(review.getPoints().toString()));

        reviewPanel.addComponent(new Label("Date"));
        reviewPanel.addComponent(new Label(formatDateAndTime(review.getDate())));

        reviewPanel.addComponent(new Label("Text"));

        // -- Add Label Panel for review text to new row
        Panel textPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Label textLabel = new Label("");
        textLabel.setLabelWidth(100);
        textLabel.setText(review.getText());
        textPanel.addComponent(textLabel);

        // --- Add bottom buttons
        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new Button("Back", window::close));

        //Add root components
        root.addComponent(reviewPanel);
        root.addComponent(textPanel);
        root.addComponent(buttons);

        // Show window
        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    private String formatDateAndTime(Timestamp t) {
        return new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(t);
    }
}
