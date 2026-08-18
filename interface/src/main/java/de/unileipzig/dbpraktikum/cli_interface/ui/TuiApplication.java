package de.unileipzig.dbpraktikum.cli_interface.ui;

import java.io.IOException;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.DBInterface;

public class TuiApplication {
    private final DBInterface db;

    public TuiApplication(DBInterface db) {
        this.db = db;
    }

    public void run() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminalEmulator();

        try (Screen screen = new TerminalScreen(terminal)) {
            screen.startScreen();

            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);
            MainMenuScreen mainMenu = new MainMenuScreen(gui, db);

            mainMenu.show();
        }
    }
}
