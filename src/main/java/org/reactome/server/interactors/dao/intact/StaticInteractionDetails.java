package org.reactome.server.interactors.dao.intact;

import org.reactome.server.interactors.dao.InteractionDetailsDAO;
import org.reactome.server.interactors.database.InteractorsDatabase;
import org.reactome.server.interactors.model.InteractionDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class StaticInteractionDetails implements InteractionDetailsDAO {

    final Logger logger = LoggerFactory.getLogger(StaticInteractionDetails.class);

    private Connection connection;

    private final String TABLE = "INTERACTION_DETAILS";
    private final String ALL_COLUMNS = "INTERACTION_ID, INTERACTION_AC";

    public StaticInteractionDetails(InteractorsDatabase database) {
        this.connection = database.getConnection();
    }
    
    /**
     * Create interactions details using batch approach
     * @throws SQLException
     */
    public boolean create(List<InteractionDetails> interactionDetails) throws SQLException {
        connection.setAutoCommit(false);

        try {
            String query = "INSERT INTO " + TABLE + " (" + ALL_COLUMNS + ") "
                    + "VALUES(?, ?)";

            PreparedStatement pstm = connection.prepareStatement(query);

            for (InteractionDetails interactionDetail : interactionDetails) {
                pstm.setLong(1, interactionDetail.getInteractionId());
                pstm.setString(2, interactionDetail.getInteractionAc());

                pstm.addBatch();
            }

            pstm.executeBatch();

            connection.commit();
        } catch(SQLException s){
            logger.error("");
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
        }

        return true;

    }

    public List<InteractionDetails> getByInteraction(Long interactionId) throws SQLException {
        List<InteractionDetails> interactionsDetails = new ArrayList<>();

        try {
            // TODO: IMPROVE HERE
            String query = "SELECT * FROM INTERACTION_DETAILS WHERE INTERACTION_ID = ?";

            PreparedStatement pstm = connection.prepareStatement(query);
            pstm.setLong(1, interactionId);

            ResultSet rs = pstm.executeQuery();

            while(rs.next()){
                InteractionDetails interactionDetails = buildInteractionDetails(rs);

                interactionsDetails.add(interactionDetails);
            }



        }catch (SQLException e){
            logger.error("An error has occurred during interaction batch insert. Please check the following exception.");
            throw new SQLException(e);
        }

        return interactionsDetails;

    }

    public InteractionDetails buildInteractionDetails(ResultSet rs) throws SQLException {
        InteractionDetails interactionDetails = new InteractionDetails();
        interactionDetails.setId(rs.getLong("ID"));
        interactionDetails.setInteractionId(rs.getLong("INTERACTION_ID"));
        interactionDetails.setInteractionAc(rs.getString("INTERACTION_AC"));

        return interactionDetails;
    }
}
