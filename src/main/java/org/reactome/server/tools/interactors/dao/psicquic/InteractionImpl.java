package org.reactome.server.tools.interactors.dao.psicquic;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.PsicquicRegistryClientException;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.wsclient.PsicquicClientException;
import org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient;
import org.hupo.psi.mi.psicquic.wsclient.result.MitabSearchResult;
import org.reactome.server.tools.interactors.dao.PsicquicDAO;
import org.reactome.server.tools.interactors.model.*;
import psidev.psi.mi.tab.model.Alias;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.Confidence;
import psidev.psi.mi.tab.model.CrossReference;

import java.util.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

/**
 * Interaction implementation of PsicquicDAO
 * Query psicquic based ond resources
 */
public class InteractionImpl implements PsicquicDAO {

    /**
     * From PSICQUIC Documentation: https://github.com/MICommunity/psicquic/blob/wiki/ClientCodeSample.md
     * Be careful with this approach as it will store all the results in memory.
     * It is useful for queries that do not return massive results (e.g. < 10,000 results).
     *
     * Reactome will query for interactors based on the given accession
     */
    private static final int MAX_RESULTS = 200;


    @Override
    public List<Interaction> getInteraction(String resource, String acc) {
        List<Interaction> interactions = new ArrayList<>();

        try {
            PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();

            ServiceType service = registryClient.getService(resource);

            InteractorResource interactorResource = new InteractorResource();
            interactorResource.setId(1000L);
            interactorResource.setName("UniProt");
            interactorResource.setUrl("http://www.uniprot.org/query/##ID##");

            InteractionResource interactionResource = new InteractionResource();
            interactionResource.setId(2000L);
            interactionResource.setName("IntAct");
            interactionResource.setUrl("http://www.ebi.ac.uk/intact/interaction/##ID##");

            List<BinaryInteraction> allBinaryInteraction = new ArrayList<>();

            UniversalPsicquicClient client = new UniversalPsicquicClient(service.getSoapUrl());

            MitabSearchResult result = client.getByInteractor(acc.trim(), 0, MAX_RESULTS);

            allBinaryInteraction.addAll(result.getData());

            for (BinaryInteraction binaryInteraction : allBinaryInteraction) {

                Interaction interaction = buildInteraction(acc, binaryInteraction);

                if (interaction != null) {
                    interactions.add(interaction);
                }

                // TODO: THINK! return a list of warnings, e.g This interaction has a B-null value ?
            }

        } catch (PsicquicRegistryClientException e) {
            //fail(e.getMessage());
        } catch (PsicquicClientException e) {
            System.out.println(" ## Error querying. It is active, but the return data couldn't be fetched");
        }

        return interactions;

    }

    @Override
    public Map<String, List<Interaction>> getInteraction(String resource, Collection<String> accs) {
        Map<String, List<Interaction>> interactionsRtn = new HashMap<>();

        try {
            PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();

            ServiceType service = registryClient.getService(resource);

            InteractorResource interactorResource = new InteractorResource();
            interactorResource.setId(1000L);
            interactorResource.setName("UniProt");
            interactorResource.setUrl("http://www.uniprot.org/query/##ID##");

            InteractionResource interactionResource = new InteractionResource();
            interactionResource.setId(2000L);
            interactionResource.setName("IntAct");
            interactionResource.setUrl("http://www.ebi.ac.uk/intact/interaction/##ID##");

            List<BinaryInteraction> allBinaryInteraction = new ArrayList<>();

            UniversalPsicquicClient universalPsicquicClient = new UniversalPsicquicClient(service.getSoapUrl());

            for (String acc : accs) {
                List<Interaction> interactions = new ArrayList<>();
                MitabSearchResult result = universalPsicquicClient.getByInteractor(acc.trim(), 0, MAX_RESULTS);

                allBinaryInteraction.addAll(result.getData());

                for (BinaryInteraction binaryInteraction : allBinaryInteraction) {

//                    PsicquicClient client;
//                    switch (resource){
//                        case "InnateDB"
//
//                    }
                    Interaction interaction = buildInteraction(acc.trim(), binaryInteraction);

                    if (interaction != null) {
                        interactions.add(interaction);
                    }

                    // TODO: THINK! return a list of warnings, e.g This interaction has a B-null value ?
                }

                interactionsRtn.put(acc.trim(), interactions);

            }
        } catch (PsicquicRegistryClientException e) {
            //fail(e.getMessage());
        } catch (PsicquicClientException e) {
            System.out.println(" ## Error querying. It is active, but the return data couldn't be fetched");
        }

        return interactionsRtn;
    }

