package de.unileipzig.dbpraktikum.cli_interface;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.*;
import de.unileipzig.dbpraktikum.cli_interface.ui.TuiApplication;

public class MediaStoreCliInterface {
    public static void main(String[] args) {

        DBInterface db = new DBInterfaceImpl();
        
        try {
            db.init();

            TuiApplication tui = new TuiApplication(db);
            tui.run();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            db.finish();
        }
    }
}
