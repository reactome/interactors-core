package org.reactome.server.tools.interactors.model.psicquic;

import org.reactome.server.tools.interactors.model.Interactor;
import psidev.psi.mi.tab.model.Alias;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.Confidence;

import java.util.Iterator;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class GenericClient extends AbstractClient {

    public GenericClient(String resource) {
        super(resource);
    }

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
    public String getConfidenceValue(List confidenceValues) {
        String intactScore = "";
        String otherScore = "";

        Iterator it = confidenceValues.iterator();
        while(it.hasNext()){
            Confidence confidence = (Confidence)it.next();
            String type = confidence.getType();

            if (type.equalsIgnoreCase("intact-miscore") && intactScore.isEmpty()) {
                // check if intact is number
                if (isNumeric(confidence.getValue())) {
                    intactScore = confidence.getValue();
                }
            } else if (otherScore.isEmpty()) {
                // check if score is number
                if (isNumeric(confidence.getValue())) {
                    otherScore = confidence.getValue();
                }
            }
        }

        if (!intactScore.isEmpty()) return intactScore;

        return otherScore;
    }

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
    public String getAlias(List<Alias> aliases) {
        String uniprotAlias = "";
        String psimiAlias = "";
        String otherAlias = "";

        for (Alias alias : aliases) {
            String db = alias.getDbSource();

            if (db.equalsIgnoreCase("uniprotkb") && uniprotAlias.isEmpty()) {
                uniprotAlias = alias.getName();
            } else if (db.equalsIgnoreCase("psi-mi") && psimiAlias.isEmpty()) {
                psimiAlias = alias.getName();
            } else if (otherAlias.isEmpty()) {
                otherAlias = alias.getName();
            }
        }

        if (!uniprotAlias.isEmpty()) return uniprotAlias;

        if (!psimiAlias.isEmpty()) return psimiAlias;

        return otherAlias;
    }

    @Override
    public String getInteractionAc(List interactionAcs) {
        return null;
    }

    @Override
    public Interactor getInteractorA(BinaryInteraction binaryInteraction) {
        Interactor interactorA = new Interactor();

        if (binaryInteraction.getInteractorA() == null || binaryInteraction.getInteractorA().getIdentifiers().size() == 0) {
            return null;
        }

        interactorA.setAcc(binaryInteraction.getInteractorA().getIdentifiers().iterator().next().getIdentifier());

        if (binaryInteraction.getInteractorA().getAliases().size() > 0) {
            String aliasName = getAlias(binaryInteraction.getInteractorA().getAliases());
            interactorA.setAlias(aliasName);
        }

        return interactorA;
    }

    @Override
    public Interactor getInteractorB(BinaryInteraction binaryInteraction) {
        if (binaryInteraction.getInteractorB() == null || binaryInteraction.getInteractorB().getIdentifiers().size() == 0) {
            return null;
        }

        binaryInteraction.getInteractorB().getIdentifiers().iterator().next().getIdentifier();

        if (binaryInteraction.getInteractorB().getAliases().size() > 0) {
            String aliasName = getAlias(binaryInteraction.getInteractorB().getAliases());
            //interactorA.setAlias(aliasName);
        }
        return null;

    }
}
