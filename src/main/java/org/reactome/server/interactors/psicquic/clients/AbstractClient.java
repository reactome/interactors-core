package org.reactome.server.interactors.psicquic.clients;

import org.reactome.server.interactors.model.Interaction;
import org.reactome.server.interactors.model.InteractionDetails;
import org.reactome.server.interactors.model.Interactor;
import org.reactome.server.interactors.psicquic.PsicquicClient;
import psidev.psi.mi.tab.model.Confidence;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public abstract class AbstractClient implements PsicquicClient {

    enum InteractorLink {A, B}

    protected final int CHEMICAL_ALIAS_SIZE_THRESHOLD = 15;

    protected String resource;

    AbstractClient(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public Interaction getInteraction(EncoreInteraction encoreInteraction) {
        Interaction interaction = new Interaction();

        Interactor interactorA = getInteractor(encoreInteraction, InteractorLink.A);
        Interactor interactorB = getInteractor(encoreInteraction, InteractorLink.B);

        interaction.setInteractorA(interactorA);
        interaction.setInteractorB(interactorB);

        interaction.setIntactScore(getScore(encoreInteraction.getConfidenceValues()));

        interaction.setInteractionDetailsList(getInteractionIdentifier(encoreInteraction.getExperimentToDatabase()));

        return interaction;
    }

    public Interactor getInteractor(EncoreInteraction encoreInteraction, InteractorLink link) {
        Interactor interactor = new Interactor();

        switch (link){
            case A:
                interactor.setAcc(getAcc(encoreInteraction.getInteractorAccsA()));
                interactor.setAlias(getAlias(encoreInteraction.getOtherInteractorAccsA()));
                break;
            case B:
                interactor.setAcc(getAcc(encoreInteraction.getInteractorAccsB()));
                interactor.setAlias(getAlias(encoreInteraction.getOtherInteractorAccsB()));
                break;
        }

        return interactor;
    }

    /**
     * Get miscore which is calculate by InteractionClusterScore service
     * Now, if intact-miscore is present, take intact-miscore.
     *
     * @return confidence value
     */
    public double getScore(List<Confidence> confidenceValues) {
        String intactMiscore = "";
        String miscore = "";
        for (Confidence confidence : confidenceValues) {
            String type = confidence.getType();
            if (type.equalsIgnoreCase("intact-miscore") && intactMiscore.isEmpty()) {
                intactMiscore = confidence.getValue();
            } else if (type.equalsIgnoreCase("miscore") && miscore.isEmpty()) {
                miscore = confidence.getValue();
            }
        }

        String retScore = miscore;
        if(!intactMiscore.isEmpty()){
            retScore = intactMiscore;
        }

        return new Double(retScore);
    }

    /**
     * Retrieve all the alias of a given accession
     *
     * @param accessions key=dbsource{psi-mi,uniprot,etc} value=alias
     */
    public String getAlias(Map<String, List<String>> accessions) {
        String psimiAlias = "";
        String uniprotAlias = "";
        String otherAlias = "";

        for (String dbSource : accessions.keySet()) {
            if (dbSource.equalsIgnoreCase("psi-mi") && psimiAlias.isEmpty()) {
                //psimiAlias = accessions.get(dbSource).get(0);
                for (String alias : accessions.get(dbSource)){
                    // if it matches the regex : assign
                    if(isPotencialAlias(alias) && psimiAlias.isEmpty()) {
                        psimiAlias = alias;
                    }
                }
            } else if (dbSource.equalsIgnoreCase("uniprotkb") && uniprotAlias.isEmpty()) {
                uniprotAlias = accessions.get(dbSource).get(0);
            } else if (otherAlias.isEmpty()) {
                otherAlias = accessions.get(dbSource).get(0);
            }
        }

        String rtn = otherAlias;
        if (!psimiAlias.isEmpty()){
            rtn = psimiAlias;
        } else if (!uniprotAlias.isEmpty()){
            rtn = uniprotAlias;
        }

        // Checking the size is a save the day solution.
        return rtn.length() > CHEMICAL_ALIAS_SIZE_THRESHOLD ? null : rtn.toUpperCase();
    }

    /**
     * This is the same implementation from AbstractEncoreInteraction
     * It queries the mapping db and return the first occurrence.
     *
     * @param interactorAccs List of interactor Accs. dbName,identifier
     * @return the one that matches the dbNames
     */
    public String getAcc(Map<String, String> interactorAccs) {
        String interactorAcc = null;
        String[] databaseNames = getDatabaseNames().split(",");
        for (String db : databaseNames) {
            if(interactorAccs.containsKey(db)) {
                interactorAcc = interactorAccs.get(db);
                break;
            }
        }

        if(interactorAcc == null) {
            interactorAcc = interactorAccs.values().iterator().next();
        }

        return interactorAcc;
    }

    /**
     * Retrieve all the alias of a given accession
     * Aliases are passed all in the same String delimited by $
     *
     * @param accessions key=dbsource{psi-mi,uniprot,etc} value=alias
     * @return alias1$alias2 -2tyrosine$alias3 (1,2)
     */
    public String getSynonyms(Map<String, List<String>> accessions) {
        String allSynonyms = "";
        for (String dbSource : accessions.keySet()) {
            for(String alias : accessions.get(dbSource)){
                allSynonyms = allSynonyms.concat(alias).concat("$");
            }
        }

        if(allSynonyms.endsWith("$")){
            allSynonyms = allSynonyms.substring(0, allSynonyms.length() - 1);
        }

        return allSynonyms;
    }

    /**
     * Get interaction identifier
     * @param interactionAcs key=interactionId, value=dbSource
     */
    public List<InteractionDetails> getInteractionIdentifier(Map<String, List<String>> interactionAcs) {
        List<InteractionDetails> interactionDetailsList = new ArrayList<>();
        for (String interactionId : interactionAcs.keySet()) {
            if(interactionId != null && !interactionId.isEmpty()) {
                InteractionDetails interactionDetails = new InteractionDetails();
                interactionDetails.setInteractionAc(interactionId);
                interactionDetailsList.add(interactionDetails);
            }
        }

        return interactionDetailsList;
    }

    protected boolean isPotencialAlias(String pAlias){
        Pattern p = Pattern.compile("^([a-zA-Z0-9\\s:-_]{2,15})");
        return p.matcher(pAlias).matches();
    }
}
