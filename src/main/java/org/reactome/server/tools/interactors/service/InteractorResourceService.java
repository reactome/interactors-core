package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.dao.InteractorResourceDAO;
import org.reactome.server.tools.interactors.dao.impl.JDBCInteractorImpl;
import org.reactome.server.tools.interactors.dao.impl.JDBCInteractorResourceImpl;
import org.reactome.server.tools.interactors.database.InteractorsDatabase;
import org.reactome.server.tools.interactors.model.InteractorResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractorResourceService {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorImpl.class);

    private InteractorResourceDAO interactorResourceDao;

    public InteractorResourceService(InteractorsDatabase database){
        interactorResourceDao = new JDBCInteractorResourceImpl(database);
    }

    /**
     * Retrieves all Interactor Resources from database
     * @return
     * @throws SQLException
     */
    public List<InteractorResource> getAll() throws SQLException {
        return interactorResourceDao.getAll();
    }

    /**
     * Retrieve all interactor resource into a Map data structure having
     * the unique db id as the key
     *
     * @return
     * @throws SQLException
     */
    public Map<Long, InteractorResource> getAllMappedById() throws SQLException {
        Map<Long, InteractorResource> interactorResourceMap = new HashMap<>();
        List<InteractorResource> interactorResourceList = interactorResourceDao.getAll();
        for (InteractorResource interactorResource : interactorResourceList) {
            interactorResourceMap.put(interactorResource.getId(), interactorResource);
        }

        return interactorResourceMap;
    }

    /**
     * Retrieve all interactor resource into a Map data structure having
     * the name as the key
     * @return
     * @throws SQLException
     */
    public Map<String, InteractorResource> getAllMappedByName() throws SQLException {
        Map<String, InteractorResource> interactorResourceMap = new HashMap<>();
        List<InteractorResource> interactorResourceList = interactorResourceDao.getAll();
        for (InteractorResource interactorResource : interactorResourceList) {
            interactorResourceMap.put(interactorResource.getName(), interactorResource);
        }

        return interactorResourceMap;
    }
    
}
