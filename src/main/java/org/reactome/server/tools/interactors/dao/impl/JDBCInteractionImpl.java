package org.reactome.server.tools.interactors.dao.impl;

import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.database.SQLiteConnection;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.InteractionDetails;
import org.reactome.server.tools.interactors.model.Interactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
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

    public List<Interaction> getByAcc(String acc, Long resourceId) throws SQLException{
        List<String> accList = new ArrayList<>(1);
        accList.add(acc);

        return getByAcc(accList, resourceId);
    }

    public List<Interaction> getByAcc(List<String> accs, Long resourceId) throws SQLException{
        Connection conn = database.getConnection();

        List<Interaction> interactions = new ArrayList<>();

        try {
            String query = "SELECT   INTERACTION.ID AS 'INTERACTION_ID', " +
                                    "INTERACTORA.ID AS 'ID_A', INTERACTORA.ACC AS 'ACC_A', INTERACTORA.ALIAS AS 'ALIAS_A', INTERACTORA.INTERACTOR_RESOURCE_ID AS 'INTERACTOR_RESOURCE_A_ID', " +
                                    "INTERACTORB.ID AS 'ID_B', INTERACTORB.ACC AS 'ACC_B', INTERACTORB.ALIAS AS 'ALIAS_B', INTERACTORB.INTERACTOR_RESOURCE_ID AS 'INTERACTOR_RESOURCE_B_ID', " +
                                    "INTERACTION.AUTHOR_SCORE, " +
                                    "INTERACTION.MISCORE, " +
                                    "INTERACTION.INTERACTION_RESOURCE_ID " +
                           "FROM     INTERACTION, INTERACTOR AS INTERACTORA, INTERACTOR AS INTERACTORB " +
                           "WHERE    INTERACTORA.ID = INTERACTION.INTERACTOR_A " +
                           "AND      INTERACTORB.ID = INTERACTION.INTERACTOR_B " +
                           "AND      (INTERACTION.INTERACTOR_A = (select id from interactor where ACC = ?) OR INTERACTION.INTERACTOR_B = (select id from interactor where ACC = ?)) " +
                           "AND      INTERACTION.INTERACTION_RESOURCE_ID = ? " +
                           "ORDER BY INTERACTION.MISCORE DESC";

            for (String acc : accs) {
                PreparedStatement pstm = conn.prepareStatement(query);
                pstm.setString(1, acc);
                pstm.setString(2, acc);
                pstm.setLong(3, resourceId);

                ResultSet rs = pstm.executeQuery();
                while(rs.next()){
                    Interaction interaction = buildInteraction(acc, rs);

                    interactions.add(interaction);
                }

            }

        }catch (SQLException e){
            logger.error("An error has occurred during interaction batch insert. Please check the following exception.");
            throw new SQLException(e);

        } finally {
            //conn.close();
        }

        return interactions;
    }

    private Interaction buildInteraction(String acc, ResultSet rs) throws SQLException {
        Interaction interaction = new Interaction();
        interaction.setId(rs.getLong("INTERACTION_ID"));

        Interactor interactorA = new Interactor();
        interactorA.setId(rs.getLong("ID_A"));
        interactorA.setAcc(rs.getString("ACC_A"));
        interactorA.setAlias(rs.getString("ALIAS_A"));
        interactorA.setInteractorResourceId(rs.getLong("INTERACTOR_RESOURCE_A_ID"));

        Interactor interactorB = new Interactor();
        interactorB.setId(rs.getLong("ID_B"));
        interactorB.setAcc(rs.getString("ACC_B"));
        interactorB.setAlias(rs.getString("ALIAS_B"));
        interactorB.setInteractorResourceId(rs.getLong("INTERACTOR_RESOURCE_B_ID"));

        /**
         * If A interacts with B and B with A we are talking about the same interaction, so
         * just to keep it easy to create the JSON - the interactor in the query will be always on side of A
         * otherwise just set them as A.set(b) and B.set(a).
         */
        if(acc.equals(interactorA.getAcc())) {
            interaction.setInteractorA(interactorA);
            interaction.setInteractorB(interactorB);
        }else {
            interaction.setInteractorA(interactorB);
            interaction.setInteractorB(interactorA);
        }

        interaction.setAuthorScore(rs.getDouble("AUTHOR_SCORE"));
        interaction.setIntactScore(rs.getDouble("MISCORE"));
        interaction.setInteractionResourceId(rs.getLong("INTERACTION_RESOURCE_ID"));

        return interaction;
    }

}
