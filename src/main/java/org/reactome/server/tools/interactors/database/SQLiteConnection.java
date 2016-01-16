package org.reactome.server.tools.interactors.database;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class SQLiteConnection extends DatabaseConnection {

    private static SQLiteConnection sqliteConnection = null;

    public static SQLiteConnection getInstance() {
        if (sqliteConnection == null) {
            sqliteConnection = new SQLiteConnection();
        }

        return sqliteConnection;
    }

    private SQLiteConnection() {
        String dbFile;
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/db.properties"));
            dbFile = props.getProperty("database");
        } catch (IOException e) {
            logger.error("Can't read properties file.", e);
            dbFile = "/tmp/interactors.db";
        }

        if (connection == null) {
            setConnection(dbFile);
            logger.info("Database Initialized at " + dbFile);
        }
    }


}
