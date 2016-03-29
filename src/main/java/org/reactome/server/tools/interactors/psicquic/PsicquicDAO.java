package org.reactome.server.tools.interactors.psicquic;

import org.reactome.server.tools.interactors.exception.CustomPsicquicInteractionClusterException;
import org.reactome.server.tools.interactors.exception.PsicquicInteractionClusterException;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.PsicquicResource;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public interface PsicquicDAO {

    Map<String, List<Interaction>> getInteraction(String resource, Collection<String> accs) throws PsicquicInteractionClusterException;

    Map<String, List<Interaction>> getInteractionFromCustomPsicquic(String url, Collection<String> accs) throws CustomPsicquicInteractionClusterException;

    Map<String, Integer> countInteraction(String resource, Collection<String> accs) throws PsicquicInteractionClusterException;

    List<PsicquicResource> getResources() throws PsicquicInteractionClusterException;


}
