package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.dao.InteractionResourceDAO;
import org.reactome.server.tools.interactors.dao.intact.StaticInteractionResource;
import org.reactome.server.tools.interactors.dao.intact.StaticInteractor;
import org.reactome.server.tools.interactors.database.InteractorsDatabase;
import org.reactome.server.tools.interactors.model.InteractionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractionResourceService {

    final Logger logger = LoggerFactory.getLogger(StaticInteractor.class);

    public InteractionResourceDAO interactionResourceDAO;

    public InteractionResourceService(InteractorsDatabase database){
        interactionResourceDAO = new StaticInteractionResource(database);
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

    /**
     * Retrieve all interactor resource into a Map data structure having
     * the unique db id as the key
     *
     * @return
     * @throws SQLException
     */
    public Map<Long, InteractionResource> getAllMappedById() throws SQLException {
        Map<Long, InteractionResource> interactionResourceMap = new HashMap<>();
        List<InteractionResource> interactionResourceList = interactionResourceDAO.getAll();
        for (InteractionResource interactionResource : interactionResourceList) {
            interactionResourceMap.put(interactionResource.getId(), interactionResource);
        }

        return interactionResourceMap;
    }

    /**
     * Retrieve all interactor resource into a Map data structure having
     * the name as the key
     * @return
     * @throws SQLException
     */
    public Map<String, InteractionResource> getAllMappedByName() throws SQLException {
        Map<String, InteractionResource> interactionResourceMap = new HashMap<>();
        List<InteractionResource> interactionResourceList = interactionResourceDAO.getAll();
        for (InteractionResource interactionResource : interactionResourceList) {
            interactionResourceMap.put(interactionResource.getName(), interactionResource);
        }

        return interactionResourceMap;
    }
}
