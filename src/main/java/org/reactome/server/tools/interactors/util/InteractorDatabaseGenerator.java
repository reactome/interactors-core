package org.reactome.server.tools.interactors.util;

import org.reactome.server.tools.interactors.database.SQLiteConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class InteractorDatabaseGenerator {

    static final Logger logger = LoggerFactory.getLogger(InteractorDatabaseGenerator.class);

    private static void generateNewDatabase() throws ClassNotFoundException {
        logger.info("Creating interactors database.");
        Connection connection = null;

        try {
            connection = SQLiteConnection.getInstance().getConnection();

            Statement statement = connection.createStatement();

            /** Create our tables **/
            logger.info("Creating table Interactor_Resource.");
            statement.executeUpdate(QueryStatement.CREATE_TABLE_INTERACTOR_RESOURCE);

            logger.info("Creating table Interaction Resource");
            statement.executeUpdate(QueryStatement.CREATE_TABLE_INTERACTION_RESOURCE);

            logger.info("Creating interactor");
            statement.executeUpdate(QueryStatement.CREATE_TABLE_INTERACTOR);

            logger.info("Creating interaction");
            statement.executeUpdate(QueryStatement.CREATE_TABLE_INTERACTION);

            logger.info("Creating paricipants");
            statement.executeUpdate(QueryStatement.CREATE_TABLE_INTERACTION_DETAILS);


            /** Pre-populate tables **/
            logger.info("Populate table interaction resource");
            statement.executeUpdate(QueryStatement.INSERT_INTERACTION_RESOURCE_INTACT);

            logger.info("Populate table interactor resource");
            statement.executeUpdate(QueryStatement.INSERT_INTERACTOR_RESOURCE_CHEBI);
            statement.executeUpdate(QueryStatement.INSERT_INTERACTOR_RESOURCE_UNIPROT);

            logger.info("Database has been created properly");

        } catch (SQLException e) {
            logger.error("Error creating interactor database", e);
        } catch (Exception e) {
            logger.error("Generic exception occurred. Please check stacktrace for further information", e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {

        // we can always start from a temp db and rename later on to a final...
        generateNewDatabase();
    }
}

