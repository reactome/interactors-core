package org.reactome.server.interactors.psicquic.clients;

import org.reactome.server.interactors.model.InteractionDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class MenthaClient extends AbstractClient {

    MenthaClient(String resource) {
        super(resource);
    }

    @Override
    public String getDatabaseNames() {
        return "uniprotkb,chebi,intact,mint,dip,matrixdb";
    }

    /**
     * The identifier in Mentha is a little bit messy. It has various databases as part of the interaction identifier.
     * In this case the database will be part of the identifier.
     *
     * @param interactionAcs key=interactionId, value=dbSource
     *
     */
    public List<InteractionDetails> getInteractionIdentifier(Map<String, List<String>> interactionAcs) {
        List<InteractionDetails> interactionDetailsList = new ArrayList<>();

        String[] databaseNames = getDatabaseNames().split(",");
        for (String db : databaseNames) {
            for (String interactionId : interactionAcs.keySet()) {
                if (db.equals(interactionAcs.get(interactionId).get(0))) {
                    InteractionDetails interactionDetails = new InteractionDetails();
                    interactionDetails.setInteractionAc(interactionId + "#" + interactionAcs.get(interactionId).get(0));

                    interactionDetailsList.add(interactionDetails);
                }
            }
        }

        if (interactionDetailsList.isEmpty()) {
            for (String interactionId : interactionAcs.keySet()) {
                InteractionDetails interactionDetails = new InteractionDetails();
                interactionDetails.setInteractionAc(interactionId + "#" + interactionAcs.get(interactionId).get(0));

                interactionDetailsList.add(interactionDetails);
            }
        }

        return interactionDetailsList;

    }
}
