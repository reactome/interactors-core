package org.reactome.server.tools.interactors.model.psicquic;

import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.Interactor;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteraction;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class GenericClient extends AbstractClient {

    public GenericClient(String resource) {
        super(resource);
    }

    @Override
    public Interaction getInteraction(EncoreInteraction encoreInteraction) {
        Interaction interaction = new Interaction();

        Interactor interactorA = getInteractor(encoreInteraction, InteractorLink.A);
        Interactor interactorB = getInteractor(encoreInteraction, InteractorLink.B);

        interaction.setInteractorA(interactorA);
        interaction.setInteractorB(interactorB);

        interaction.setIntactScore(getMiscore(encoreInteraction.getConfidenceValues()));

        interaction.setInteractionDetailsList(getInteractionAc(encoreInteraction.getExperimentToDatabase()));

        return interaction;
    }

}
