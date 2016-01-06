package org.reactome.server.tool.interactors.dao;

import org.junit.Before;
import org.junit.Test;
import org.reactome.server.tools.interactors.dao.DAOFactory;
import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.model.InteractionResource;
import org.reactome.server.tools.interactors.model.Interactor;
import org.reactome.server.tools.interactors.model.InteractorResource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestInteractorImpl {

    private InteractorDAO interactorDAO;

    @Before
    public void setUp() {
        interactorDAO = DAOFactory.createInteractorDAO();
    }
//
//
//    @Test
//    public void testInteractorDAO() {
//        try {
//            List<String> proteinOrChemicalList = new ArrayList<String>();
//            proteinOrChemicalList.add("Q13501");
//
//            InteractorResource interactorResource = new InteractorResource();
//            interactorResource.setId(1000L);
//            interactorResource.setName("UniProt");
//            interactorResource.setUrl("http://www.uniprot.org/query/##ID##");
//
//            InteractionResource interactionResource = new InteractionResource();
//            interactionResource.setId(2000L);
//            interactionResource.setName("IntAct");
//            interactionResource.setUrl("http://www.ebi.ac.uk/intact/interaction/##ID##");
//
//            for (String proteinOrChemical : proteinOrChemicalList) {
//                Interactor interactor = new Interactor();
//                interactor.setInteractorResourceId(1L);
//                interactor.setAcc(proteinOrChemical);
//
//                interactor = interactorDAO.create(interactor);
//
//                System.out.println(interactor.getId());
//            }
//
//
//
//        } catch (Throwable e) {
//            fail(e.getMessage());
//        }
//
//    }

    @Test
    public void testSimpleQuery() throws SQLException {
        System.out.println(interactorDAO.getByAccession("Q13501"));
    }

}
