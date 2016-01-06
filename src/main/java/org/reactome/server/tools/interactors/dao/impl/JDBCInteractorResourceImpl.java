package org.reactome.server.tools.interactors.dao.impl;

import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.dao.InteractorResourceDAO;
import org.reactome.server.tools.interactors.database.SQLiteConnection;
import org.reactome.server.tools.interactors.model.InteractionResource;
import org.reactome.server.tools.interactors.model.Interactor;
import org.reactome.server.tools.interactors.model.InteractorResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class JDBCInteractorResourceImpl implements InteractorResourceDAO {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorResourceImpl.class);

    private SQLiteConnection database = SQLiteConnection.getInstance();

    private String TABLE = "INTERACTOR_RESOURCE";
    private String ALL_COLUMNS = "NAME, URL";
    private String ALL_COLUMNS_SEL = "ID, ".concat(ALL_COLUMNS);

    public JDBCInteractorResourceImpl() {

    }

    public InteractorResource create(InteractorResource interactorResource) throws SQLException {
        return null;
    }

    public boolean update(InteractorResource interactorResource) throws SQLException {
        return false;
    }

    public InteractorResource getById(String id) throws SQLException {
        return null;
    }

    public boolean delete(String id) throws SQLException {
        return false;
    }

    public List<InteractorResource> getAll() throws SQLException {
        logger.debug("Retrieving all InteractorResources");

        List<InteractorResource> ret = new ArrayList<>();

        String query = "SELECT " + ALL_COLUMNS_SEL +
                        " FROM " + TABLE;

        Connection conn = database.getConnection();

        try {
            PreparedStatement pstm = conn.prepareStatement(query);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                InteractorResource interactorResource = buildInteractorResource(rs);
                ret.add(interactorResource);
            }

        } finally {
            //conn.close();
        }

        return ret;
    }

    private InteractorResource buildInteractorResource(ResultSet rs) throws SQLException {
        InteractorResource ret = new InteractorResource();
        ret.setId(rs.getLong("ID"));
        ret.setName(rs.getString("NAME"));
        ret.setUrl(rs.getString("URL"));

        return ret;
    }
}
