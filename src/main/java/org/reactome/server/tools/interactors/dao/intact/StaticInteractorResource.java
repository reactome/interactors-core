package org.reactome.server.tools.interactors.dao.intact;

import org.reactome.server.tools.interactors.dao.InteractorResourceDAO;
import org.reactome.server.tools.interactors.database.InteractorsDatabase;
import org.reactome.server.tools.interactors.model.InteractorResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class StaticInteractorResource implements InteractorResourceDAO {

    final Logger logger = LoggerFactory.getLogger(StaticInteractorResource.class);

    private Connection connection;

    private String TABLE = "INTERACTOR_RESOURCE";
    private String ALL_COLUMNS = "NAME, URL";
    private String ALL_COLUMNS_SEL = "ID, ".concat(ALL_COLUMNS);

    public StaticInteractorResource(InteractorsDatabase database) {
        this.connection = database.getConnection();
    }

    public List<InteractorResource> getAll() throws SQLException {
        logger.debug("Retrieving all InteractorResources");

        List<InteractorResource> ret = new ArrayList<>();

        String query = "SELECT " + ALL_COLUMNS_SEL +
                        " FROM " + TABLE;

        PreparedStatement pstm = connection.prepareStatement(query);
        ResultSet rs = pstm.executeQuery();
        while (rs.next()) {
            InteractorResource interactorResource = buildInteractorResource(rs);
            ret.add(interactorResource);
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
