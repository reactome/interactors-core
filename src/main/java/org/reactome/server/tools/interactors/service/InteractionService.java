package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.dao.DAOFactory;
import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.Interactor;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractionService {

    public InteractionDAO interactionDAO = DAOFactory.createInterationDAO();
    public InteractorDAO interactorDAO = DAOFactory.createInteractorDAO();

    public void save(List<Interaction> interactionList) throws SQLException {

        for (Interaction interaction : interactionList) {

            Interactor interactorA = interaction.getInteractorA();
            interactorDAO.create(interactorA);

            Interactor interactorB = interaction.getInteractorB();
            interactorDAO.create(interactorB);


//            interaction.setInteractorA(interactorA);
//            interaction.setInteractorB(interactorB);






        }

    }

}
