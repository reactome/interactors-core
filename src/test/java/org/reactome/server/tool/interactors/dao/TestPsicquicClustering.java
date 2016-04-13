package org.reactome.server.tool.interactors.dao;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reactome.server.tools.interactors.exception.PsicquicQueryException;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.service.PsicquicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import psidev.psi.mi.tab.PsimiTabException;

import java.util.*;

/**
 * For mor PSICQUIC examples: https://github.com/EBI-IntAct/micluster/blob/master/micluster-score/src/test/java/TestInteractionClusterScore.java
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class TestPsicquicClustering {

    Logger logger = LoggerFactory.getLogger(TestPsicquicClustering.class);

    PsicquicService psicquicService;


    @Before
    public void setup() {
        psicquicService = new PsicquicService();
    }

    @Test
    public void testPsicquicResource() {
        List<String> psicquicResources = getPsicquicResource();

        if (psicquicResources != null) {
            Assert.assertTrue("No resources have been returned from PSICQUIC", getPsicquicResource().size() > 0);
        } else {
            // Psicquic is down, but we don't want to break our tests because of it.
            logger.warn("Couldn't get PSICQUIC Resources. Reason: PSICQUIC is down.");
        }

    }

    @Test
    public void testSpecificPsicquicResource() {
        String resourceName = "Reactome-FIs";

        try {
            //long start = System.currentTimeMillis();
            Map<String, List<Interaction>> interactions = psicquicService.getInteractions(resourceName, getSampleAccessions(resourceName));
            //long elapsedTime = System.currentTimeMillis() - start;

            Assert.assertTrue("No interactors present in " + resourceName + " database.", interactions.size() >= 1);

        } catch (PsicquicQueryException | PsimiTabException | PsicquicRegistryClientException e) {
            // Psicquic is down, but we don't want to break our tests because of it.
            logger.warn("Couldn't perform PSICQUIC Query. Reason: PSICQUIC is down");
        }
    }

    @Test
    public void testSpecificPsicquicResourceAndAccession() {
        String resourceName = "Reactome-FIs";
        String accession = "Q02750";

        try {
            Map<String, List<Interaction>> interactions = psicquicService.getInteractions(resourceName, Collections.singleton(accession));

            Assert.assertTrue("No interactors present in " + resourceName + " database.", interactions.size() >= 1);

        } catch (PsicquicQueryException | PsimiTabException | PsicquicRegistryClientException e) {
            // Psicquic is down, but we don't want to break our tests because of it.
            logger.warn("Could perform PSICQUIC Query. Reason: PSICQUIC is down");
        }
    }

    private List<String> getPsicquicResource() {
        try {
            PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();

            List<ServiceType> services = registryClient.listActiveServices();

            List<String> resources = new ArrayList<>(services.size());
            for (ServiceType service : services) {
                resources.add(service.getName());
            }

            return resources;
        } catch (PsicquicRegistryClientException e) {
            return null;
        }
    }

    private Collection<String> getSampleAccessions(String resource) {
        Collection<String> accessions = new ArrayList<>(1);

        switch (resource) {
            case "APID":
                accessions.add("P00533");
                break;

            case "BAR":
                accessions.add("Q9LFH5");
                accessions.add("Q7PC79");
                break;

            case "BioGrid":
                accessions.add("29101");
                break;

            case "BIND":
                accessions.add("16761");
                break;

            case "BindingDB":
                accessions.add("O43353");
                break;

            case "bhf-ucl":
                accessions.add("P35222");
                break;

            case "ChEMBL":
                accessions.add("Q02750");
                accessions.add("161511");
                break;

            case "DIP":
                accessions.add("DIP-232N");
                accessions.add("Q8IHE3");
                break;

            case "DrugBank":
                accessions.add(""); // DOWN
                break;

            case "EBI-GOA-nonIntAct":
                accessions.add("Q13485");
                accessions.add("P84022");
                break;

            case "GeneMANIA":
                accessions.add("Q13501"); // This is impossible (better if we remove it ... we should report this error in the score
                break;

            case "HPIDb":
                accessions.add("Q32PH0");
                break;

            case "InnateDB":
                accessions.add("Q9BXM7");
                break;

            case "InnateDB-All":
                accessions.add("Q9BXM7");
                break;

            case "IntAct":
                accessions.add("Q13501");
                break;

            case "iRefIndex":
                accessions.add(""); // DOWN
                break;

            case "Interoporc":
                accessions.add(""); // does not have score, can't find any higher than 0.45. Very old DB.
                break;

            case "I2D":
                accessions.add(""); // does not have score, can't find any higher than 0.45.
                break;

            case "I2D-IMEx":
                accessions.add("P12830");
                break;

            case "InnateDB-IMEx":
                accessions.add("Q8N7N6");
                break;

            case "MatrixDB":
                accessions.add("P13213");
                break;

            case "mentha":
                //accessions.add("Q9NWZ3");
                accessions.add("Q13501");
                break;

            case "MPIDB":
                accessions.add("Q70M91");
                break;

            case "MBInfo":
                accessions.add("P07228");
                break;

            case "MINT":
                accessions.add("O60231");
                break;

            case "MolCon":
                accessions.add("Q92917");
                break;

            case "Reactome":
                accessions.add("Q99661"); // does not have score, can't find any higher than 0.45.
                break;

            case "Reactome-FIs":
                accessions.add("O75534");
                break;

            case "STRING":
                accessions.add(""); // DOWN
                break;

            case "Spike":
                accessions.add("P05412");
                break;

            case "TopFind":
                accessions.add(""); // DOWN
                break;

            case "UniProt":
                accessions.add("");
                break;

            case "VirHostNet":
                accessions.add(""); // does not have score, can't find any higher than 0.45.
                break;

        }

        return accessions;

    }

}
