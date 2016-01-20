package org.reactome.server.tools.interactors;

import com.martiansoftware.jsap.*;
import org.apache.commons.io.FileUtils;
import org.reactome.server.tools.interactors.database.InteractorsDatabase;
import org.reactome.server.tools.interactors.model.*;
import org.reactome.server.tools.interactors.service.InteractionParserService;
import org.reactome.server.tools.interactors.service.InteractionResourceService;
import org.reactome.server.tools.interactors.service.InteractorResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class IntactParser {

    enum ParserIndex {

        ID_INTERACTOR_A(0),
        ID_INTERACTOR_B(1),
        ALTERNATIVE_INTERACTOR_A(2),
        ALTERNATIVE_INTERACTOR_B(3),
        ALIAS_INTERACTOR_A(4),
        ALIAS_INTERACTOR_B(5),
        INTERACTION_DETECTION_METHOD(6),
        PUBLICATION_1ST_AUTHOR(7),
        PUBLICATION_IDENTIFIER(8),
        TAXID_INTERACTOR_A(9),
        TAXID_INTERACTOR_B(10),
        INTERACTION_TYPE(11),
        SOURCE_DATABASE(12),
        INTERACTION_IDENTIFIER(13),
        CONFIDENCE_VALUE(14);

        final int value;

        ParserIndex(int value) {
            this.value = value;
        }
    }

    static final Logger logger = LoggerFactory.getLogger(IntactParser.class);

    /** This is the default intact file URL, a program argument can be specified in order to use a different URL **/
    private static String INTACT_FILE_URL = "ftp://ftp.ebi.ac.uk/pub/databases/intact/current/psimitab/intact-micluster.txt";

    private static String INTACT_SCORE_LABEL = "intact-miscore";
    private static String AUTHOR_SCORE_LABEL = "author score";

    private static String PSI_MI_LABEL = "psi-mi";

    /** Regex that extracts the Taxonid from taxid:9606(human) **/
    private Pattern pattern = Pattern.compile("([0-9]+)");

    /** Parse file messages **/
    private String outputFileMessages = "";

    /** Errors report lists **/
    private Set<String> parserErrorMessages = new HashSet<>();
    private List<String> dbErrorMessages = new ArrayList<>();

    /** Services Declaration **/
    private InteractionParserService interactionParserService;
    private InteractorResourceService interactorResourceService;
    private InteractionResourceService interactionResourceService;

    public IntactParser(InteractorsDatabase database) {
        interactionParserService = new InteractionParserService(database);
        interactorResourceService = new InteractorResourceService(database);
        interactionResourceService = new InteractionResourceService(database);
    }

    /**
     * Easy access to the Resources.
     */
    private Map<String, InteractionResource> interactionResourceMap = new HashMap<>();
    private Map<String, InteractorResource> interactorResourceMap = new HashMap<>();

    /**
     * Parsing the fil
     *
     * @param file
     */
    public void parser(String file) {
        try {
            String inputLine;
            BufferedReader br = new BufferedReader(new FileReader(file));

            /**
             * First line is a header. Calling readLine will point the cursor to the next line.
             */
            br.readLine();

            /** Intact Identifiers **/
            // intact:EBI-7122727|intact:EBI-7122766|intact:EBI-7122684|intact:EBI-7121552

            int lines = 1;

            List<Interaction> interactionList = new ArrayList<>();
            while ((inputLine = br.readLine()) != null) {
                String[] content = inputLine.split("\\t");

                /** Parse the line **/
                Interaction interaction = interactionFromFile(content);

                interactionList.add(interaction);

                /** Go to the database every 1000 interactions **/
                if((lines % 1000) == 0){
                    logger.info("Performing a DB save. Rows parsed [{}]", lines);
                    try {
                        interactionParserService.save(interactionList);
                        interactionList.clear();
                    }catch (SQLException e){
                        logger.error("Exception thrown during DB save: ", e);
                        dbErrorMessages.add("Error inserting interactions to the Database." + e.getMessage());
                    }
                }
                lines++;
            }

            // Persist remaining items
            try {
                logger.info("Performing a DB save. Rows parsed [{}]", lines);

                interactionParserService.save(interactionList);
                interactionList.clear();
            } catch (SQLException e){
                logger.error("Exception thrown during DB save: ", e);
                dbErrorMessages.add("Error inserting interactions to the Database." + e.getMessage());
            }

            br.close();

        } catch (IOException ex) {
            logger.error("Can't get the IntAct file. Please check the logs.", ex);
        } catch (Exception ex) {
            logger.error("Got a generic exception. Please check the logs.", ex);
        } finally {
            if(parserErrorMessages.size() > 0) {
                logger.info("There are [{}] error messages in the IntAct file.", parserErrorMessages.size());
                writeOutputFile(parserErrorMessages, outputFileMessages);
            }

            if(dbErrorMessages.size() > 0 ) {
                logger.info("There are [{}] db error messages.", dbErrorMessages.size());
                writeOutputFile(dbErrorMessages, "db-error-messages.txt");
            }
        }

    }

    private void writeOutputFile(Collection<String> messages, String filename) {
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

        } catch(IOException e){
            logger.error("Can't create the output file.", e);
        }
    }

    /**
     * A given interactorA and interactorB can have a list of different InteractionID.
     *
     * @param line
     * @return
     */
    public Interaction interactionFromFile(String[] line){

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
        String[] intactIdRawA = line[ParserIndex.ID_INTERACTOR_A.value].split(":");
        interactorA.setIntactId(intactIdRawA[1]);

        /** sample of Alt. ID(s) interactor A => uniprotkb:Q1231 **/
        parseAlternativeIds(line[ParserIndex.ALTERNATIVE_INTERACTOR_A.value], interactorA);

        /** gene/ewas name **/
        parseAliases(line[ParserIndex.ALIAS_INTERACTOR_A.value], interactorA);

        /** taxid:9606(human) **/
        parseTaxonomy(line[ParserIndex.TAXID_INTERACTOR_A.value], interactorA);

        /** sample of ID Interactor B => intact:EBI-7121510 **/
        String[] intactIdRawB = line[ParserIndex.ID_INTERACTOR_B.value].split(":");
        interactorB.setIntactId(intactIdRawB[1]);

        /** sample of Alt. ID(s) interactor B =>  uniprotkb:Q15301 **/
        parseAlternativeIds(line[ParserIndex.ALTERNATIVE_INTERACTOR_B.value], interactorB);

        /** **/
        parseAliases(line[ParserIndex.ALIAS_INTERACTOR_B.value], interactorB);

        /** taxid:9606(human) **/
        parseTaxonomy(line[ParserIndex.TAXID_INTERACTOR_B.value], interactorB);

        /** Create interaction **/
        Interaction interaction = prepareInteractions(line, interactorA, interactorB);

        return interaction;
    }

    private void parseTaxonomy(String value, Interactor interactor) {
        if (!value.equals("-")){ // not null
            /** Split at : taxid:9606 **/
            String[] taxid = value.split(":");

            Matcher m = pattern.matcher(taxid[1]);

            if (m.find()) {
                interactor.setTaxid(new Integer(m.group(1)));
            }

        }else {
            interactor.setTaxid(0);
            parserErrorMessages.add("Interactor ID [" + interactor.getAcc() + "] - Does not have taxid.");
        }
    }


    private Interaction prepareInteractions(String[] line, Interactor interactorA, Interactor interactorB) {
        Interaction interaction = new Interaction();

        interaction.setInteractorA(interactorA);
        interaction.setInteractorB(interactorB);

        Long intactResourceId = interactionResourceMap.get("static").getId();
        interaction.setInteractionResourceId(intactResourceId);

        parseConfidenceValue(line[ParserIndex.CONFIDENCE_VALUE.value], interaction);

        /** Get interaction ID column **/
        String allInteractionIds = line[ParserIndex.INTERACTION_IDENTIFIER.value];

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
     * Alternative ID is the accession value.
     * Based on the alternative id - retrieves the interactor resource
     *
     * @param value
     * @param interactor
     */
    private void parseAlternativeIds(String value, Interactor interactor) {
        if (!value.equals("-")){ // not null
            /** Considering only the first split. Otherwise ChEBI id breaks the split. e.g chebi:CHEBI:23423 **/
            String[] alternativeId = value.split(":", 2);
            interactor.setAcc(alternativeId[1]);

            /** Set interactor resource id **/
            InteractorResource interactorResource = interactorResourceMap.get(alternativeId[0]);
            if(interactorResource != null){
                interactor.setInteractorResourceId(interactorResource.getId());
            }

        }else {
            /** In case alternative ID is null we will set the IntAct id as the accession. This is done in the IntactPortal **/
            interactor.setAcc(value);

            parserErrorMessages.add("Interactor ID [" + interactor.getAcc() + "] - Interactor alternative ID(s) are null.");
        }
    }

    /**
     * Parsing the Aliases A and B for the identifiers. Separated by "|".
     *
     * @param value
     * @param interactor
     */
    private void parseAliases(String value, Interactor interactor) {
        if (!value.equals("-")){ // not null
            String[] allAliases = value.split("\\|");
            for (String uniqueAlias : allAliases) {
                /** databaseName:value **/
                String[] alias = uniqueAlias.split(":");

                /**
                 * If alternatives IDs are null, try to figure the resource out in the alias
                 */
                if(interactor.getInteractorResourceId() == 0){
                    InteractorResource interactorResource = interactorResourceMap.get(alias[0]);
                    if(interactorResource != null){
                        interactor.setInteractorResourceId(interactorResource.getId());
                    }
                }

                /** first occurrence of psi-mi should be taken as the alias **/
                if(alias[0].equalsIgnoreCase(PSI_MI_LABEL) && interactor.getAlias() == null){
                    interactor.setAlias(alias[1]);
                }
            }
        }

        /** Some cases like EBI-7121639 there is no resource ???? Yes! There's resource **/
        if(interactor.getInteractorResourceId() == 0){
            parserErrorMessages.add("The Interactor ID [" + interactor.getIntactId() + "] do not have alternate identifiers. Can't get Resource.");
            InteractorResource interactorResource = interactorResourceMap.get("undefined");
            if(interactorResource != null){
                interactor.setInteractorResourceId(interactorResource.getId());
            }
        }
    }

    /**
     *
     * @param value
     * @param interaction
     */
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

    public String getOutputFileMessages() {
        return outputFileMessages;
    }

    public void setOutputFileMessages(String outputFileMessages) {
        this.outputFileMessages = outputFileMessages;
    }

    /**
     * Call parse
     * This is a standalone process that will generate an interactors database and
     * @param args
     */
    public static void main(String[] args) throws JSAPException, IOException {
        long start = System.currentTimeMillis();
        logger.info("Start Parsing IntAct File");

        SimpleJSAP jsap = new SimpleJSAP(
                IntactParser.class.getName(),
                "A tool for parsing Intact file and create a database",
                new Parameter[]{
                        new FlaggedOption("file", JSAP.STRING_PARSER, "/tmp/intact-micluster.txt", JSAP.NOT_REQUIRED, 'f', "file",
                                "IntAct file to be parsed")
                        , new FlaggedOption("url", JSAP.STRING_PARSER, INTACT_FILE_URL, JSAP.NOT_REQUIRED, 'u', "url",
                        "IntAct file URL")
                        , new QualifiedSwitch("download", JSAP.BOOLEAN_PARSER, null, JSAP.NOT_REQUIRED, 'd', "download",
                        "Download IntAct File")
                        , new FlaggedOption("destination", JSAP.STRING_PARSER, "/tmp", JSAP.NOT_REQUIRED, 't', "destination",
                        "Folder to save the downloaded file")
                        , new FlaggedOption("output", JSAP.STRING_PARSER, "/tmp/parser-messages.txt", JSAP.NOT_REQUIRED, 'o', "output",
                        "Output parser file messages")
                }
        );

        //TODO: add as a required parameter
        String database = "/Users/reactome/interactors/interactors.db";
        InteractorsDatabase interactors = null;
        try {
            interactors = new InteractorsDatabase(database);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        IntactParser intactParser = new IntactParser(interactors);
        intactParser.cacheResources();

        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);

        String file = config.getString("file");
        boolean download = config.getBoolean("download");
        String output = config.getString("output");

        if(download){
            String url = config.getString("url");
            String downloadedFile = intactParser.downloadFile(url, config.getString("destination"));
            logger.info("File has been download. Parse will be executed pointing to this file: " + downloadedFile);
            file = downloadedFile;
        }

        intactParser.setOutputFileMessages(output);
        intactParser.parser(file);

        logger.info("Database has been populate properly.");
        logger.info("End IntAct File parsing. Elapsed Time [{}.ms]",(System.currentTimeMillis() - start));
    }

    /**
     * Download the file from Intact server.
     * @return the file path
     * @throws IOException
     */
    public String downloadFile(String urlFtp, String directory) throws IOException{
        URL url = new URL(urlFtp);

        File intactFile = new File(directory, "intact-micluster.txt");
        if(intactFile.exists()){
            intactFile.renameTo(new File(directory,"intact-micluster-"+new Date().toString()+".txt"));
        }
        FileUtils.copyURLToFile(url, intactFile);

        return intactFile.getAbsolutePath();
    }

    /**
     * Caching InteractorResource and InteractionResource
     */
    private void cacheResources() {
        logger.info("Caching Interaction and Interactor Resources from DB");

        try {

            /** Load INTERACTOR resources (chebi, uniprot) **/
            List<InteractorResource> interactorResources = interactorResourceService.getAll();
            for (InteractorResource interactorResource : interactorResources) {
                interactorResourceMap.put(interactorResource.getName().toLowerCase(), interactorResource);
            }

            /** Load INTERACTION resources (intact, ...) **/
            List<InteractionResource> interactionResources = interactionResourceService.getAll();
            for (InteractionResource interactionResource : interactionResources) {
                interactionResourceMap.put(interactionResource.getName().toLowerCase(), interactionResource);
            }

        } catch(SQLException e) {
            logger.error("Error retrieving resources from database", e);
        }

        logger.info("Caching is Done. InteractionResource [{}] and Interactor Resources[{}]", interactionResourceMap.size(), interactorResourceMap.size());

    }
}

