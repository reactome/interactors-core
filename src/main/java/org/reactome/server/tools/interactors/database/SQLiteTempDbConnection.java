package org.reactome.server.tools.interactors.database;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class SQLiteTempDbConnection extends DatabaseConnection {

    private static SQLiteTempDbConnection sqliteConnection = null;

    public static SQLiteTempDbConnection getInstance() {
        if(sqliteConnection == null){
            sqliteConnection = new SQLiteTempDbConnection();
        }

        return sqliteConnection;
    }

    private SQLiteTempDbConnection() {
        if(connection == null) {
            setConnection(TEMP_DATABASE);
        }
    }

}
