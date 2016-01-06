package org.reactome.server.tools.interactors.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public abstract class DatabaseConnection {
    final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    protected Connection connection;

    //TODO SET A PATH TO THE DATABASE
    protected final String FINAL_DATABASE = "/Users/reactome/interactors/interactors.db";

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

            /**
             * Enabling FK. It is disabled by default in SQLite.
             */
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);

            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:".concat(dbName),config.toProperties());

        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

}
