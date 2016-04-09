package org.reactome.server.tools.interactors.tuple.parser;

import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.InteractionDetails;
import org.reactome.server.tools.interactors.psicquic.PsicquicClient;
import org.reactome.server.tools.interactors.psicquic.clients.ClientFactory;
import org.reactome.server.tools.interactors.tuple.custom.CustomResource;
import org.reactome.server.tools.interactors.tuple.exception.ParserException;
import org.reactome.server.tools.interactors.tuple.exception.TupleParserException;
import org.reactome.server.tools.interactors.tuple.model.CustomInteraction;
import org.reactome.server.tools.interactors.tuple.model.Summary;
import org.reactome.server.tools.interactors.tuple.model.TupleResult;
import org.reactome.server.tools.interactors.tuple.util.FileDefinition;
import org.reactome.server.tools.interactors.util.InteractorConstant;
import psidev.psi.mi.tab.PsimiTabException;
import psidev.psi.mi.tab.PsimiTabReader;
import uk.ac.ebi.enfin.mi.cluster.ClusterServiceException;
import uk.ac.ebi.enfin.mi.cluster.EncoreInteraction;
import uk.ac.ebi.enfin.mi.cluster.score.InteractionClusterScore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.reactome.server.tools.interactors.tuple.parser.response.Response.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class PsimiTabParser extends CommonParser {

    //private int START_INDEX = 0;

    @Override
    public TupleResult parse(List<String> input) throws ParserException {
        int avoidedByScore = 0;

        /** This is a work-around because the PSI-MITAB reader didn't work fine with the iterator **/
        String file = "";
        for (String line : input) {
            file += line + "\n";
        }

        InputStream is = new ByteArrayInputStream(file.getBytes());

        /** Run cluster using list of binary interactions as input **/
        String mappingIdDbNames = "uniprotkb,chebi,intact,irefindex,ddbj/embl/genbank,refseq,unknown";

        InteractionClusterScore interactionClusterScore;
        try {
            interactionClusterScore = new InteractionClusterScore(is, false);
        } catch (ClusterServiceException e) {
            throw new ParserException("Error while reading the PSI-MITAB content");
        }

        interactionClusterScore.setMappingIdDbNames(mappingIdDbNames);
        interactionClusterScore.runService();

        /** Retrieve results **/
        Map<Integer, EncoreInteraction> interactionMapping = interactionClusterScore.getInteractionMapping();

        CustomResource customResource = new CustomResource();
        for (Integer key : interactionMapping.keySet()) {
            EncoreInteraction encoreInteraction = interactionMapping.get(key);
            encoreInteraction.setMappingIdDbNames(interactionClusterScore.getMappingIdDbNames());

            /** Create our own custom interaction object from clustered results **/
            CustomInteraction customInteraction = getCustomInteraction(encoreInteraction);

            /** Check Mandatory fields **/
            List<String> mandatoryMessages = checkMandatoryAttributes(customInteraction);
            if (mandatoryMessages.size() == 0) {
                /**
                 * After clustering the results we assume that duplicates have been clustered,
                 * so we don't need to check it here.
                 * If there is any duplicates we cannot track the line and return a precise warning list
                 */

                /** Add to the list taking into account the MINIMUM_VALID_SCORE **/
                if (customInteraction.getConfidenceValue() >= InteractorConstant.MINIMUM_VALID_SCORE) {
                    customResource.add(customInteraction);
                } else {
                    avoidedByScore++;
                }

            } else {
                /** If there is any error we cannot track the line once the results have been clustered by MICluster **/
                errorResponses.add(getMessage(MISSING_MANDATORY_FIELDS, 0, mandatoryMessages));
            }

        }

        if (hasError()) {
            throw new TupleParserException("Error parsing your interactors overlay", errorResponses);
        }

        if (avoidedByScore > 0) {
            warningResponses.add(getMessage(AVOIDED_SCORE, avoidedByScore, InteractorConstant.MINIMUM_VALID_SCORE));
        }

        Summary summary = new Summary();
        summary.setInteractions(customResource.getInteractions());
        summary.setInteractors(customResource.getInteractors());

        TupleResult result = new TupleResult();
        result.setSummary(summary);
        result.setWarningMessages(warningResponses);
        result.setCustomResource(customResource);

        return result;
    }

    @Override
    public FileDefinition getParserDefinition(List<String> lines) {
        PsimiTabReader mitabReader = new PsimiTabReader();
        try {
            for (String line : lines) {
                mitabReader.readLine(line);
            }

            // TODO try stopping parse after some lines. Summary can provide more details
            return FileDefinition.PSIMITAB_DATA;

        } catch (PsimiTabException e) {
            return null;
        }
    }

    /**
     * Prepare the CustomInteraction to be stored based on the EncoreInteraction
     *
     * @param encoreInteraction is the clustered results based on the MICluster
     *
     * @return the CustomInteraction
     */
    private CustomInteraction getCustomInteraction(EncoreInteraction encoreInteraction) {
        /** Create the GenericClient and get interactions from it **/
        PsicquicClient client = ClientFactory.getClient(InteractorConstant.GENERIC_CLIENT_FACTORY);
        Interaction interaction = client.getInteraction(encoreInteraction);

        /** Create the CustomInteraction and populate it getting the values from encoreInteraction **/
        CustomInteraction customInteraction = new CustomInteraction();
        customInteraction.setInteractorIdA(interaction.getInteractorA().getAcc());
        customInteraction.setAliasInteractorA(interaction.getInteractorA().getAlias());
        customInteraction.setInteractorIdB(interaction.getInteractorB().getAcc());
        customInteraction.setAliasInteractorB(interaction.getInteractorB().getAlias());
        customInteraction.setConfidenceValue(interaction.getIntactScore());

        /** Right now just retrieving the first one in the list **/
        List<InteractionDetails> interactionEvidences = interaction.getInteractionDetailsList();
        customInteraction.setInteractionEvidence(interactionEvidences.get(0).getInteractionAc());

        return customInteraction;
    }
}
