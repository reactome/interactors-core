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

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorImpl.class);

    private SQLiteConnection database = SQLiteConnection.getInstance();

    private final String TABLE = "INTERACTION_DETAILS";
    private final String ALL_COLUMNS = "INTERACTION_ID, INTERACTION_AC";
    private final String ALL_COLUMNS_SEL = "ID, ".concat(ALL_COLUMNS);

    public InteractionDetails create(InteractionDetails interactionDetails) throws SQLException {
        Connection conn = database.getConnection();
        System.out.println(conn);
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
