package org.reactome.server.tools.interactors.dao.psicquic;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.reactome.server.tools.interactors.dao.PsicquicDAO;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.Interactor;
import org.reactome.server.tools.interactors.model.psicquic.GenericClient;
import org.reactome.server.tools.interactors.model.psicquic.PsicquicClient;
import psidev.psi.mi.tab.PsimiTabException;
import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteraction;
import uk.ac.ebi.enfin.mi.cluster.score.InteractionClusterScore;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractionClusterImpl implements PsicquicDAO {

    private static final Double MINIMUM_VALID_SCORE = 0.45;

    private final String QUERY_METHOD = "interactor";

    @Override
    public List<Interaction> getInteraction(String resource, String acc) {

        return null;
    }

    @Override
    public Map<String, List<Interaction>> getInteraction(String resource, Collection<String> accs) {
        Map<String, List<Interaction>> ret = new HashMap<>();

        try {
            /** Get PsicquicResource **/
            PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();
            ServiceType service = registryClient.getService(resource);

            for (String acc : accs) {
                List<Interaction> interactions = new ArrayList<>();

                /** Build service URL **/
                String queryRestUrl = service.getRestUrl().concat(QUERY_METHOD).concat("/").concat(acc);

                /** Get binaryInteractions from PSI-MI files **/
                URL url = new URL(queryRestUrl);

                List<BinaryInteraction> binaryInteractions = new ArrayList<>();

                PsimiTabReader mitabReader = new PsimiTabReader();
                binaryInteractions.addAll(mitabReader.read(url));

                /** Run cluster using list of binary interactions as input **/
                InteractionClusterScore interactionClusterScore = new InteractionClusterScore();
                interactionClusterScore.setBinaryInteractionIterator(binaryInteractions.iterator());
                interactionClusterScore.runService();

                /* Retrieve results */
                Map<Integer, EncoreInteraction> interactionMapping = interactionClusterScore.getInteractionMapping();

                for (Integer key : interactionMapping.keySet()) {
                    EncoreInteraction encoreInteraction = interactionMapping.get(key);

                    PsicquicClient psicquicClient = new GenericClient(resource);

                    Interaction interaction = psicquicClient.getInteraction(encoreInteraction);

                    /** make sure the acc in the search is always on link A **/
                    if (!acc.equals(interaction.getInteractorA().getAcc())) {
                        Interactor tempA = interaction.getInteractorA();

                        interaction.setInteractorA(interaction.getInteractorB());
                        interaction.setInteractorB(tempA);
                    }

                    if(interaction.getIntactScore() >= MINIMUM_VALID_SCORE) {
                        interactions.add(interaction);
                    }
                }

                Collections.sort(interactions);
                Collections.reverse(interactions);

                ret.put(acc, interactions);

            }

        } catch (IOException | PsimiTabException | PsicquicRegistryClientException e) {
            e.printStackTrace();
        }

        return ret;

    }
}
