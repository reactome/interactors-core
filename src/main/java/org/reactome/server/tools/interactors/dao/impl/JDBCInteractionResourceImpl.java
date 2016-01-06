package org.reactome.server.tools.interactors.dao.impl;

import org.reactome.server.tools.interactors.dao.InteractionResourceDAO;
import org.reactome.server.tools.interactors.database.SQLiteConnection;
import org.reactome.server.tools.interactors.model.InteractionResource;
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

public class JDBCInteractionResourceImpl implements InteractionResourceDAO {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorImpl.class);

    private SQLiteConnection database = SQLiteConnection.getInstance();

    private final String TABLE = "INTERACTION_RESOURCE";
    private final String ALL_COLUMNS = "NAME, URL";
    private final String ALL_COLUMNS_SEL = "ID, ".concat(ALL_COLUMNS);

    public InteractionResource create(InteractionResource interactionResource) throws SQLException {
        return null;
    }

    public boolean update(InteractionResource interactionResource) throws SQLException {
        return false;
    }

    public InteractionResource getById(String id) throws SQLException {
        return null;
    }

    public List<InteractionResource> getAll() throws SQLException {
        logger.debug("Retrieving all InteractionResources");

        List<InteractionResource> ret = new ArrayList<>();

        String query = "SELECT " + ALL_COLUMNS_SEL +
                        " FROM " + TABLE;

        Connection conn = database.getConnection();

        try {
            PreparedStatement pstm = conn.prepareStatement(query);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                InteractionResource interactionResource = buildInteractionResource(rs);
                ret.add(interactionResource);
            }

        } finally {
            //conn.close();
        }

        return ret;
    }

    public boolean delete(String id) throws SQLException {
        return false;
    }

    private InteractionResource buildInteractionResource(ResultSet rs) throws SQLException {
        InteractionResource ret = new InteractionResource();
        ret.setId(rs.getLong("ID"));
        ret.setName(rs.getString("NAME"));
        ret.setUrl(rs.getString("URL"));

        return ret;
    }
}