    /**
     * Helper that creates an Interaction from a BinaryInteraction
     *
     * @param acc
     * @param binaryInteraction
     * @return null if the interaction should be ignored otherwise Interaction.
     */
    private Interaction buildInteraction(String acc, BinaryInteraction binaryInteraction) {
        Interaction interaction = new Interaction();

        /**
         * Depends on the resource it does not provide InteractionAC, if this is the case then ignore it.
         */
        if (binaryInteraction.getInteractionAcs() == null || binaryInteraction.getInteractionAcs().size() == 0)
            return null;

        List<InteractionDetails> interactionDetailsList = new ArrayList<>();
        if (binaryInteraction.getInteractionAcs().size() > 0) {
            CrossReference cr = (CrossReference) binaryInteraction.getInteractionAcs().iterator().next();
            String interactionAc = cr.getIdentifier();

            InteractionDetails interactionDetails = new InteractionDetails();
            interactionDetails.setInteractionAc(interactionAc);
            interactionDetailsList.add(interactionDetails);
        }
        interaction.setInteractionDetailsList(interactionDetailsList);

        /**
         * While querying PSICQUIC e.g P16497 in the MPIDB service, it return an interaction with the B-side as null.
         * ID A: P16497 and ID B: null ( then we will ignore it )
         */
        Interactor interactorA = new Interactor();
        if (binaryInteraction.getInteractorA() == null || binaryInteraction.getInteractorA().getIdentifiers().size() == 0) {
            return null;
        }

        interactorA.setAcc(binaryInteraction.getInteractorA().getIdentifiers().iterator().next().getIdentifier());

        if (binaryInteraction.getInteractorA().getAliases().size() > 0) {
            String aliasName = getAlias(binaryInteraction.getInteractorA().getAliases());
            interactorA.setAlias(aliasName);
        }


        //interactorA.setInteractorResourceId(rs.getLong("INTERACTOR_RESOURCE_A_ID"));
        //interactorA.setIntactId(rs.getString("INTACT_IDA"));
        //interactorA.setTaxid(rs.getInt("TAX_IDA"));

        Interactor interactorB = new Interactor();
        if (binaryInteraction.getInteractorB() == null || binaryInteraction.getInteractorB().getIdentifiers().size() == 0) {
            return null;
        }

        interactorB.setAcc(binaryInteraction.getInteractorB().getIdentifiers().iterator().next().getIdentifier());

        if (binaryInteraction.getInteractorB().getAliases().size() > 0) {
            String aliasName = getAlias(binaryInteraction.getInteractorB().getAliases());
            interactorB.setAlias(aliasName);
        }

        //interactorB.setInteractorResourceId(rs.getLong("INTERACTOR_RESOURCE_B_ID"));
        //interactorB.setIntactId(rs.getString("INTACT_IDB"));
        //interactorB.setTaxid(rs.getInt("TAX_IDB"));

        /**
         * If A interacts with B and B with A we are talking about the same interaction, so
         * just to keep it easy to create the JSON - the interactor in the query will be always on side of A
         * otherwise just set them as A.set(b) and B.set(a).
         */
        if (acc.equals(interactorA.getAcc())) {
            interaction.setInteractorA(interactorA);
            interaction.setInteractorB(interactorB);
        } else {
            interaction.setInteractorA(interactorB);
            interaction.setInteractorB(interactorA);
        }

//        if (binaryInteraction.getConfidenceValues().size() > 0) {
//            Confidence confidence = (Confidence) binaryInteraction.getConfidenceValues().iterator().next();
//
//            if (isNumeric(confidence.getValue())) {
//                interaction.setIntactScore(new Double(confidence.getValue()));
//            }
//
//        }

        String score = getConfidenceValue(binaryInteraction.getConfidenceValues());
        if(!score.isEmpty()) {
            interaction.setIntactScore(new Double(getConfidenceValue(binaryInteraction.getConfidenceValues())));
        }

        return interaction;

    }

    // TODO this method is duplicated in this project...
    public boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }

        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
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
    private String getConfidenceValue(List confidenceValues) {
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
    private String getAlias(List<Alias> aliases) {
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
}