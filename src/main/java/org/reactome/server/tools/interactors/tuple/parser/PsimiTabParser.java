package org.reactome.server.tools.interactors.tuple.parser;

import org.reactome.server.tools.interactors.tuple.exception.ParserException;
import org.reactome.server.tools.interactors.tuple.exception.TupleParserException;
import org.reactome.server.tools.interactors.tuple.model.*;
import org.reactome.server.tools.interactors.tuple.util.FileDefinition;
import psidev.psi.mi.tab.PsimiTabException;
import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.ConfidenceImpl;
import psidev.psi.mi.tab.model.CrossReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.reactome.server.tools.interactors.tuple.parser.response.Response.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class PsimiTabParser extends CommonParser {

    @Override
    public TupleResult parse(List<String> input) throws ParserException {
        // TODO apply the micluster ?

        /** Unique id that identifies the data submission **/
        String token = UUID.randomUUID().toString();

        /** Parser PSI-MITab file **/
        List<BinaryInteraction> binaryInteractions = new ArrayList<>();
        PsimiTabReader mitabReader = new PsimiTabReader();
        try {
            for (String line : input) {
                binaryInteractions.add(mitabReader.readLine(line));
            }
        } catch (PsimiTabException e) {
            throw new ParserException("Error parsing PSI-MITAB file", e);
        }

        /** Read parsed lines and apply our rules to provide a nice summary **/
        UserDataContainer userDataContainer = new UserDataContainer();
        for (int line = 0; line < binaryInteractions.size(); line++) {

            /** Get binaryInteraction that is retrieved. This is provided the psimitab library **/
            BinaryInteraction binaryInteraction = binaryInteractions.get(line);

            /** Create our own custom interaction objects **/
            CustomInteraction customInteraction = getCustomInteraction(binaryInteraction);

            /** Check Mandatory fields **/
            List<String> mandatoryMessages = checkMandatoriesAttributes(customInteraction);
            if (mandatoryMessages.size() == 0) {
                /** Check if an interaction exists based on AccessionA and AccessionB **/
                if (userDataContainer.getCustomInteractions() != null && userDataContainer.getCustomInteractions().contains(customInteraction)) {
                    warningResponses.add(getMessage(DUPLICATE_AB, line + 1, customInteraction.getInteractorIdA(), customInteraction.getInteractorIdB()));
                }

                /** Flip a and b and check again if the interactions exists **/
                customInteraction.flip(customInteraction.getInteractorIdA(), customInteraction.getInteractorIdB());

                if (userDataContainer.getCustomInteractions() != null && userDataContainer.getCustomInteractions().contains(customInteraction)) {
                    warningResponses.add(getMessage(DUPLICATE_BA, line + 1, customInteraction.getInteractorIdB(), customInteraction.getInteractorIdA(), customInteraction.getInteractorIdA(), customInteraction.getInteractorIdB()));
                } else {
                    /** Flip back to original form **/
                    customInteraction.flip(customInteraction.getInteractorIdA(), customInteraction.getInteractorIdB());

                    /** Add to the list **/
                    userDataContainer.addCustomInteraction(customInteraction);
                }
            } else {
                errorResponses.add(getMessage(MISSING_MANDATORY_FIELDS, line + 1, mandatoryMessages));
            }
        }

        if (hasError()) {
            throw new TupleParserException("Error parsing your interactors overlay", errorResponses);
        }

        Summary summary = new Summary();
        summary.setToken(token);
        summary.setInteractions(userDataContainer.getCustomInteractions().size());
        summary.setInteractors(countInteractors(userDataContainer));

        TupleResult result = new TupleResult();
        result.setSummary(summary);
        result.setWarningMessages(warningResponses);

        CustomInteractorRepository.save(token, userDataContainer);

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

    private CustomInteraction getCustomInteraction(BinaryInteraction binaryInteraction) {
        CustomInteraction customInteraction = new CustomInteraction();

        if (binaryInteraction.getInteractorA() != null) {
            customInteraction.setInteractorIdA(binaryInteraction.getInteractorA().getIdentifiers().get(0).getIdentifier());
            customInteraction.setAliasInteractorA(binaryInteraction.getInteractorA().getAliases().get(0).getName());
            customInteraction.setTaxonomyIdInteractorA(binaryInteraction.getInteractorA().getOrganism().getTaxid());
        }

        if (binaryInteraction.getInteractorB() != null) {
            customInteraction.setInteractorIdB(binaryInteraction.getInteractorB().getIdentifiers().get(0).getIdentifier());
            customInteraction.setAliasInteractorB(binaryInteraction.getInteractorB().getAliases().get(0).getName());
            customInteraction.setTaxonomyIdInteractorB(binaryInteraction.getInteractorB().getOrganism().getTaxid());
        }

        if (binaryInteraction.getConfidenceValues() != null && binaryInteraction.getConfidenceValues().size() > 0) {
            customInteraction.setConfidenceValue(((ConfidenceImpl) binaryInteraction.getConfidenceValues().get(0)).getValue());
        }

        if (binaryInteraction.getInteractionAcs() != null && binaryInteraction.getInteractionAcs().size() > 0) {
            CrossReference xref = (CrossReference) binaryInteraction.getInteractionAcs().get(0);
            customInteraction.setInteractionIdentifier(xref.getText());
        }

        return customInteraction;
    }
}
