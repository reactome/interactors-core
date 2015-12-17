package org.reactome.server.tools.interactors.database;

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
        if (connection == null) {
            setConnection(FINAL_DATABASE);
        }
    }


}
