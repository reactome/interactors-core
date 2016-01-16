package org.reactome.server.tool.interactors.dao;

import org.junit.Before;
import org.junit.Test;
import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.dao.impl.JDBCInteractorImpl;
import org.reactome.server.tools.interactors.database.InteractorsDatabase;

import java.sql.SQLException;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestInteractorImpl {

    private InteractorDAO interactorDAO;

    @Before
    public void setUp() {
        String file = "/Users/reactome/interactors/interactors.db";
        InteractorsDatabase interactors = null;
        try {
            interactors = new InteractorsDatabase(file);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        interactorDAO = new JDBCInteractorImpl(interactors);
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
