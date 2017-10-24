package org.reactome.server.interactors.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class InteractorsDatabase {

    static final Logger logger = LoggerFactory.getLogger("InteractorsDatabase");

    private Connection connection;

    public InteractorsDatabase(String fileName) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        /**
         * Enabling FK. It is disabled by default in SQLite.
         */
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);

        // create a database connection
        this.connection = DriverManager.getConnection("jdbc:sqlite:".concat(fileName), config.toProperties());
    }

    //TODO: Constructor for MySQL?

    public Connection getConnection(){
        return connection;
    }

    public void closeConnection() {
        try {
            if(connection!=null)  connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection = null;

            // Now deregister JDBC drivers in this context's ClassLoader:
            // Get the webapp's ClassLoader
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            // Loop through all drivers
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                if (driver.getClass().getClassLoader() == cl) {
                    // This driver was registered by the webapp's ClassLoader, so deregister it:
                    try {
                        logger.info("Deregistering JDBC driver {}", driver);
                        DriverManager.deregisterDriver(driver);
                    } catch (SQLException ex) {
                        logger.error("Error deregistering JDBC driver {}", driver, ex);
                    }
                } else {
                    // driver was not registered by the webapp's ClassLoader and may be in use elsewhere
                    logger.trace("Not deregistering JDBC driver {} as it does not belong to this webapp's ClassLoader", driver);
                }
            }
        }
    }
}
