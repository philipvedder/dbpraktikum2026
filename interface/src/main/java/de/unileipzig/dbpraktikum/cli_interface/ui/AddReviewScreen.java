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

/** Collects and stores a new review for one product. */
public class AddReviewScreen {
    private final WindowBasedTextGUI gui;
    private final DBInterface db;
    private final Product product;

    private boolean saved;

    public AddReviewScreen(WindowBasedTextGUI gui, DBInterface db, Product product) {
        this.gui = gui;
        this.db = db;
        this.product = product;
    }

    public boolean show() {
        saved = false;

        BasicWindow window = new BasicWindow("Add review");
        Panel root = new Panel(new LinearLayout(Direction.VERTICAL));

        root.addComponent(new Label("Product: " + product.getId() + " - " + product.getTitle()));

        TextBox usernameBox = new TextBox(new TerminalSize(35, 1));
        TextBox pointsBox = new TextBox(new TerminalSize(5, 1));
        TextBox reviewBox = new TextBox(new TerminalSize(75, 6), TextBox.Style.MULTI_LINE);

        Panel usernameRow = new Panel(new LinearLayout(Direction.HORIZONTAL));
        usernameRow.addComponent(new Label("Username: "));
        usernameRow.addComponent(usernameBox);
        root.addComponent(usernameRow);

        Panel pointsRow = new Panel(new LinearLayout(Direction.HORIZONTAL));
        pointsRow.addComponent(new Label("Points (1-5): "));
        pointsRow.addComponent(pointsBox);
        root.addComponent(pointsRow);

        root.addComponent(new Label("Review text (optional):"));
        root.addComponent(reviewBox);

        Label status = new Label("Enter a username and points from 1 to 5.");
        root.addComponent(status);

        Panel buttons = new Panel(new LinearLayout(Direction.HORIZONTAL));
        buttons.addComponent(new Button("Save", () -> saveReview(
            window, status, usernameBox.getText(), pointsBox.getText(), reviewBox.getText()
        )));
        buttons.addComponent(new Button("Cancel", window::close));
        root.addComponent(buttons);

        window.setComponent(root);
        gui.addWindowAndWait(window);
        return saved;
    }

    private void saveReview(BasicWindow window, Label status, String usernameInput,
            String pointsInput, String textInput) {
        String username = usernameInput == null ? "" : usernameInput.trim();
        if (username.isEmpty()) {
            showInvalidInput(status, "Enter a username.");
            return;
        }
        if (username.length() > 256) {
            showInvalidInput(status, "The username must not exceed 256 characters.");
            return;
        }

        final int points;
        try {
            points = Integer.parseInt(pointsInput == null ? "" : pointsInput.trim());
            if (points < 1 || points > 5) {
                throw new NumberFormatException("Points outside range");
            }
        } catch (NumberFormatException ex) {
            showInvalidInput(status, "Enter whole-number points from 1 to 5.");
            return;
        }

        String text = textInput == null ? "" : textInput.trim();

        try {
            db.addNewReview(product, username, points, text.isEmpty() ? null : text);
            saved = true;
            MessageDialog.showMessageDialog(gui, "Review saved",
                "The review was saved successfully.", MessageDialogButton.OK);
            window.close();
        } catch (IllegalArgumentException ex) {
            showInvalidInput(status, ex.getMessage());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            status.setText("Could not save the review.");
            MessageDialog.showMessageDialog(gui, "Database error",
                "Could not save the review.\nSee the terminal for details.",
                MessageDialogButton.OK);
        }
    }

    private void showInvalidInput(Label status, String message) {
        status.setText(message);
        MessageDialog.showMessageDialog(gui, "Invalid review", message, MessageDialogButton.OK);
    }
}
