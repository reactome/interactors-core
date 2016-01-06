package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.dao.DAOFactory;
import org.reactome.server.tools.interactors.dao.InteractorResourceDAO;
import org.reactome.server.tools.interactors.dao.impl.JDBCInteractorImpl;
import org.reactome.server.tools.interactors.model.InteractorResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractorResourceService {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorImpl.class);

    private static InteractorResourceService interactionResourceService = null;

    private InteractorResourceDAO interactorResourceDao = DAOFactory.createInteratorResourceDAO();

    public static InteractorResourceService getInstance() {
        if (interactionResourceService == null) {
            interactionResourceService = new InteractorResourceService();
        }

        return interactionResourceService;
    }

    private InteractorResourceService(){

    }

    /**
     * Retrieves all Interactor Resources from database
     * @return
     * @throws SQLException
     */
    public List<InteractorResource> getAll() throws SQLException {
        return interactorResourceDao.getAll();
    }

}
