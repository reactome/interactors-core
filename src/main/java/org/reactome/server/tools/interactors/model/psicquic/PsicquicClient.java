package org.reactome.server.tools.interactors.model.psicquic;

import org.reactome.server.tools.interactors.model.Interaction;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteraction;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface PsicquicClient {

    Interaction getInteraction(EncoreInteraction encoreInteraction);

}
