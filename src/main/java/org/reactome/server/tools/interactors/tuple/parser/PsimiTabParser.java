package org.reactome.server.tools.interactors.tuple.parser;

import org.reactome.server.tools.interactors.tuple.exception.ParserException;
import org.reactome.server.tools.interactors.tuple.model.*;
import org.reactome.server.tools.interactors.tuple.util.FileDefinition;
import psidev.psi.mi.tab.PsimiTabException;
import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.ConfidenceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class PsimiTabParser extends CommonParser {

    @Override
    public TupleResult parse(List<String> input) throws ParserException {
        String token = UUID.randomUUID().toString();

        List<BinaryInteraction> binaryInteractions = new ArrayList<>();

        PsimiTabReader mitabReader = new PsimiTabReader();

        UserDataContainer udc = new UserDataContainer();

        try {
            for (String line : input) {
                binaryInteractions.add(mitabReader.readLine(line));
            }
        } catch (PsimiTabException e) {
            throw new ParserException("Error parsing PSI-MITAB file", e);
        }

        for (BinaryInteraction binaryInteraction : binaryInteractions) {
            CustomInteraction customInteraction = new CustomInteraction();
            customInteraction.setInteractorIdA(binaryInteraction.getInteractorA().getIdentifiers().get(0).getIdentifier());
            customInteraction.setInteractorIdB(binaryInteraction.getInteractorB().getIdentifiers().get(0).getIdentifier());

            customInteraction.setConfidenceValue(((ConfidenceImpl) binaryInteraction.getConfidenceValues().get(0)).getValue());

            udc.addCustomInteraction(customInteraction);
        }

        Summary summary = new Summary();
        summary.setToken(token);
        summary.setInteractions(udc.getCustomInteractions().size());
        summary.setInteractors(222);

        TupleResult result = new TupleResult();
        result.setSummary(summary);
        result.setWarningMessages(warningResponses);

        CustomInteractorRepository.save(token, udc);

        return result;
    }

    @Override
    public FileDefinition getParserDefinition(List<String> lines) {
        PsimiTabReader mitabReader = new PsimiTabReader();
        try {
            for (String line : lines) {
                mitabReader.readLine(line);
            }

            return FileDefinition.PSIMITAB_DATA;

        } catch (PsimiTabException e) {
            return null;
        }
    }
}
