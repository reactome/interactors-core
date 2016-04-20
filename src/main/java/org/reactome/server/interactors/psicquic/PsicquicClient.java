package org.reactome.server.interactors.psicquic;

import org.reactome.server.interactors.model.Interaction;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteraction;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface PsicquicClient {

    Interaction getInteraction(EncoreInteraction encoreInteraction);

    String getDatabaseNames();
}
