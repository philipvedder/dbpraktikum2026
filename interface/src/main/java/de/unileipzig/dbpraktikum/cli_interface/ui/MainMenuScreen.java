package de.unileipzig.dbpraktikum.cli_interface.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;

public class MainMenuScreen {
    private final WindowBasedTextGUI gui;
    private final DBInterface db;

    public MainMenuScreen(WindowBasedTextGUI gui, DBInterface db) {
        this.gui = gui;
        this.db = db;
    }

    public void show() {
        // Setup terminal and screen layers
        BasicWindow window = new BasicWindow("Media Store");

        // Setup basic menu
        ActionListBox menu = new ActionListBox(new TerminalSize(30,10));

        menu.addItem("Product List", () -> new ProductListScreen(gui, db).show());
        menu.addItem("Product by ID", () -> new ProductByIdScreen(gui, db).show());
        menu.addItem("Category Tree", () -> new CategoryTreeScreen(gui, db).show());
        menu.addItem("Top Products", () -> new TopProductsScreen(gui, db).show());
        menu.addItem("Trolls", () -> new TrollsScreen(gui, db).show());
        menu.addItem("Exit", window::close);

        // Start gui
        window.setComponent(menu);
        gui.addWindowAndWait(window);
    }
}
