package org.reactome.server.tools.interactors.util;

import org.reactome.server.tools.interactors.dao.DAOFactory;
import org.reactome.server.tools.interactors.dao.InteractionDAO;
import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.Interactor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class IntactParser {

    /** This is the default intact file URL, a program argument can be specified in order to use a different URL **/
    private static String INTACT_FILE_URL = "ftp://ftp.ebi.ac.uk/pub/databases/intact/current/psimitab/intact-micluster.txt";
    private static String INTACT_SCORE_LABEL = "intact-score";
    private static String AUTHOR_SCORE_LABEL = "author score";

    private List<String> parserErrorMessages = new ArrayList<String>();

    public InteractionDAO interactionDAO = DAOFactory.createInterationDAO();

    /**
     *
     * @param file
     */
    public void parser(String file) {
        try {
            URL url = new URL(file);

            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String inputLine;

            /**
             * First line is a header
             */
            String header = in.readLine();

            /** Intact Identifiers **/
            // intact:EBI-7122727|intact:EBI-7122766|intact:EBI-7122684|intact:EBI-7121552

            System.out.println(header);

            int lines = 1;
            while ((inputLine = in.readLine()) != null) {
                String[] content = inputLine.split("\\t");

                List<Interaction> interaction = interactionFromFile(content);

                //interactionDAO.persist(interaction);


                lines++;
            }

            System.out.println(lines);
            in.close();

        } catch (IOException ex) {
            // there was some connection problem, or the file did not exist on the server,
            // or your URL was not in the right format.
            // think about what to do now, and put it here.
            ex.printStackTrace(); // for now, simply output it.
        }

    }

    public static void main(String[] args) {
        String aa = "chebi:CHEBI:27083480";
        String[] aabb = aa.split("\\|");

        String file = INTACT_FILE_URL;
        if (args.length > 0){
            file = args[0];
        }

        IntactParser intactParser = new IntactParser();
        intactParser.parser(file);
    }

    /**
     * A given interactorA and interactorB can have a list of different InteractionID.
     *
     * @param line
     * @return
     */
    public List<Interaction> interactionFromFile(String[] line){

        Interactor interactorA = new Interactor();
        Interactor interactorB = new Interactor();

        /**
         * Even though identifiers from multiple databases can be separated by "|", it is recommended to give only one
         * identifier in this column. It is recommended that proteins be identified by stable identifiers such as their
         * UniProtKB or RefSeq accession number. Small molecules should have Chebi identifiers, nucleic acids should
         * have embl/ddbj/genbank identifiers and gene should have entrez gene/locuslink, ensembl, or ensemblGenome
         * identifiers. This column should never be empty ('-') except for describing intra-molecular interactions or
         * auto-catalysis. Ex: uniprotkb:P12346
         */
        /** sample of ID Interactor A => intact:EBI-7121510 **/
        String[] accRawA = line[IntactParserIndex.ID_INTERACTOR_A].split(":");
        interactorA.setAcc(accRawA[1]);

        /** sample of Alt. ID(s) interactor A => uniprotkb:Q1231 **/
        parseAlternativeIds(line[IntactParserIndex.ALTERNATIVE_INTERACTOR_A], interactorA);

        /** sample of ID Interactor B => intact:EBI-7121510 **/
        String[] accRawB = line[IntactParserIndex.ID_INTERACTOR_B].split(":");
        interactorB.setAcc(accRawB[1]);

        /** sample of Alt. ID(s) interactor B =>  uniprotkb:Q15301 **/
        parseAlternativeIds(line[IntactParserIndex.ALTERNATIVE_INTERACTOR_B], interactorB);

        /** Create interaction **/
        List<Interaction> interactions = prepareInteractions(line, interactorA, interactorB);

        return interactions;
    }

    private List<Interaction> prepareInteractions(String[] line, Interactor interactorA, Interactor interactorB) {

        List<Interaction> interactions = new ArrayList<>();

        /** Get interaction ID column **/
        String allInteractionIds = line[IntactParserIndex.INTERACTION_IDENTIFIER];

        String[] interactionIds = allInteractionIds.split("\\|");
        for (String interactionIdCompound : interactionIds) {
            String[] interactionId = interactionIdCompound.split(":");
            String id = interactionId[1];

            Interaction interaction = new Interaction();
            interaction.setInteractionId(id);

            interaction.setInteractorA(interactorA);
            interaction.setInteractorB(interactorB);

            parseConfidenceValue(line[IntactParserIndex.CONFIDENCE_VALUE],interaction);

            interactions.add(interaction);
        }

        return interactions;

    }

    private void parseAlternativeIds(String value, Interactor interactor) {
        if (!value.equals("-")){ // not null
            String[] alternativeIdsRaw = value.split("\\|");
            for (String alternativeIds : alternativeIdsRaw) {
                /** Considering only the first split. Otherwise ChEBI id breaks the split. e.g chebi:CHEBI:23423 **/
                String[] alternativeId = alternativeIds.split(":", 2);
                interactor.addAlternativeIds(alternativeId[1]);
            }
        }else {
            parserErrorMessages.add("Interactor alternative ID(s) are null.");
        }
    }

    private void parseConfidenceValue(String value, Interaction interaction) {

        if (!value.equals("-")){ // not null
            String[] alternativeIdsRaw = value.split("\\|");
            for (String alternativeIds : alternativeIdsRaw) {
                String[] alternativeId = alternativeIds.split(":");
                if(alternativeId[0].equalsIgnoreCase(AUTHOR_SCORE_LABEL)){
                    if(isNumeric(alternativeId[1])){
                        interaction.setAuthorScore(new Double(alternativeId[1]));
                    }else {
                        parserErrorMessages.add("The author score is not a number." + alternativeId[1]);
                    }
                }
                if(alternativeId[0].equalsIgnoreCase(INTACT_SCORE_LABEL)){
                    if(isNumeric(alternativeId[1])){
                        interaction.setIntactScore(new Double(alternativeId[1]));
                    }else {
                        parserErrorMessages.add("The intact-score is not a number." + alternativeId[1]);
                    }
                }
            }
        }
    }

    /**
     * Instead of calling the Double.valueOf(...) in a try-catch statement and many of the checks to fail due to
     * not being a number then performance of this mechanism will not be great, since you're relying upon
     * exceptions being thrown for each failure, which is a fairly expensive operation.
     * <p>
     * An alternative approach may be to use a regular expression to check for validity of being a number:
     *
     * @param str
     * @return true if is Number
     */
    public boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }

        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
}

