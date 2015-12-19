package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.dao.DAOFactory;
import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.dao.InteractionDetailsDAO;
import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.dao.impl.JDBCInteractorImpl;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.InteractionDetails;
import org.reactome.server.tools.interactors.model.Interactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

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

    public InteractionDAO interactionDAO = DAOFactory.createInterationDAO();
    public InteractorDAO interactorDAO = DAOFactory.createInteractorDAO();
    public InteractionDetailsDAO interactionDetailsDAO = DAOFactory.createInterationDetailsDAO();

    public void save(List<Interaction> interactionList) throws SQLException {
        logger.debug("Saving Interactions");

        for (Interaction interaction : interactionList) {
            save(interaction);
        }

    }

    public void save(Interaction interaction) throws SQLException {
        logger.debug("Saving Interactions");

        logger.debug("Obtaining InteractorA...");
        Interactor interactorA = interaction.getInteractorA();

        logger.debug("Searching InteractorA in the Database");
        Interactor dbInteractorA = interactorDAO.getByAccession(interactorA.getAcc());
        if(dbInteractorA == null){
            logger.debug("InteractorA does not exist in the Database. Persist a new one [" + interactorA.getAcc() + "]");
            interactorDAO.create(interactorA);
        }else {
            logger.debug("InteractorA exists in the Database and its ID is [" + dbInteractorA.getId() + "];");
            interactorA.setId(dbInteractorA.getId());
        }

        Interactor interactorB = interaction.getInteractorB();

        Interactor dbInteractorB = interactorDAO.getByAccession(interactorB.getAcc());
        if(dbInteractorB == null){
            logger.debug("InteractorB does not exist in the Database. Persist a new one [" + interactorB.getAcc() + "]");
            interactorDAO.create(interactorB);
        }else {
            logger.debug("InteractorB exists in the Database and its ID is [" + dbInteractorB.getId() + "];");
            interactorB.setId(dbInteractorB.getId());
        }

        //interaction.setInteractorA(interactorA);
        //interaction.setInteractorB(interactorB);

        interactionDAO.create(interaction);

        List<InteractionDetails> interactionDetailsList = interaction.getInteractionDetailsList();
        for (InteractionDetails interactionDetails : interactionDetailsList) {
            interactionDetails.setInteractionId(interaction.getId());

            interactionDetailsDAO.create(interactionDetails);
        }



    }

}
