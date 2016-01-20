package org.reactome.server.tools.interactors.model.psicquic;

import org.reactome.server.tools.interactors.model.Interactor;
import psidev.psi.mi.tab.model.Alias;
import psidev.psi.mi.tab.model.BinaryInteraction;

import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class DIPClient extends AbstractClient {

    public DIPClient(String resource) {
        super(resource);
    }

    @Override
    public String getConfidenceValue(List confidenceValues) {
        return null;
    }

    @Override
    public String getAlias(List<Alias> aliases) {
        return null;
    }

    @Override
    public String getInteractionAc(List interactionAcs) {
        return null;
    }

    @Override
    public Interactor getInteractorA(BinaryInteraction binaryInteraction) {
        return null;
    }

    @Override
    public Interactor getInteractorB(BinaryInteraction binaryInteraction) {
        return null;
    }
}
