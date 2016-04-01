package org.reactome.server.tools.interactors.tuple.model;

import java.util.HashSet;
import java.util.Set;

/**
 * User data container is the generic result for any kind of file the user send to our
 * parsers. This is the common model that we exchange between the core and the service.
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class UserDataContainer {

    private Set<CustomInteraction> customInteractions;
    // then here we have the 15 columns that we can map

    public Set<CustomInteraction> getCustomInteractions() {
        return customInteractions;
    }

    public void setCustomInteractions(Set<CustomInteraction> customInteractions) {
        this.customInteractions = customInteractions;
    }

    public void addCustomInteraction(CustomInteraction customInteraction){
        if (customInteractions == null){
            customInteractions = new HashSet<>();
        }

        customInteractions.add(customInteraction);
    }
}
