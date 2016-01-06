package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.dao.impl.JDBCInteractorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractionService {

    final Logger logger = LoggerFactory.getLogger(JDBCInteractorImpl.class);

    private static InteractionService interactionService = null;

    public static InteractionService getInstance() {
        if (interactionService == null) {
            interactionService = new InteractionService();
        }

        return interactionService;
    }

    private InteractionService(){

    }

}
