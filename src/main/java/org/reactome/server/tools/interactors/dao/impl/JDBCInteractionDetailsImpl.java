package org.reactome.server.tools.interactors.dao.impl;

import org.reactome.server.tools.interactors.dao.InteractionDetailsDAO;
import org.reactome.server.tools.interactors.database.SQLiteConnection;
import org.reactome.server.tools.interactors.model.InteractionDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class JDBCInteractionDetailsImpl implements InteractionDetailsDAO {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractionDetailsImpl.class);

    private SQLiteConnection database = SQLiteConnection.getInstance();

    private final String TABLE = "INTERACTION_DETAILS";
    private final String ALL_COLUMNS = "INTERACTION_ID, INTERACTION_AC";
    private final String ALL_COLUMNS_SEL = "ID, ".concat(ALL_COLUMNS);

    public InteractionDetails create(InteractionDetails interactionDetails) throws SQLException {
        Connection conn = database.getConnection();

        try {
            String query = "INSERT INTO " + TABLE + " (" + ALL_COLUMNS + ") "
                    + "VALUES(?, ?)";

            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setLong(1, interactionDetails.getInteractionId());
            pstm.setString(2, interactionDetails.getInteractionAc());

            if(pstm.executeUpdate() > 0) {
                try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        interactionDetails.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating InteractorDetails failed, no ID obtained.");
                    }
                }
            }

        } finally {
            //conn.close();
        }

        return interactionDetails;
    }

    /**
     * Create interactions details using batch approach
     *
     * @param interactionDetails
     * @return
     * @throws SQLException
     */
    public boolean create(List<InteractionDetails> interactionDetails) throws SQLException {
        Connection conn = database.getConnection();
        conn.setAutoCommit(false);

        try {
            String query = "INSERT INTO " + TABLE + " (" + ALL_COLUMNS + ") "
                    + "VALUES(?, ?)";

            PreparedStatement pstm = conn.prepareStatement(query);

            for (InteractionDetails interactionDetail : interactionDetails) {
                pstm.setLong(1, interactionDetail.getInteractionId());
                pstm.setString(2, interactionDetail.getInteractionAc());

                pstm.addBatch();
//                if (pstm.executeUpdate() > 0) {
//                    try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
//                        if (generatedKeys.next()) {
//                            interactionDetails.setId(generatedKeys.getLong(1));
//                        } else {
//                            throw new SQLException("Creating InteractorDetails failed, no ID obtained.");
//                        }
//                    }
//                }
            }

            pstm.executeBatch();

            conn.commit();
        } catch(SQLException s){
            logger.error("");
            conn.rollback();
        } finally {
            conn.setAutoCommit(true);
        }

        return true;

    }

    public boolean update(InteractionDetails interaction) throws SQLException {
        return false;
    }

    public InteractionDetails getById(String id) throws SQLException {
        return null;
    }

    public List<InteractionDetails> getAll() throws SQLException {
        return null;
    }

    public boolean delete(String id) throws SQLException {
        return false;
    }

}
