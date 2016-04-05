package org.reactome.server.tools.interactors.tuple.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User data container is the generic result for any kind of file the user send to our
 * parsers. This is the common model that we exchange between the core and the service.
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class UserDataContainer {

    private Map<CustomInteraction, CustomInteraction> customInteractions = new HashMap<>();

    public Collection<CustomInteraction> getCustomInteractions() {
        return customInteractions.values();
    }

    public void addCustomInteraction(CustomInteraction customInteraction) {
        CustomInteraction aux = customInteractions.get(customInteraction);
        if (aux == null || customInteraction.getConfidenceValue() > aux.getConfidenceValue()) {
            customInteractions.put(customInteraction, customInteraction);
        }
    }
}
