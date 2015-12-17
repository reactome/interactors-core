package org.reactome.server.tool.interactors.dao;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.wsclient.PsicquicClientException;
import org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient;
import org.hupo.psi.mi.psicquic.wsclient.result.MitabSearchResult;
import org.junit.Before;
import org.junit.Test;
import org.reactome.server.tools.interactors.dao.DAOFactory;
import org.reactome.server.tools.interactors.dao.InteractorDAO;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.InteractionResource;
import org.reactome.server.tools.interactors.model.Interactor;
import org.reactome.server.tools.interactors.model.InteractorResource;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.Confidence;
import psidev.psi.mi.tab.model.CrossReference;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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


    @Test
    public void testInteractorDAO() {
        try {
            List<String> proteinOrChemicalList = new ArrayList<String>();
            proteinOrChemicalList.add("Q13501");

            InteractorResource interactorResource = new InteractorResource();
            interactorResource.setId(1000L);
            interactorResource.setName("UniProt");
            interactorResource.setUrl("http://www.uniprot.org/query/##ID##");

            InteractionResource interactionResource = new InteractionResource();
            interactionResource.setId(2000L);
            interactionResource.setName("IntAct");
            interactionResource.setUrl("http://www.ebi.ac.uk/intact/interaction/##ID##");

            for (String proteinOrChemical : proteinOrChemicalList) {
                Interactor interactor = new Interactor();
                interactor.setInteractorResourceId(1L);
                interactor.setAcc(proteinOrChemical);

                interactor = interactorDAO.create(interactor);

                System.out.println(interactor.getId());
            }



        } catch (Throwable e) {
            fail(e.getMessage());
        }

    }

}
