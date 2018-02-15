package org.reactome.server.interactors.database;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class InteractorsDatabase {
    private Connection connection;

    public InteractorsDatabase(String fileName) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Enabling FK. It is disabled by default in SQLite.
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        this.connection = DriverManager.getConnection("jdbc:sqlite:".concat(fileName), config.toProperties());
    }

    public Connection getConnection(){
        return connection;
    }
}
