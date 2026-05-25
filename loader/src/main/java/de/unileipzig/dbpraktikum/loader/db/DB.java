package de.unileipzig.dbpraktikum.loader.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private final String url = "jdbc:postgresql://localhost:5432/compose-postgres";
    private final String user = "compose-postgres";
    private final String password = "compose-postgres";

    public Connection openConnection() throws SQLException {
        Connection con = DriverManager.getConnection(
            url, 
            user, 
            password
        );

        return con;
    }
}
