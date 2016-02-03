package org.reactome.server.tools.interactors.dao.intact;

import org.reactome.server.tools.interactors.dao.InteractionResourceDAO;
import org.reactome.server.tools.interactors.database.InteractorsDatabase;
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

public class StaticInteractionResource implements InteractionResourceDAO {

    final Logger logger = LoggerFactory.getLogger(StaticInteractor.class);

    private Connection connection;

    private final String TABLE = "INTERACTION_RESOURCE";
    private final String ALL_COLUMNS = "NAME, URL";
    private final String ALL_COLUMNS_SEL = "ID, ".concat(ALL_COLUMNS);

    public StaticInteractionResource(InteractorsDatabase database) {
        this.connection = database.getConnection();
    }

    public List<InteractionResource> getAll() throws SQLException {
        logger.debug("Retrieving all InteractionResources");

        List<InteractionResource> ret = new ArrayList<>();

        String query = "SELECT " + ALL_COLUMNS_SEL +
                        " FROM " + TABLE;

        PreparedStatement pstm = connection.prepareStatement(query);
        ResultSet rs = pstm.executeQuery();
        while (rs.next()) {
            InteractionResource interactionResource = buildInteractionResource(rs);
            ret.add(interactionResource);
        }

        return ret;
    }

    public InteractionResource getByName(String name) throws SQLException {
        logger.debug("Retrieving InteractionResource by name");

        InteractionResource interactionResource = null;

        String query = "SELECT " + ALL_COLUMNS_SEL +
                        " FROM " + TABLE +
                        " WHERE LOWER(name) = ?";

        PreparedStatement pstm = connection.prepareStatement(query);
        pstm.setString(1,name.toLowerCase());

        ResultSet rs = pstm.executeQuery();
        if (rs.next()) {
            interactionResource = buildInteractionResource(rs);
        }

        return interactionResource;
    }

    private InteractionResource buildInteractionResource(ResultSet rs) throws SQLException {
        InteractionResource ret = new InteractionResource();
        ret.setId(rs.getLong("ID"));
        ret.setName(rs.getString("NAME"));
        ret.setUrl(rs.getString("URL"));

        return ret;
    }
}
