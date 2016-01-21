package org.reactome.server.tools.interactors.util;

import org.reactome.server.tools.interactors.database.InteractorsDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class InteractorDatabaseGenerator {

    static final Logger logger = LoggerFactory.getLogger(InteractorDatabaseGenerator.class);

    private static void generateNewDatabase(Connection connection) throws ClassNotFoundException {
        logger.info("Creating interactors database.");

        try {
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

            logger.info("Creating participants");
            statement.executeUpdate(QueryStatement.CREATE_TABLE_INTERACTION_DETAILS);

            /** Create indexes **/
            logger.info("Creating indexes");
            statement.executeUpdate(QueryStatement.CREATE_INTERACTOR_ACC_INDEX);
            statement.executeUpdate(QueryStatement.CREATE_INTERACTOR_A_INDEX);
            statement.executeUpdate(QueryStatement.CREATE_INTERACTOR_B_INDEX);
            statement.executeUpdate(QueryStatement.CREATE_INTERACTION_DETAILS_ID_INDEX);

            /** Pre-populate tables **/
            logger.info("Populate table interaction resource");
            statement.executeUpdate(QueryStatement.INSERT_INTERACTION_RESOURCE_STATIC);

            logger.info("Populate table interactor resource");
            statement.executeUpdate(QueryStatement.INSERT_INTERACTOR_RESOURCE_UNDEFINED);
            statement.executeUpdate(QueryStatement.INSERT_INTERACTOR_RESOURCE_UNIPROT);
            statement.executeUpdate(QueryStatement.INSERT_INTERACTOR_RESOURCE_CHEBI);

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
        try {
            String file = "/Users/reactome/interactors/interactors.db";
            InteractorsDatabase interactors = new InteractorsDatabase(file);
            generateNewDatabase(interactors.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

