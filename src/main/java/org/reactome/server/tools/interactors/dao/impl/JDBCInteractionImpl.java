package org.reactome.server.tools.interactors.dao.impl;

import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.database.SQLiteConnection;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.InteractionDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class JDBCInteractionImpl implements InteractionDAO {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorImpl.class);

    private SQLiteConnection database = SQLiteConnection.getInstance();

    private final String TABLE = "INTERACTION";
    private final String ALL_COLUMNS = "INTERACTOR_A, INTERACTOR_B, AUTHOR_SCORE, MISCORE, INTERACTION_RESOURCE_ID";
    private final String ALL_COLUMNS_SEL = "ID, ".concat(ALL_COLUMNS);

    public Interaction create(Interaction interaction) throws SQLException {
        Connection conn = database.getConnection();

        try {
            String query = "INSERT INTO " + TABLE + " (" + ALL_COLUMNS + ") "
                    + "VALUES(?, ?, ?, ?, ?)";

            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setLong(1, interaction.getInteractorA().getId());
            pstm.setLong(2, interaction.getInteractorB().getId());
            pstm.setDouble(3, interaction.getAuthorScore());
            pstm.setDouble(4, interaction.getIntactScore());
            pstm.setLong(5, interaction.getInteractionResourceId());

            if (pstm.executeUpdate() > 0) {
                try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        interaction.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating Interaction failed, no ID obtained.");
                    }
                }
            }
        } finally {
            //conn.close();
        }

        return interaction;
    }

    /**
     * Create interactions using same transaction.
     *
     * @param interactions
     * @return
     * @throws SQLException
     */
    public boolean create(List<Interaction> interactions) throws SQLException {
        Connection conn = database.getConnection();
        conn.setAutoCommit(false);

        try {
            String query = "INSERT INTO " + TABLE + " (" + ALL_COLUMNS + ") "
                    + "VALUES(?, ?, ?, ?, ?)";

            PreparedStatement pstm = conn.prepareStatement(query);

            for (Interaction interaction : interactions) {
                pstm.setLong(1, interaction.getInteractorA().getId());
                pstm.setLong(2, interaction.getInteractorB().getId());
                pstm.setDouble(3, interaction.getAuthorScore());
                pstm.setDouble(4, interaction.getIntactScore());
                pstm.setLong(5, interaction.getInteractionResourceId());

                //pstm.addBatch();
                if (pstm.executeUpdate() > 0) {
                    try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            interaction.setId(generatedKeys.getLong(1));
                        } else {
                            throw new SQLException("Creating Interaction failed, no ID obtained.");
                        }
                    }
                }


                // maybe create the interaction details here, but it is not a good practice have two DAO's talking
                // each other.

            }

            conn.commit();
        }catch (SQLException e){
            logger.error("An error has occurred during interaction batch insert. Please check the following exception.");
            conn.rollback();

            throw new SQLException(e);

        } finally {
            conn.setAutoCommit(true);
            //conn.close();
        }

        return true;
    }

    public boolean update(Interaction interaction) throws SQLException {
        return false;
    }

    public Interaction getById(String id) throws SQLException {
        return null;
    }

    public List<Interaction> getAll() throws SQLException {
        return null;
    }

    public boolean delete(String id) throws SQLException {
        return false;
    }

}
