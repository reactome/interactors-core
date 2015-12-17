package org.reactome.server.tools.interactors.dao.impl;

import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.database.SQLiteConnection;
import org.reactome.server.tools.interactors.database.SQLiteTempDbConnection;
import org.reactome.server.tools.interactors.model.Interaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class JDBCInteractionImpl implements InteractionDAO {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorImpl.class);

    private SQLiteConnection database = SQLiteConnection.getInstance();

    private final String TABLE = "INTERACTION";
    private final String ALL_COLUMNS = "INTERACTION_A, INTERACTION_B, INTERACTION_ID, AUTHOR_SCORE, MISCORE, INTERACTION_RESOURCE_ID";
    private final String ALL_COLUMNS_SEL = "ID, ".concat(ALL_COLUMNS);

    public Interaction create(Interaction interaction) throws SQLException {
        Connection conn = database.getConnection();

        try {
            String query = "INSERT INTO " + TABLE + " (" + ALL_COLUMNS + ") "
                    + "VALUES(?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pstm = conn.prepareStatement(query);
            pstm.setLong(1, interaction.getId());
            pstm.setLong(2, interaction.getInteractorA().getId());
            pstm.setLong(3, interaction.getInteractorB().getId());
            pstm.setString(4, interaction.getInteractionId());
            pstm.setDouble(5, interaction.getAuthorScore());
            pstm.setDouble(6, interaction.getIntactScore());
            pstm.setLong(7, interaction.getInteractionResourceId());

            if (pstm.executeUpdate() > 0) {
                return null;
            }

        } finally {
            //conn.close();
        }

        return null;
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
