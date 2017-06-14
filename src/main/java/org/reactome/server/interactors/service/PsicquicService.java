package org.reactome.server.interactors.service;

import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.reactome.server.interactors.exception.CustomPsicquicInteractionClusterException;
import org.reactome.server.interactors.exception.PsicquicInteractionClusterException;
import org.reactome.server.interactors.exception.PsicquicQueryException;
import org.reactome.server.interactors.exception.PsicquicResourceNotFoundException;
import org.reactome.server.interactors.model.Interaction;
import org.reactome.server.interactors.model.PsicquicResource;
import org.reactome.server.interactors.psicquic.PsicquicDAO;
import org.reactome.server.interactors.psicquic.impl.InteractionClusterImpl;
import psidev.psi.mi.tab.PsimiTabException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

@SuppressWarnings("unused")
public class PsicquicService {

    private PsicquicDAO psicquicDAO;

    public PsicquicService() {
        psicquicDAO = new InteractionClusterImpl();
    }

    public Map<String, List<Interaction>> getInteractions(String resource, Collection<String> accs) throws PsicquicQueryException, PsimiTabException, PsicquicRegistryClientException, PsicquicResourceNotFoundException {
        return psicquicDAO.getInteraction(resource, accs);
    }

    public Map<String, List<Interaction>> getInteractionFromCustomPsicquic(String url, Collection<String> accs) throws CustomPsicquicInteractionClusterException {
        return psicquicDAO.getInteractionFromCustomPsicquic(url, accs);
    }

    public List<PsicquicResource> getResources() throws PsicquicInteractionClusterException {
        return psicquicDAO.getResources();
    }

    public Map<String, Integer> countInteraction(String resource, Collection<String> accs) throws PsicquicQueryException, PsimiTabException, PsicquicRegistryClientException, PsicquicResourceNotFoundException {
        return psicquicDAO.countInteraction(resource, accs);
    }

    public Map<String, List<Interaction>> getInteractions(String resource, Collection<String> accs, int numberOfThreads, boolean cache) throws PsicquicQueryException, PsicquicResourceNotFoundException, PsicquicRegistryClientException, PsimiTabException {
        return psicquicDAO.getInteraction(resource, accs, numberOfThreads, cache);
    }
}