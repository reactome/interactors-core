package org.reactome.server.interactors.psicquic.impl;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.reactome.server.interactors.exception.CustomPsicquicInteractionClusterException;
import org.reactome.server.interactors.exception.PsicquicInteractionClusterException;
import org.reactome.server.interactors.exception.PsicquicQueryException;
import org.reactome.server.interactors.exception.PsicquicResourceNotFoundException;
import org.reactome.server.interactors.model.Interaction;
import org.reactome.server.interactors.model.Interactor;
import org.reactome.server.interactors.model.PsicquicResource;
import org.reactome.server.interactors.psicquic.PsicquicClient;
import org.reactome.server.interactors.psicquic.PsicquicDAO;
import org.reactome.server.interactors.psicquic.clients.ClientFactory;
import org.reactome.server.interactors.util.InteractorConstant;
import org.reactome.server.interactors.util.PsimiTabReaderRunnable;
import org.reactome.server.interactors.util.Toolbox;
import psidev.psi.mi.tab.PsimiTabException;
import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteraction;
import uk.ac.ebi.enfin.mi.cluster.score.InteractionClusterScore;
import uk.ac.ebi.enfin.mi.cluster.score.InteractionClusterScoreCache;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InteractionClusterImpl implements PsicquicDAO {

    public static final String FORMAT = "?format=tab26";

    /**
     * This method queries PSICQUIC for a given resource (retrieve the resource URL directly on the server) and accession
     * list and retrieves the interactions clustered. This method only take into account those interactions which the
     * score is higher than MINIMUM_VALID_SCORE. It also sort by score with highest on top. Some oddity cases are being
     * solved by the removeDuplicatedInteractor which keeps from the interactor list only the highest score.
     *
     * @param resource PSICQUIC Resource
     * @param accs     List of accession
     * @return map of accession as key an its list of interaction
     * @throws PsicquicQueryException, PsimiTabException, PsicquicRegistryClientException
     */
    @Override
    public Map<String, List<Interaction>> getInteraction(String resource, Collection<String> accs) throws PsicquicQueryException, PsimiTabException, PsicquicRegistryClientException, PsicquicResourceNotFoundException {
        Map<String, List<Interaction>> ret = new HashMap<>();
        for (String acc : accs) {
            PsicquicClient psicquicClient = ClientFactory.getClient(resource);
            String databaseNames = psicquicClient.getDatabaseNames();
            InteractionClusterScore interactionClusterScore = getInteractionClusterScore(resource, acc, databaseNames);

            // Retrieve results
            Map<Integer, EncoreInteraction> interactionMapping = interactionClusterScore.getInteractionMapping();
            List<Interaction> interactions = getClusteredInteraction(acc, interactionMapping, interactionClusterScore, psicquicClient);
            interactions = Toolbox.removeDuplicatedInteractor(interactions);
            Collections.sort(interactions);
            Collections.reverse(interactions);
            ret.put(acc, interactions);
        }
        return ret;
    }

    @Override
    public Map<String, List<Interaction>> getInteractionFromCustomPsicquic(String url, Collection<String> accs) throws CustomPsicquicInteractionClusterException {
        Map<String, List<Interaction>> ret = new HashMap<>();
        for (String acc : accs) {
            PsicquicClient psicquicClient = ClientFactory.getClient(InteractorConstant.GENERIC_CLIENT_FACTORY); // This client does not exist and the GenericClient will be instantiate.
            String databaseNames = psicquicClient.getDatabaseNames();
            InteractionClusterScore interactionClusterScore = getInteractionClusterForCustomPsicquic(url, acc, databaseNames);

            // Retrieve results
            Map<Integer, EncoreInteraction> interactionMapping = interactionClusterScore.getInteractionMapping();
            List<Interaction> interactions = getClusteredInteraction(acc, interactionMapping, interactionClusterScore, psicquicClient);
            interactions = Toolbox.removeDuplicatedInteractor(interactions);
            Collections.sort(interactions);
            Collections.reverse(interactions);

            ret.put(acc, interactions);
        }
        return ret;
    }

    private List<Interaction> getClusteredInteraction(String acc, Map<Integer, EncoreInteraction> interactionMapping, InteractionClusterScore interactionClusterScore, PsicquicClient psicquicClient) {
        List<Interaction> interactions = new ArrayList<>();

        for (Integer key : interactionMapping.keySet()) {
            EncoreInteraction encoreInteraction = interactionMapping.get(key);
            encoreInteraction.setMappingIdDbNames(interactionClusterScore.getMappingIdDbNames());

            Interaction interaction = psicquicClient.getInteraction(encoreInteraction);
            if (interaction == null) continue;

            // make sure the acc in the search is always on link A
            if (!acc.equals(interaction.getInteractorA().getAcc())) {
                Interactor tempA = interaction.getInteractorA();

                interaction.setInteractorA(interaction.getInteractorB());
                interaction.setInteractorB(tempA);
            }

            // Requirement: Add in the interaction list only scores higher than MINIMUM_VALID_SCORE
            Double score = interaction.getIntactScore();
            if (score >= InteractorConstant.MINIMUM_VALID_SCORE) {
                // Must round score after checking the MINIMUM_VALID_SCORE
                interaction.setIntactScore(Toolbox.roundScore(score));
                interactions.add(interaction);
            }
        }
        return interactions;
    }

    @Override
    public Map<String, Integer> countInteraction(String resource, Collection<String> accs) throws PsicquicQueryException, PsimiTabException, PsicquicRegistryClientException, PsicquicResourceNotFoundException {
        Map<String, Integer> ret = new HashMap<>();
        for (String acc : accs) {
            // Run cluster using list of binary interactions as input
            InteractionClusterScore interactionClusterScore = getInteractionClusterScore(resource, acc, null);
            // Retrieve results
            Map<Integer, EncoreInteraction> interactionMapping = interactionClusterScore.getInteractionMapping();
            int countInteractionsAboveThreshold = 0;
            for (Integer key : interactionMapping.keySet()) {
                EncoreInteraction encoreInteraction = interactionMapping.get(key);
                PsicquicClient psicquicClient = ClientFactory.getClient(resource);
                Interaction interaction = psicquicClient.getInteraction(encoreInteraction);
                if (interaction == null) continue;
                if (interaction.getIntactScore() >= InteractorConstant.MINIMUM_VALID_SCORE) {
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
    @Override
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

        return resourceList;
    }

    /**
     * Helper method that create the InteractionClusterScore.
     *
     * @throws PsicquicQueryException, PsimiTabException, PsicquicRegistryClientException
     */
    private InteractionClusterScore getInteractionClusterScore(String resource, String acc, String databaseNames) throws PsicquicQueryException, PsimiTabException, PsicquicRegistryClientException, PsicquicResourceNotFoundException {
        final String queryMethod = "interactor/";

        try {
            // Get PsicquicResource
            PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();
            ServiceType service = registryClient.getService(resource);

            if (service == null) {
                throw new PsicquicResourceNotFoundException(resource + " not found");
            }

            // Build service URL
            String queryRestUrl = service.getRestUrl().concat(queryMethod).concat(URLEncoder.encode(acc, "UTF-8").concat(FORMAT));

            return prepareInteratorClusterScore(queryRestUrl, databaseNames);
        } catch (IOException e) {
            throw new PsicquicQueryException(e);
        }
    }

    /**
     * Helper method that create the InteractionClusterScore.
     *
     * @throws CustomPsicquicInteractionClusterException
     */
    private InteractionClusterScore getInteractionClusterForCustomPsicquic(String customURL, String acc, String databaseNames) throws CustomPsicquicInteractionClusterException {
        final String queryMethod = "";

        try {
            // Build service URL
            String queryRestUrl = customURL.concat(queryMethod).concat(URLEncoder.encode(acc, "UTF-8")).concat(FORMAT);

            return prepareInteratorClusterScore(queryRestUrl, databaseNames);

        } catch (IOException | PsimiTabException e) {
            throw new CustomPsicquicInteractionClusterException(e);
        }
    }

    /**
     * Common method to get the InteractorClusterScore.
     * It fits either in Psicquic or Custom Psicquic
     */
    private InteractionClusterScore prepareInteratorClusterScore(String queryRestUrl, String databaseNames) throws IOException, PsimiTabException {
        // Get binaryInteractions from PSI-MI files
        URL url = new URL(queryRestUrl);

        List<BinaryInteraction> binaryInteractions = new ArrayList<>();

        PsimiTabReader mitabReader = new PsimiTabReader();
        binaryInteractions.addAll(mitabReader.read(url));

        // Run cluster using list of binary interactions as input
        InteractionClusterScore interactionClusterScore = new InteractionClusterScore();
        interactionClusterScore.setBinaryInteractionIterator(binaryInteractions.iterator());

        // This is the dbSource added in the alias
        interactionClusterScore.setMappingIdDbNames(databaseNames);

        interactionClusterScore.runService();

        return interactionClusterScore;
    }

    @Override
    public Map<String, List<Interaction>> getInteraction(String resource, Collection<String> accs, int numberOfThreads, boolean cache) throws PsicquicQueryException, PsimiTabException, PsicquicRegistryClientException, PsicquicResourceNotFoundException {
        Map<String, List<Interaction>> ret = new HashMap<>();
        // Keeps a cache of MITerms.
        InteractionClusterScoreCache interactionClusterScoreCache = new InteractionClusterScoreCache();

        // Resource is the same, there is no need do be in the loop.
        ServiceType service = getPsicquicResource(resource);

        PsicquicClient psicquicClient = ClientFactory.getClient(resource);
        String databaseNames = psicquicClient.getDatabaseNames();

        /*
         *  Multi-thread mechanism to get the RAW data from the given resource.
         */
        // 1) Get Raw data
        ConcurrentHashMap<String, List<BinaryInteraction>> proteinInteractionsMap = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        for (String acc : accs) {
            try {
                URL url = new URL(service.getRestUrl().concat("interactor/").concat(URLEncoder.encode(acc, "UTF-8")));
                Runnable worker = new PsimiTabReaderRunnable(acc, url, proteinInteractionsMap);
                executor.submit(worker);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        try {
            // TODO Should we wait MAX_VALUE ?! Research needed.
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // TODO Add some logs in here ?
        }

        // 2) Cluster
        for (String acc : proteinInteractionsMap.keySet()) {
            InteractionClusterScore interactionClusterScore = new InteractionClusterScore();
            if (cache) {
                interactionClusterScore = new InteractionClusterScore(interactionClusterScoreCache);
            }
            interactionClusterScore.setBinaryInteractionIterator(proteinInteractionsMap.get(acc).iterator());
            interactionClusterScore.setMappingIdDbNames(databaseNames);
            interactionClusterScore.runService();

            // Build results
            Map<Integer, EncoreInteraction> interactionMapping = interactionClusterScore.getInteractionMapping();
            List<Interaction> interactions = getClusteredInteraction(acc, interactionMapping, interactionClusterScore, psicquicClient);
            interactions = Toolbox.removeDuplicatedInteractor(interactions);
            Collections.sort(interactions);
            Collections.reverse(interactions);

            ret.put(acc, interactions);
        }

        return ret;
    }

    private ServiceType getPsicquicResource(String resource) throws PsicquicRegistryClientException, PsicquicResourceNotFoundException, PsicquicQueryException {
        PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();
        ServiceType service = registryClient.getService(resource);

        if (service == null) {
            throw new PsicquicResourceNotFoundException(resource + " not found");
        }

        return service;
    }
}
