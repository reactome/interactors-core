package org.reactome.server.tool.interactors.dao;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.junit.Before;
import org.junit.Test;
import org.reactome.server.tools.interactors.exception.PsicquicInteractionClusterException;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.service.PsicquicService;
import psidev.psi.mi.tab.PsimiTabException;
import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;
import uk.ac.ebi.enfin.mi.cluster.Encore2Binary;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteraction;
import uk.ac.ebi.enfin.mi.cluster.score.InteractionClusterScore;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestPsicquicClustering {

    PsicquicService psicquicService;

    @Before
    public void setup(){
        psicquicService = new PsicquicService();
    }

    //@Test
    public void testAllPsicquicServices(){
        try {

            /* Get binaryInteractions from PSI-MI files */
//            URL intactQuery = new URL("http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/query/P07200");
//            URL intactQuery = new URL("http://webservice.baderlab.org:8480/psicquic-ws/webservices/current/search/query/16019");
//            URL irefindexQuery = new URL("http://biotin.uio.no:8080/psicquic-ws/webservices/current/search/query/P07200");
//            URL intactQuery = new URL("http://catalyst.ucsd.edu:8080/psicquic-ws/webservices/current/search/query/50002196");
//            URL intactQuery = new URL("http://webservice.baderlab.org:8380/psi-gm/webservices/current/search/interactor/Q9NQ94");
//            URL intactQuery = new URL("http://bar.utoronto.ca:9090/psicquic/webservices/current/search/interactor/At5g15200?compressed=true");
//            URL intactQuery = new URL("http://www.ebi.ac.uk/Tools/webservices/psicquic/uniprot/webservices/current/search/interactor/P04626?compressed=true");
//            URL intactQuery = new URL("http://imex.mbi.ucla.edu/psicquic-ws/webservices/current/search/interactor/P35439");
//            URL intactQuery = new URL("http://www.ebi.ac.uk/Tools/webservices/psicquic/molcon/webservices/current/search/interactor/P22694-2");
//            URL intactQuery = new URL("http://bar.utoronto.ca:9090/psicquic/webservices/current/search/interactor/At2g36990");

            for (String resourceName : getPsicquicResource()) {

                Map<String, List<Interaction>> interactions =  psicquicService.getInteractions(resourceName, getSampleAccessions(resourceName));

            }


        } catch (PsicquicInteractionClusterException | PsicquicRegistryClientException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSpecificPsicquicResource(){
        String resourceName = "mentha";

        try {
            long start = System.currentTimeMillis();
            Map<String, List<Interaction>> interactions =  psicquicService.getInteractions(resourceName, getSampleAccessions(resourceName));
            long elapsedTime = System.currentTimeMillis() - start;

            System.out.println();
//            System.out.println(interactions.get("161511").get(0).getInteractorA().getAlias());
        } catch (PsicquicInteractionClusterException e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void testRegex(){
        String text = "Kinase phosph";

        Pattern p = Pattern.compile("^([a-zA-Z0-9\\s:-_]{2,15})");
        if(p.matcher(text).matches()){

        }
    }

    private List<String> getPsicquicResource() throws PsicquicRegistryClientException {
        PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();

        List<ServiceType> services = registryClient.listActiveServices();

        List<String> resources = new ArrayList<>(services.size());
        for (ServiceType service : services) {
            resources.add(service.getName());
        }

        return resources;
    }

    private Collection<String> getSampleAccessions(String resource){
        Collection<String> accessions = new ArrayList<>(1);

        switch (resource){
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

            case "HPIDb":
                accessions.add("");
                break;

            case "InnateDB":
                accessions.add("P30679");
                break;

            case "InnateDB-All":
                accessions.add("P30679");
                break;

            case "IntAct":
                accessions.add("");
                break;

            case "mentha":
                //accessions.add("Q9NWZ3");
                accessions.add("P39060");
                break;

            case "MPIDB":
                accessions.add("");
                break;

            case "iRefIndex":
                accessions.add("");
                break;

            case "MatrixDB":
                accessions.add("");
                break;

            case "MINT":
                accessions.add("O60231");
                break;

            case "Reactome":
                accessions.add("");
                break;

            case "Reactome-FIs":
                accessions.add("");
                break;

            case "STRING":
                accessions.add("");
                break;

            case "BIND":
                accessions.add("16761");
                accessions.add("P04275");
                accessions.add("P07948");
                accessions.add("Q9HCN6");
                accessions.add("P06241");
                break;

            case "Interoporc":
                accessions.add("");
                break;

            case "DrugBank":
                accessions.add(""); // DOWN
                break;

            case "I2D":
                accessions.add("");
                break;

            case "I2D-IMEx":
                accessions.add("");
                break;

            case "InnateDB-IMEx":
                accessions.add("");
                break;

            case "MolCon":
                accessions.add("");
                break;

            case "UniProt":
                accessions.add("");
                break;

            case "MBInfo":
                accessions.add("");
                break;

            case "BindingDB":
                accessions.add("O43353");
                break;

            case "VirHostNet":
                accessions.add("");
                break;

            case "TopFind":
                accessions.add("");
                break;

            case "Spike":
                accessions.add("");
                break;

            case "GeneMANIA":
                accessions.add("Q13501");
                break;

            case "EBI-GOA-nonIntAct":
                accessions.add("Q13485");
                accessions.add("P84022");
                break;

        }

        return accessions;

    }


    /**
     * @link https://github.com/EBI-IntAct/micluster/blob/master/micluster-score/src/test/java/TestInteractionClusterScore.java
     */
    //@Test
    public void testInteractionClusterScoreWithBinaryInteractions(){
        try {
            /* Get binaryInteractions from PSI-MI files */
            URL intactQuery = new URL("http://tyersrest.tyerslab.com:8805/psicquic/webservices/current/search/query/560144");
            List<BinaryInteraction> binaryInteractions = new ArrayList<>();
            PsimiTabReader mitabReader = new PsimiTabReader();
            binaryInteractions.addAll(mitabReader.read(intactQuery));

            /* Run cluster using list of binary interactions as input */
            InteractionClusterScore iC = new InteractionClusterScore();
            iC.setBinaryInteractionIterator(binaryInteractions.iterator());
            iC.setMappingIdDbNames("uniprotkb,irefindex,ddbj/embl/genbank,refseq,chebi,chembl,entrez gene/locuslink,unknown");

            iC.runService();

            /* Retrieve results */
            Map<Integer, EncoreInteraction> interactionMapping = iC.getInteractionMapping();
            Map<String, List<Integer>> interactorMapping = iC.getInteractorMapping();
            Map<String, String> synonymMapping = iC.getSynonymMapping();
            int interactionMappingId = iC.getInteractionMappingId();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (PsimiTabException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    //@Test
    public void testEncore2Binary(){
        PsimiTabReader mitabReader = new PsimiTabReader();
        /* Run cluster using list of binary interactions as input */
        InteractionClusterScore iC = new InteractionClusterScore();
        /* Add interactions from Psicquic */
        iC.addQueryAcc("560144");
        iC.addQuerySource("biogrid");
//        iC.addQuerySource("intact");
        /* Set priority for molecule accession mapping (Find database names in MI Ontology) */
        iC.setMappingIdDbNames("uniprotkb,irefindex,ddbj/embl/genbank,refseq,chebi");
        /* Run clustering service */
        iC.runService();

        /* Retrieve results */
        Map<Integer, EncoreInteraction> interactionMapping = iC.getInteractionMapping();

        /* Get PSI binary Interactions */
        Map<Integer, BinaryInteraction> binaryInteractionMapping = new HashMap<>();
        Encore2Binary iConverter = new Encore2Binary(iC.getMappingIdDbNames());
        for(int mappingId:interactionMapping.keySet()){
            EncoreInteraction eI = interactionMapping.get(mappingId);
            BinaryInteraction bI = iConverter.getBinaryInteractionForScoring(eI);
            binaryInteractionMapping.put(mappingId,bI);
        }

        System.out.println();
        /* Test */
//        assertTrue(binaryInteractionMapping.size() > 0);
    }
}
