package org.reactome.server.tools.interactors.util;

import org.reactome.server.tools.interactors.model.Interaction;
import org.reactome.server.tools.interactors.model.InteractionDetails;
import org.reactome.server.tools.interactors.model.Interactor;
import org.reactome.server.tools.interactors.service.InteractionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class IntactParser {

    final Logger logger = LoggerFactory.getLogger(IntactParser.class);

    /** This is the default intact file URL, a program argument can be specified in order to use a different URL **/
    private static String INTACT_FILE_URL = "ftp://ftp.ebi.ac.uk/pub/databases/intact/current/psimitab/intact-micluster.txt";

    private static String INTACT_SCORE_LABEL = "intact-miscore";
    private static String AUTHOR_SCORE_LABEL = "author score";

    private List<String> parserErrorMessages = new ArrayList<>();

    private List<String> dbErrorMessages = new ArrayList<>();

    public InteractionService interactionService = InteractionService.getInstance();

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

            int lines = 1;

            List<Interaction> interactionList = new ArrayList<>();
            List<Interaction> secondAttemptInteractionList = new ArrayList<>();
            while ((inputLine = in.readLine()) != null) {
                String[] content = inputLine.split("\\t");

                Interaction interaction = interactionFromFile(content);

                interactionList.add(interaction);

                /** Go to the database every 1000 interacions **/
                if((lines % 1000) == 0){
                    logger.info("Performing a DB save");
                    try {
                        interactionService.save(interactionList);
                        interactionList.clear();
                    }catch (SQLException e){
                        logger.error("Exception thrown during DB save: " + e.getMessage());
                        e.printStackTrace();
                        dbErrorMessages.add("Error inserting interactions to the Database." + e.getMessage());
                        secondAttemptInteractionList.addAll(interactionList);
                    }
                }
                lines++;
            }

            in.close();


            logger.info("There are [" + parserErrorMessages.size() + "] error messages in the IntAct file.");
            writeOutputFile(parserErrorMessages, "parser-error-messages.txt");
//            for (String parserErrorMessage : parserErrorMessages) {
//                //System.out.println(parserErrorMessage);
//            }

            logger.info("There are [" + dbErrorMessages.size() + "] db error messages.");
            writeOutputFile(dbErrorMessages, "db-error-messages.txt");
//            for (String dbErrorMessage : dbErrorMessages) {
//                //System.out.println(dbErrorMessage);
//            }


        } catch (IOException ex) {
           logger.error("Can't get the IntAct file. Please check the provided URL.");
        }
    }

    private void writeOutputFile(List<String> messages, String filename) {
        logger.info("Creating output file. [" + filename + "]. Messages [" + messages.size() + "]");
        try {
            File fout = new File(filename);
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (String message : messages) {
                bw.write(message);
                bw.newLine();
            }

            bw.flush();
            bw.close();

        }catch(IOException e){
            logger.error("Can't create the output file.", e);
        }
    }


    /**
     * You can parse
     * @param args
     */
    public static void main(String[] args) {
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
    public Interaction interactionFromFile(String[] line){

        Interactor interactorA = new Interactor();
        interactorA.setInteractorResourceId(1L);  // TODO This cannot be HARD CODED

        Interactor interactorB = new Interactor();
        interactorB.setInteractorResourceId(1L);  // TODO This cannot be HARD CODED

        /**
         * Even though identifiers from multiple databases can be separated by "|", it is recommended to give only one
         * identifier in this column. It is recommended that proteins be identified by stable identifiers such as their
         * UniProtKB or RefSeq accession number. Small molecules should have Chebi identifiers, nucleic acids should
         * have embl/ddbj/genbank identifiers and gene should have entrez gene/locuslink, ensembl, or ensemblGenome
         * identifiers. This column should never be empty ('-') except for describing intra-molecular interactions or
         * auto-catalysis. Ex: uniprotkb:P12346
         */
        /** sample of ID Interactor A => intact:EBI-7121510 **/
        String[] intactIdRawA = line[IntactParserIndex.ID_INTERACTOR_A].split(":");
        interactorA.setIntactId(intactIdRawA[1]);

        /** sample of Alt. ID(s) interactor A => uniprotkb:Q1231 **/
        parseAlternativeIds(line[IntactParserIndex.ALTERNATIVE_INTERACTOR_A], interactorA);

        /** sample of ID Interactor B => intact:EBI-7121510 **/
        String[] intactIdRawB = line[IntactParserIndex.ID_INTERACTOR_B].split(":");
        interactorB.setIntactId(intactIdRawB[1]);

        /** sample of Alt. ID(s) interactor B =>  uniprotkb:Q15301 **/
        parseAlternativeIds(line[IntactParserIndex.ALTERNATIVE_INTERACTOR_B], interactorB);

        /** Create interaction **/
        Interaction interaction = prepareInteractions(line, interactorA, interactorB);

        return interaction;
    }

    private Interaction prepareInteractions(String[] line, Interactor interactorA, Interactor interactorB) {
        Interaction interaction = new Interaction();

        interaction.setInteractorA(interactorA);
        interaction.setInteractorB(interactorB);

        // TODO This cannot be HARD CODED
        interaction.setInteractionResourceId(1L);

        parseConfidenceValue(line[IntactParserIndex.CONFIDENCE_VALUE], interaction);

        /** Get interaction ID column **/
        String allInteractionIds = line[IntactParserIndex.INTERACTION_IDENTIFIER];

        String[] interactionIds = allInteractionIds.split("\\|");
        for (String interactionIdCompound : interactionIds) {
            String[] interactionId = interactionIdCompound.split(":");
            String id = interactionId[1];

            InteractionDetails interactionDetails = new InteractionDetails();
            interactionDetails.setInteractionAc(id);

            interaction.addInteractionDetails(interactionDetails);

        }

        return interaction;

    }

    /**
     * Alternative ID in the file the is the accession value that we want.
     *
     * @param value
     * @param interactor
     */
    private void parseAlternativeIds(String value, Interactor interactor) {
        if (!value.equals("-")){ // not null
            /** Considering only the first split. Otherwise ChEBI id breaks the split. e.g chebi:CHEBI:23423 **/
            String[] alternativeId = value.split(":", 2);
            interactor.setAcc(alternativeId[1]);
        }else {
            /** In case alternative ID is null we will the IntAct id as the accession. This is done in the IntactPortal **/
            interactor.setAcc(value);
            parserErrorMessages.add("Interactor ID [" + interactor.getAcc() + "] - Interactor alternative ID(s) are null.");
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
                        parserErrorMessages.add("Interactor A [" + interaction.getInteractorA().getIntactId() + "] - Interactor B [" + interaction.getInteractorB().getIntactId() + "] - The author score is not a number [" + alternativeId[1] + "]");
                    }
                }
                if(alternativeId[0].equalsIgnoreCase(INTACT_SCORE_LABEL)){
                    if(isNumeric(alternativeId[1])){
                        interaction.setIntactScore(new Double(alternativeId[1]));
                    }else {
                        parserErrorMessages.add("Interactor A [" + interaction.getInteractorA().getIntactId() + "] - Interactor B [" + interaction.getInteractorB().getIntactId() + "] - The intact-miscore is not a number [" + alternativeId[1] + "]");
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

