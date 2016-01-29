package org.reactome.server.tools.interactors.dao.psicquic;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.reactome.server.tools.interactors.dao.PsicquicDAO;
import org.reactome.server.tools.interactors.exception.PsicquicInteractionClusterException;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.Interactor;
import org.reactome.server.tools.interactors.model.PsicquicResource;
import org.reactome.server.tools.interactors.model.psicquic.GenericClient;
import org.reactome.server.tools.interactors.model.psicquic.PsicquicClient;
import org.reactome.server.tools.interactors.util.InteractorConstant;
import psidev.psi.mi.tab.PsimiTabException;
import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteraction;
import uk.ac.ebi.enfin.mi.cluster.score.InteractionClusterScore;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractionClusterImpl implements PsicquicDAO {

    /**
     *
     * This method queries PSICQUIC for a given resource (retrieve the resource URL directly on the server) and accession
     * list and retrieves the interactions clustered. This method only take into account those interactions which the
     * score is higher than MINIMUM_VALID_SCORE. It also sort by score with highest on top. Some oddity cases are being
     * solved by the removeDuplicatedInteractor which keeps from the interactor list only the highest score.
     *
     * @param resource PSICQUIC Resource
     * @param accs List of accession
     * @return map of accession as key an its list of interaction
     *
     * @throws PsicquicInteractionClusterException
     */
    public Map<String, List<Interaction>> getInteraction(String resource, Collection<String> accs) throws PsicquicInteractionClusterException {
        Map<String, List<Interaction>> ret = new HashMap<>();

        for (String acc : accs) {
            List<Interaction> interactions = new ArrayList<>();

            InteractionClusterScore interactionClusterScore = getInteractionClusterScore(resource, acc);

            /** Retrieve results **/
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

                /** Requirement: Add in the interaction list only scores higher than MINIMUM_VALID_SCORE **/
                if(interaction.getIntactScore() >= InteractorConstant.MINIMUM_VALID_SCORE) {
                    interactions.add(interaction);
                }
            }

            Collections.sort(interactions);
            Collections.reverse(interactions);

            removeDuplicatedInteractor(interactions);

            ret.put(acc, interactions);

        }

        return ret;

    }

    /**
     * For the same Accession retrieve the list of interactors. If the interactors are the same we will
     * remove the duplicates and keep the one of highest score.
     *
     * Requirement: Keep only the one with highest score if the interactors are the same (with different identifiers)
     *              e.g CHEBI:16027 (16027) for ChEMBL.
     *
     * @param interactions A sorted and reversed list of interactions (Highest score on top).
     */
    private void removeDuplicatedInteractor(List<Interaction> interactions) {

        Iterator<Interaction> it = interactions.iterator();

        String auxIndentifier = "";
        while(it.hasNext()) {
            Interaction interaction = it.next();

            if(interaction.getInteractorB().getAcc().equalsIgnoreCase(auxIndentifier)){
                it.remove();
            }

            auxIndentifier = interaction.getInteractorB().getAcc();

        }

    }

    public Map<String, Integer> countInteraction(String resource, Collection<String> accs) throws PsicquicInteractionClusterException {
        Map<String, Integer> ret = new HashMap<>();

        for (String acc : accs) {

            /** Run cluster using list of binary interactions as input **/
            InteractionClusterScore interactionClusterScore = getInteractionClusterScore(resource, acc);

            /* Retrieve results */
            Map<Integer, EncoreInteraction> interactionMapping = interactionClusterScore.getInteractionMapping();

            int countInteractionsAboveThreshold = 0;

            for (Integer key : interactionMapping.keySet()) {
                EncoreInteraction encoreInteraction = interactionMapping.get(key);

                PsicquicClient psicquicClient = new GenericClient(resource);

                Interaction interaction = psicquicClient.getInteraction(encoreInteraction);
                if(interaction.getIntactScore() >= InteractorConstant.MINIMUM_VALID_SCORE) {
                    countInteractionsAboveThreshold++;
                }
            }

            ret.put(acc, countInteractionsAboveThreshold);

        }

        return ret;

    }

    /**
     * @return Psicquic Resources sorted by name
     * @throws PsicquicInteractionClusterException
     */
    public List<PsicquicResource> getResources() throws PsicquicInteractionClusterException {
        PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();

        List<PsicquicResource> resourceList = new ArrayList<>();

        try {
            List<ServiceType> services = registryClient.listServices();
            for (ServiceType service : services) {
                PsicquicResource p = new PsicquicResource();
                p.setActive(service.isActive());
                p.setName(service.getName());
                p.setRestURL(service.getRestUrl());
                p.setSoapURL(service.getSoapUrl());

                resourceList.add(p);
            }

        } catch (PsicquicRegistryClientException e) {
            throw new PsicquicInteractionClusterException(e);
        }

        Collections.sort(resourceList);

        return  resourceList;
    }

    /**
     * Helper method that create the InteractionClusterScore.
     *
     * @throws PsicquicInteractionClusterException
     */
    private InteractionClusterScore getInteractionClusterScore(String resource, String acc) throws PsicquicInteractionClusterException {
        final String queryMethod = "interactor/";

        try {
            /** Get PsicquicResource **/
            PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();
            ServiceType service = registryClient.getService(resource);

            /** Build service URL **/
            String queryRestUrl = service.getRestUrl().concat(queryMethod).concat(URLEncoder.encode(acc, "UTF-8"));

            /** Get binaryInteractions from PSI-MI files **/
            URL url = new URL(queryRestUrl);

            List<BinaryInteraction> binaryInteractions = new ArrayList<>();

            PsimiTabReader mitabReader = new PsimiTabReader();
            binaryInteractions.addAll(mitabReader.read(url));

            /** Run cluster using list of binary interactions as input **/
            InteractionClusterScore interactionClusterScore = new InteractionClusterScore();
            interactionClusterScore.setBinaryInteractionIterator(binaryInteractions.iterator());
            interactionClusterScore.runService();

            return interactionClusterScore;

        } catch (IOException | PsimiTabException | PsicquicRegistryClientException e) {
            throw new PsicquicInteractionClusterException(e);
        }
    }
}
