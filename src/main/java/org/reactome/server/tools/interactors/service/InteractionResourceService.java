package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.dao.DAOFactory;
import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.dao.InteractionResourceDAO;
import org.reactome.server.tools.interactors.dao.impl.JDBCInteractorImpl;
import org.reactome.server.tools.interactors.model.InteractionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractionResourceService {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorImpl.class);

    private static InteractionResourceService interactionResourceService = null;

    public InteractionResourceDAO interactionResourceDAO = DAOFactory.createInterationResourceDAO();

    public static InteractionResourceService getInstance() {
        if (interactionResourceService == null) {
            interactionResourceService = new InteractionResourceService();
        }

        return interactionResourceService;
    }

    private InteractionResourceService(){

    }

    /**
     * Retrieves a list of all interaction Resources
     * @return
     * @throws SQLException
     */
    public List<InteractionResource> getAll() throws SQLException {
        return interactionResourceDAO.getAll();
    }

    public InteractionResource getByName(String resource) throws SQLException {
        return interactionResourceDAO.getByName(resource);
    }
}
