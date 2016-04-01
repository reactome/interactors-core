package org.reactome.server.tools.interactors.service;

import org.reactome.server.tools.interactors.exception.CustomPsicquicInteractionClusterException;
import org.reactome.server.tools.interactors.exception.PsicquicInteractionClusterException;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.PsicquicResource;
import org.reactome.server.tools.interactors.psicquic.PsicquicDAO;
import org.reactome.server.tools.interactors.psicquic.impl.InteractionClusterImpl;

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

    public Map<String, List<Interaction>> getInteractions(String resource, Collection<String> accs) throws PsicquicInteractionClusterException {
        return psicquicDAO.getInteraction(resource, accs);
    }

    public Map<String, List<Interaction>> getInteractionFromCustomPsicquic(String url, Collection<String> accs) throws CustomPsicquicInteractionClusterException {
        return psicquicDAO.getInteractionFromCustomPsicquic(url, accs);
    }

    public List<PsicquicResource> getResources() throws PsicquicInteractionClusterException {
        return psicquicDAO.getResources();
    }

    public Map<String, Integer> countInteraction(String resource, Collection<String> accs) throws PsicquicInteractionClusterException {
        return psicquicDAO.countInteraction(resource, accs);
    }

}