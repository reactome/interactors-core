package org.reactome.server.interactors.psicquic;

import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.reactome.server.interactors.exception.CustomPsicquicInteractionClusterException;
import org.reactome.server.interactors.exception.PsicquicInteractionClusterException;
import org.reactome.server.interactors.exception.PsicquicQueryException;
import org.reactome.server.interactors.exception.PsicquicResourceNotFoundException;
import org.reactome.server.interactors.model.Interaction;
import org.reactome.server.interactors.model.PsicquicResource;
import psidev.psi.mi.tab.PsimiTabException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public interface PsicquicDAO {

    Map<String, List<Interaction>> getInteraction(String resource, Collection<String> accs) throws PsicquicQueryException, PsimiTabException, PsicquicRegistryClientException, PsicquicResourceNotFoundException;

    Map<String, List<Interaction>> getInteractionFromCustomPsicquic(String url, Collection<String> accs) throws CustomPsicquicInteractionClusterException;

    Map<String, Integer> countInteraction(String resource, Collection<String> accs) throws PsicquicQueryException, PsimiTabException, PsicquicRegistryClientException, PsicquicResourceNotFoundException;

    List<PsicquicResource> getResources() throws PsicquicInteractionClusterException;

    Map<String, List<Interaction>> getInteraction(String resource, Collection<String> accs, int numberOfThreads, boolean cache) throws PsicquicQueryException, PsimiTabException, PsicquicRegistryClientException, PsicquicResourceNotFoundException;
}
