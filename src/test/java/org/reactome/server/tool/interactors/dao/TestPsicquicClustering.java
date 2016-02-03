package org.reactome.server.tool.interactors.dao;

import org.junit.Test;
import psidev.psi.mi.tab.PsimiTabException;
import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteraction;
import uk.ac.ebi.enfin.mi.cluster.score.InteractionClusterScore;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestPsicquicClustering {

    @Test
    public void testInteractionClusterScoreWithBinaryInteractions(){
        try {
            /* Get binaryInteractions from PSI-MI files */
//            URL intactQuery = new URL("http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/query/P07200");
//            URL intactQuery = new URL("http://webservice.baderlab.org:8480/psicquic-ws/webservices/current/search/query/16019");
//            URL irefindexQuery = new URL("http://biotin.uio.no:8080/psicquic-ws/webservices/current/search/query/P07200");
//            URL intactQuery = new URL("http://catalyst.ucsd.edu:8080/psicquic-ws/webservices/current/search/query/50002196");
            //URL intactQuery = new URL("http://webservice.baderlab.org:8380/psi-gm/webservices/current/search/interactor/Q9NQ94");
//            URL intactQuery = new URL("http://bar.utoronto.ca:9090/psicquic/webservices/current/search/interactor/At5g15200?compressed=true");
//            URL intactQuery = new URL("http://www.ebi.ac.uk/Tools/webservices/psicquic/uniprot/webservices/current/search/interactor/P04626?compressed=true");
//            URL intactQuery = new URL("http://imex.mbi.ucla.edu/psicquic-ws/webservices/current/search/interactor/P35439");
//            URL intactQuery = new URL("http://www.ebi.ac.uk/Tools/webservices/psicquic/molcon/webservices/current/search/interactor/P22694-2");
            URL intactQuery = new URL("http://bar.utoronto.ca:9090/psicquic/webservices/current/search/interactor/At2g36990");

            List<BinaryInteraction> binaryInteractions = new ArrayList<>();
            PsimiTabReader mitabReader = new PsimiTabReader();
            binaryInteractions.addAll(mitabReader.read(intactQuery));
            //binaryInteractions.addAll(mitabReader.read(irefindexQuery));

            /* Run cluster using list of binary interactions as input */
            InteractionClusterScore iC = new InteractionClusterScore();
            //iC.addQueryAcc("P23919");

            iC.setBinaryInteractionIterator(binaryInteractions.iterator());
            //iC.setMappingIdDbNames("uniprotkb,irefindex,ddbj/embl/genbank,refseq,chebi"); this is the default value
            //iC.addQuerySource("intact");

            iC.getBinaryInteractionIterator();
            iC.runService();


            /* Retrieve results */
            Map<Integer, EncoreInteraction> interactionMapping = iC.getInteractionMapping();

            for (Integer integer : interactionMapping.keySet()) {
                EncoreInteraction interaction = interactionMapping.get(integer);
                System.out.println(interaction.getInteractorAccsA());
                //System.out.println(interaction.getInteractorA("uniprot"));
                //System.out.println(interaction.getInteractorB());
                //interaction.get


            }

            Map<String, List<Integer>> interactorMapping = iC.getInteractorMapping();
            Map<String, String> synonymMapping = iC.getSynonymMapping();
            int interactionMappingId = iC.getInteractionMappingId();

            /* Test */
//            assertTrue(interactionMappingId > 0);
//            assertTrue(interactionMapping.size() > 0);
//            assertTrue(interactorMapping.size() > 0);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (PsimiTabException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
