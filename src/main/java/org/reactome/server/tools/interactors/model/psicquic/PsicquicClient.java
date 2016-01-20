package org.reactome.server.tools.interactors.model.psicquic;

import org.reactome.server.tools.interactors.model.Interactor;
import psidev.psi.mi.tab.model.Alias;
import psidev.psi.mi.tab.model.BinaryInteraction;

import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface PsicquicClient {

    /**
     * Retrieve the most appropriate alias from a List of Aliases.
     * The rule is:
     * First: UniProtKB
     * Second: psi-mi
     * Third: The first instance
     *
     * @param confidenceValues
     * @return
     */
    String getConfidenceValue(List confidenceValues);


    /**
     * Retrieve the most appropriate alias from a List of Aliases.
     * The rule is:
     * First: UniProtKB
     * Second: psi-mi
     * Third: The first instance
     *
     * @param aliases
     * @return
     */
    String getAlias(List<Alias> aliases);

    /**
     * Retrieve interactionAc
     * @param interactionAcs
     * @return
     */
    String getInteractionAc(List interactionAcs);

    Interactor getInteractorA(BinaryInteraction binaryInteraction);

    Interactor getInteractorB(BinaryInteraction binaryInteraction);

}
