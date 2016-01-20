package org.reactome.server.tools.interactors.dao;

import org.reactome.server.tools.interactors.model.Interaction;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface PsicquicDAO {

    List<Interaction> getInteraction(String resource, String acc);

    Map<String, List<Interaction>> getInteraction(String resource, Collection<String> accs);

}
