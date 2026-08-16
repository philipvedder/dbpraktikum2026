package de.unileipzig.dbpraktikum.cli_interface;

import de.unileipzig.dbpraktikum.cli_interface.db_interface.*;
import de.unileipzig.dbpraktikum.cli_interface.model.Product;

public class MediaStoreCliInterface {
    public static void main(String[] args) {

        DBInterface db = new DBInterfaceImpl();

        db.init();
        Product p = db.getProduct("3407788738");
        db.finish();

        System.out.println(p.getTitle());
    }
}
