package de.unileipzig.dbpraktikum.loader.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection class. 
 * Uses the PostgreSQL JDBC driver to open a connection to a Database with the corresponding credentials. 
 */
public class DB {
    //Connection params
    private final String url = "jdbc:postgresql://localhost:5432/compose-postgres";
    private final String user = "compose-postgres";
    private final String password = "compose-postgres";

    /**
     * Opens a new connection to the Database
     * @return A Connection obj, which can be used to interact with the Database. 
     * @throws SQLException thrown on errors while opening the connection.
     */
    public Connection openConnection() throws SQLException {
        Connection con = DriverManager.getConnection(
            url, 
            user, 
            password
        );

        return con;
    }
}
