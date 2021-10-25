package org.reactome.server.tool.interactors.dao;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactome.server.interactors.exception.CustomPsicquicInteractionClusterException;
import org.reactome.server.interactors.exception.PsicquicQueryException;
import org.reactome.server.interactors.exception.PsicquicResourceNotFoundException;
import org.reactome.server.interactors.model.Interaction;
import org.reactome.server.interactors.model.InteractionDetails;
import org.reactome.server.interactors.service.PsicquicService;
import org.reactome.server.interactors.util.Toolbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import psidev.psi.mi.tab.PsimiTabException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * For mor PSICQUIC examples: https://github.com/EBI-IntAct/micluster/blob/master/micluster-score/src/test/java/TestInteractionClusterScore.java
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class TestPsicquicClustering {

    private Logger logger = LoggerFactory.getLogger(TestPsicquicClustering.class);

    private PsicquicService psicquicService;

    @BeforeEach
    public void setup() {
        psicquicService = new PsicquicService();
    }

    @Test
    public void testPsicquicResource() {
        List<String> psicquicResources = getPsicquicResource();

        if (psicquicResources != null) {
            Assertions.assertTrue(getPsicquicResource().size() > 0, "No resources have been returned from PSICQUIC");
        } else {
            // Psicquic is down, but we don't want to break our tests because of it.
            logger.warn("Couldn't get PSICQUIC Resources. Reason: PSICQUIC is down.");
        }
    }

    @Test
    public void testSpecificPsicquicResource() {
        String resourceName = "UniProt";

        try {
            //long start = System.currentTimeMillis();
            Map<String, List<Interaction>> interactions = psicquicService.getInteractions(resourceName, getSampleAccessions(resourceName));
            //long elapsedTime = System.currentTimeMillis() - start;

            Assertions.assertTrue(interactions.size() >= 1, "No interactors present in " + resourceName + " database.");

        } catch (PsicquicQueryException | PsimiTabException | PsicquicRegistryClientException | PsicquicResourceNotFoundException e) {
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

            Assertions.assertTrue(interactions.size() >= 1, "No interactors present in " + resourceName + " database.");

        } catch (PsicquicQueryException | PsimiTabException | PsicquicRegistryClientException | PsicquicResourceNotFoundException  e) {
            // Psicquic is down, but we don't want to break our tests because of it.
            logger.warn("Could perform PSICQUIC Query. Reason: PSICQUIC is down");
        }
    }

    //@Test
    @SuppressWarnings("unused")
    public void testAllPsicquicResources() {
        try {
            PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();
            List<ServiceType> services = registryClient.listActiveServices();
            List<String> resources = new ArrayList<>(services.size());
            resources.addAll(services.stream().filter(ServiceType::isActive).map(ServiceType::getName).collect(Collectors.toList()));

            for (String res : resources) {
                System.out.println("Testing " + res);
                try {
                    Map<String, List<Interaction>> interactionMaps = psicquicService.getInteractions(res, getSampleAccessions(res));
                    for (String accKey : interactionMaps.keySet()) {
                        System.out.println("Accession: " + accKey + " => " + interactionMaps.get(accKey).size());

                        List<Interaction> interactions = interactionMaps.get(accKey);
                        // Remove from output if there is no interaction
                        if (interactions.size() == 0) {
                            continue;
                        }

                        for (Interaction interaction : interactions) {
                            Toolbox.getAccessionURL(interaction.getInteractorB().getAcc(), res);

                            // This list holds evidences that we are going to use to build the evidences URL.
                            List<String> evidencesWithDbNames = new ArrayList<>();

                            // Set Evidences as the others Interactions identifiers
                            if (interaction.getInteractionDetailsList() != null) {
                                for (InteractionDetails interactionDetail : interaction.getInteractionDetailsList()) {
                                    String evidence = interactionDetail.getInteractionAc();
                                    evidencesWithDbNames.add(evidence);
                                }
                            }
                            Toolbox.getEvidencesURL(evidencesWithDbNames, res);
                        }
                    }
                } catch (PsicquicQueryException | PsimiTabException | PsicquicRegistryClientException | PsicquicResourceNotFoundException  e) {
                    logger.error("Could perform PSICQUIC Query.", e);
                }
            }
        } catch (PsicquicRegistryClientException e) {
            // Psicquic is down, but we don't want to break our tests because of it.
            logger.warn("Error getting all resources");
        }
    }

    private List<String> getPsicquicResource() {
        try {
            PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();

            List<ServiceType> services = registryClient.listActiveServices();

            List<String> resources = new ArrayList<>(services.size());
            resources.addAll(services.stream().map(ServiceType::getName).collect(Collectors.toList()));

            return resources;
        } catch (PsicquicRegistryClientException e) {
            return null;
        }
    }

    /**
     * How to test a Custom Psicquic Resource
     * 1. Comment out @Test
     * 2. Set the customUrl and the accession
     * 3. Check instructions down in the code in order to debug properly.
     *
     * @throws CustomPsicquicInteractionClusterException
     */
    @Test
    public void testCustomPsicquicResource() throws CustomPsicquicInteractionClusterException {
        String customUrl = "http://psicquic.docking.org/psicquic/webservices/current/search/query/";
        //http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/query
        String accession = "P00533";

        try {
            /*
             * 3.1 If the interactors list is empty, you may want to check the clustering and the scores.
             * To do so, go to {@link org.reactome.server.interactors.psicquic.impl.InteractionClusterImpl.getInteractionFromCustomPsicquic()}
             * and check the interactionMapping.
             */
            psicquicService.getInteractionFromCustomPsicquic(customUrl, Collections.singletonList(accession));

        } catch (CustomPsicquicInteractionClusterException e) {
            e.printStackTrace();
        }
    }

    private Collection<String> getSampleAccessions(String resource) {
        Collection<String> accessions = new ArrayList<>(1);
        switch (resource) {
            case "APID Interactomes":
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
                accessions.add("K7Y1A2");
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
                accessions.add("P00533"); // does not have score, can't find any higher than 0.45.
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
                accessions.add("Q15262");
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
                accessions.add("Q8WV24");
                break;
            case "VirHostNet":
                accessions.add("Q01844"); // does not have score, can't find any higher than 0.45.
                break;
            case "ZINC":
                accessions.add("Q99720");
                break;
        }
        return accessions;
    }
}
