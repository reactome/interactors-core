package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.dao.*;
import org.reactome.server.tools.interactors.exception.InvalidInteractionResourceException;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.InteractionResource;
import org.reactome.server.tools.interactors.model.Interactor;

import java.sql.SQLException;
import java.util.*;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractorService {

    //final Logger logger = LoggerFactory.getLogger(InteractionService.class);

    private static InteractorService interactorService = null;

    private InteractorDAO interactorDAO = DAOFactory.createInteractorDAO();

    public static InteractorService getInstance() {
        if (interactorService == null) {
            interactorService = new InteractorService();
        }

        return interactorService;
    }

    private InteractorService(){

    }

    public List<Interactor> getAll() throws SQLException {
        return interactorDAO.getAll();
    }

    public List<String> getAllAccessions() throws SQLException {
        return interactorDAO.getAllAccessions();
    }


}
