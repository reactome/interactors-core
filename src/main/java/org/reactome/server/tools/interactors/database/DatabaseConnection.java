package org.reactome.server.tools.interactors.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public abstract class DatabaseConnection {
    final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    protected Connection connection;

    protected final String FINAL_DATABASE = "interactors.db";
    protected final String TEMP_DATABASE = "interactors-temp.db";

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(String dbName) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:".concat(dbName));

        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

}
