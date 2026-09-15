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
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.table.Table;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;
import de.unileipzig.dbpraktikum.cli_interface.model.Customer;
import de.unileipzig.dbpraktikum.cli_interface.model.Review;

/** Collects a rating threshold and displays the users returned by getTrolls. */
public class TrollsScreen {
    /**
     * Lanterna TUI Screen for viewing Customers with a avg Rating less than k
     */

    // Lanterna TUI and DB Interface
    private final WindowBasedTextGUI gui;
    private final DBInterface db;

    // Component storage
    private Table<String> usersTable;
    private Label status;

    public TrollsScreen(WindowBasedTextGUI gui, DBInterface db) {
        this.gui = gui;
        this.db = db;
    }

    /**
     * Constructs and shows the TUI window
     */
    public void show() {
        BasicWindow window = new BasicWindow("Trolls");
        Panel root = new Panel(new LinearLayout(Direction.VERTICAL));

        // Instructions and textbox for threshold
        root.addComponent(new Label("Find users whose average rating is strictly below the threshold."));
        Panel search = new Panel(new LinearLayout(Direction.HORIZONTAL));
        TextBox thresholdBox = new TextBox(new TerminalSize(15, 1));
        search.addComponent(new Label("Average rating below: "));
        search.addComponent(thresholdBox);
        search.addComponent(new Button("Search", () -> loadUsers(thresholdBox.getText())));
        root.addComponent(search);
        status = new Label("Enter a number (for example 3.5 or 3,5), then choose Search.");
        root.addComponent(status);

        // Users table
        usersTable = new Table<>("User ID", "User", "Average rating", "Reviews");
        usersTable.setPreferredSize(new TerminalSize(95, 23));
        usersTable.setCellSelection(false);
        root.addComponent(usersTable);
        root.addComponent(new Button("Back", window::close));

        // Show window
        window.setComponent(root);
        gui.addWindowAndWait(window);
    }

    /**
     * Load usersand update Table accordingly 
     * @param input String input from Text box to determine threshold k
     */
    private void loadUsers(String input) {
        usersTable.getTableModel().clear();

        // Sanitize input
        final float threshold;
        try {
            // Accept both decimal separators used in the task's input examples.
            threshold = Float.parseFloat(input.trim().replace(',', '.'));
            if (!Float.isFinite(threshold)) {
                throw new NumberFormatException("Threshold must be finite");
            }
        } catch (NumberFormatException ex) {
            // No valid float
            status.setText("Enter a valid numeric threshold.");
            MessageDialog.showMessageDialog(gui, "Invalid rating", "Enter a number, for example 3.5 or 3,5.\nEmpty input, NaN and infinity are not valid.", MessageDialogButton.OK);
            return;
        }

        // Load users
        try {
            // Get trolls from DBInterface and sort by name
            List<Customer> users = new ArrayList<>(db.getTrolls(threshold));
            users.sort(Comparator.comparing(Customer::getName, String.CASE_INSENSITIVE_ORDER).thenComparing(Customer::getId));

            // Add Table row for each user
            for (Customer user : users) {
                List<Review> reviews = user.getReviews();
                double average = reviews.stream().mapToInt(Review::getPoints).average().orElse(Double.NaN);
                usersTable.getTableModel().addRow(
                    user.getId().toString(),
                    user.getName(),
                    Double.isNaN(average) ? "-" : String.format("%.2f", average),
                    Integer.toString(reviews.size())
                );
            }

            // Set Status text accordingly
            status.setText(users.isEmpty()
                ? "No users found with average rating below " + threshold + "."
                : "Found " + users.size() + " users with average rating below " + threshold + ".");
        } catch (RuntimeException ex) {
            // Empty table if errors exist.
            usersTable.getTableModel().clear();
            status.setText("Could not load users.");
            ex.printStackTrace();
            MessageDialog.showMessageDialog(gui, "Database error", "Could not load users.\nSee the terminal for details.", MessageDialogButton.OK);
        }
    }
}
