package org.reactome.server.interactors;

import com.martiansoftware.jsap.*;
import org.apache.commons.io.FileUtils;
import org.reactome.server.interactors.database.InteractorsDatabase;
import org.reactome.server.interactors.model.*;
import org.reactome.server.interactors.service.InteractionParserService;
import org.reactome.server.interactors.service.InteractionResourceService;
import org.reactome.server.interactors.service.InteractorResourceService;
import org.reactome.server.interactors.util.InteractorConstant;
import org.reactome.server.interactors.util.InteractorDatabaseGenerator;
import org.reactome.server.interactors.util.Toolbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Double.parseDouble;
import static java.lang.Double.valueOf;
import static java.lang.Integer.parseInt;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class IntactParser {

    private static final Logger logger = LoggerFactory.getLogger(IntactParser.class);
    // This is the default intact file URL, a program argument can be specified in order to use a different URL
    private static final String INTACT_FILE_URL = "ftp://ftp.ebi.ac.uk/pub/databases/intact/current/psimitab/intact-micluster.txt";
    private static final String INTACT_SCORE_LABEL = "intact-miscore";
    private static final String AUTHOR_SCORE_LABEL = "author score";
    private static final String PSI_MI_LABEL = "psi-mi";
    private static final String PUBMED_LABEL = "pubmed";
    private static Map<String, String> resourceMapping = new HashMap<>();

    static {
        resourceMapping.put("uniprotkb", "UniProt");
        resourceMapping.put("chebi", "ChEBI");
        resourceMapping.put("ensemblgenomes", "ENSEMBL");
        resourceMapping.put("ddbj/embl/genbank", "EMBL");
    }

    // Regex that extracts the Taxonid from taxid:9606(human)
    private Pattern pattern = Pattern.compile("([0-9]+)");
    // Errors report lists
    private Set<String> parserErrorMessages = new HashSet<>();
    private List<String> dbErrorMessages = new ArrayList<>();
    // Services Declaration
    private InteractionParserService interactionParserService;
    private InteractorResourceService interactorResourceService;
    private InteractionResourceService interactionResourceService;
    // Easy access to the Resources.
    private Map<String, InteractionResource> interactionResourceMap = new HashMap<>();
    private Map<String, InteractorResource> interactorResourceMap = new HashMap<>();
    private Set<Interaction> interactions = new HashSet<>();

    private IntactParser(InteractorsDatabase database) {
        interactionParserService = new InteractionParserService(database);
        interactorResourceService = new InteractorResourceService(database);
        interactionResourceService = new InteractionResourceService(database);
    }

    /**
     * This is a standalone process that will generate an interactors database and parse the IntAct static file
     */
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        logger.info("Start Parsing IntAct File");

        SimpleJSAP jsap = new SimpleJSAP(IntactParser.class.getName(),"A tool for parsing Intact file and store information into a SQLite database",
                new Parameter[]{
                        new FlaggedOption("file", JSAP.STRING_PARSER, "/tmp/intact-micluster.txt", JSAP.NOT_REQUIRED, 'f', "file","IntAct file to be parsed"),
                        new FlaggedOption("url", JSAP.STRING_PARSER, INTACT_FILE_URL, JSAP.NOT_REQUIRED, 'u', "url","IntAct file URL"),
                        new QualifiedSwitch("download", JSAP.BOOLEAN_PARSER, null, JSAP.NOT_REQUIRED, 'd', "download","Download IntAct File"),
                        new FlaggedOption("destination", JSAP.STRING_PARSER, "/tmp", JSAP.NOT_REQUIRED, 't', "destination","Folder to save the downloaded file"),
                        new FlaggedOption("interactors-database-path", JSAP.STRING_PARSER, null, JSAP.REQUIRED, 'g', "interactors-database-path","Interactor Database Path")
                }
        );

        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);

        // Check if database exists and create a new one
        String database = config.getString("interactors-database-path");
        try {
            File dbFile = new File(database);
            if (dbFile.exists()) {
                logger.error("Database [{}] already exists in this location. Please inform a different database location or name.", database);
                System.exit(1);
            }

            InteractorDatabaseGenerator.create(new InteractorsDatabase(database).getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Open database connection.
        InteractorsDatabase interactors = new InteractorsDatabase(database);
        IntactParser intactParser = new IntactParser(interactors);
        intactParser.cacheResources();

        String file = config.getString("file");
        boolean download = config.getBoolean("download");

        if (download) {
            String url = config.getString("url");
            String downloadedFile = intactParser.downloadFile(url, config.getString("destination"));
            logger.info("File has been download. Parse will be executed pointing to this file: " + downloadedFile);
            file = downloadedFile;
        }

        intactParser.parser(file);

        logger.info("Database has been populate. The database size is [{} MB]", new File(database).length() / (1024L * 1024L));
        logger.info("End IntAct File parsing. Elapsed Time [{}.ms]", (System.currentTimeMillis() - start));
    }

    private static String getDatabaseName(String resource) {
        String rtn = resourceMapping.get(resource.toLowerCase().trim());
        if (rtn != null) return rtn;
        return resource;
    }

    private static String getRawIdentifier(String identifier) {
        if (identifier.contains(":")) return identifier.split(":")[1];
        return identifier;
    }

    /**
     * Retrieves the IntAct file, parses it and creates an temporary database
     *
     * @return an InteractorsDatabase in-memory instance
     * @throws SQLException thrown when there is a problem connecting to the temporary database
     * @throws IOException  thrown when there is a problem accessing to the IntAct file
     */
    public static InteractorsDatabase getInteractors(String fileDatabaseName) throws SQLException, IOException {
        long start = System.currentTimeMillis();
        logger.info("Start Parsing IntAct File");

        FileUtils.deleteQuietly(new File(fileDatabaseName));
        InteractorsDatabase interactors = new InteractorsDatabase(fileDatabaseName);
        InteractorDatabaseGenerator.create(interactors.getConnection(), false);
        IntactParser intactParser = new IntactParser(interactors);
        intactParser.cacheResources();

        String file = intactParser.downloadFile(INTACT_FILE_URL, "/tmp");
        logger.info("File has been download. Parse will be executed pointing to this file: " + file);

        intactParser.parser(file);
        logger.info("End IntAct File parsing. Elapsed Time [{}.ms]", (System.currentTimeMillis() - start));

        return interactors;
    }

    /**
     * Parsing the file
     */
    private void parser(String file) {
        int totalLinesParsed = 1;
        int totalLinesIncluded = 0;
        int totalIgnoredLines = 0;

        try {
            String inputLine;
            BufferedReader br = new BufferedReader(new FileReader(file));

            // First line is a header. Calling readLine will point the cursor to the next line.
            br.readLine();

            // Intact Identifiers
            // intact:EBI-7122727|intact:EBI-7122766|intact:EBI-7122684|intact:EBI-7121552
            List<Interaction> interactionList = new ArrayList<>();
            while ((inputLine = br.readLine()) != null) {
                String[] content = inputLine.split("\\t");

                totalLinesParsed++;

                // Parse the line
                Interaction interaction = interactionFromFile(content);
                if (interaction.getInteractorA().getIntactId().equals("-") || interaction.getInteractorB().getIntactId().equals("-")) {
                    totalIgnoredLines++;
                    continue;
                }

                // Only persist in the Database those interactions with score higher than InteractorConstant.MINIMUM_VALID_SCORE
                if (interaction.getIntactScore() >= InteractorConstant.MINIMUM_VALID_SCORE) {

                    interactionList.add(interaction);
                    totalLinesIncluded++;

                    // Go to the database every 1000 interactions
                    if ((totalLinesIncluded % 1000) == 0) {
                        logger.info("Performing a DB save. Rows parsed [{}]", totalLinesIncluded);
                        try {
                            interactionParserService.save(interactionList);
                            interactionList.clear();
                        } catch (SQLException e) {
                            logger.error("Exception thrown during DB save: ", e);
                            dbErrorMessages.add("Error inserting interactions to the Database." + e.getMessage());
                        }
                    }
                }
            }

            // Persist remaining items
            try {
                logger.info("Performing a DB save. Rows parsed [{}]", totalLinesIncluded);

                interactionParserService.save(interactionList);
                interactionList.clear();
            } catch (SQLException e) {
                logger.error("Exception thrown during DB save: ", e);
                dbErrorMessages.add("Error inserting interactions to the Database." + e.getMessage());
            }

            br.close();

        } catch (IOException ex) {
            logger.error("Can't get the IntAct file. Please check the logs.", ex);
        } catch (Exception ex) {
            logger.error("Got a generic exception. Please check the logs.", ex);
        } finally {
            if (parserErrorMessages.size() > 0) {
                logger.info("There are [{}] error messages in the IntAct file.", parserErrorMessages.size());
                writeOutputFile(parserErrorMessages, "parser-messages.txt");
            }

            if (dbErrorMessages.size() > 0) {
                logger.info("There are [{}] db error messages.", dbErrorMessages.size());
                writeOutputFile(dbErrorMessages, "db-error-messages.txt");
            }
        }

        logger.info("The IntAct parser has finished. [{}] rows have been read. [{}] have been considered as Interaction based on [{}] minimum score. [{}] ignored rows in absence of A or B", totalLinesParsed, totalLinesIncluded, InteractorConstant.MINIMUM_VALID_SCORE, totalIgnoredLines);
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

        } catch (IOException e) {
            logger.error("Can't create the output file.", e);
        }
    }

    /**
     * A given interactorA and interactorB can have a list of different InteractionID.
     */
    private Interaction interactionFromFile(String[] line) {

        Interactor interactorA = new Interactor();

        Interactor interactorB = new Interactor();

        /*
         * Even though identifiers from multiple databases can be separated by "|", it is recommended to give only one
         * identifier in this column. It is recommended that proteins be identified by stable identifiers such as their
         * UniProtKB or RefSeq accession number. Small molecules should have Chebi identifiers, nucleic acids should
         * have embl/ddbj/genbank identifiers and gene should have entrez gene/locuslink, ensembl, or ensemblGenome
         * identifiers. This column should never be empty ('-') except for describing intra-molecular interactions or
         * auto-catalysis. Ex: uniprotkb:P12346
         */
        /* sample of ID Interactor A => intact:EBI-7121510 */
        parseIntactId(line[ParserIndex.ID_INTERACTOR_A.value], interactorA);


        // sample of Alt. ID(s) interactor A => uniprotkb:Q1231
        parseAlternativeIds(line[ParserIndex.ALTERNATIVE_INTERACTOR_A.value], interactorA);

        // gene/ewas name
        parseAliases(line[ParserIndex.ALIAS_INTERACTOR_A.value], interactorA);

        // synonyms
        parseSynonyms(line[ParserIndex.ALIAS_INTERACTOR_A.value], interactorA);

        // taxid:9606(human)
        parseTaxonomy(line[ParserIndex.TAXID_INTERACTOR_A.value], interactorA);

        // sample of ID Interactor B => intact:EBI-7121510
        parseIntactId(line[ParserIndex.ID_INTERACTOR_B.value], interactorB);

        // sample of Alt. ID(s) interactor B =>  uniprotkb:Q15301
        parseAlternativeIds(line[ParserIndex.ALTERNATIVE_INTERACTOR_B.value], interactorB);

        // gene/ewas name
        parseAliases(line[ParserIndex.ALIAS_INTERACTOR_B.value], interactorB);

        // synonyms
        parseSynonyms(line[ParserIndex.ALIAS_INTERACTOR_B.value], interactorB);

        // taxid:9606(human)
        parseTaxonomy(line[ParserIndex.TAXID_INTERACTOR_B.value], interactorB);

        Interaction interaction = prepareInteractions(line, interactorA, interactorB);
        if (interactions.contains(interaction)) {
            String msg = "A Duplicate entry has been found: " + line[0] + " " + line[1] + " " + line[2] + " " + line[3] + " " + interaction.getIntactScore();
            logger.info(msg);
            parserErrorMessages.add(msg);
        } else {
            interactions.add(interaction);
        }

        // Create interaction
        return interaction;

    }

    private void parseTaxonomy(String value, Interactor interactor) {
        if (!value.equals("-")) { // not null
            // Split at : taxid:9606
            String[] taxid = value.split(":");

            Matcher m = pattern.matcher(taxid[1]);

            if (m.find()) {
                interactor.setTaxid(parseInt(m.group(1)));
            }

        } else {
            interactor.setTaxid(-1);
            parserErrorMessages.add("Interactor ID [" + interactor.getAcc() + "] - Does not have taxId.");
        }
    }

    private Interaction prepareInteractions(String[] line, Interactor interactorA, Interactor interactorB) {
        Interaction interaction = new Interaction();

        interaction.setInteractorA(interactorA);
        interaction.setInteractorB(interactorB);

        Long intactResourceId = interactionResourceMap.get(InteractorConstant.STATIC).getId();
        interaction.setInteractionResourceId(intactResourceId);

        parseConfidenceValue(line[ParserIndex.CONFIDENCE_VALUE.value], interaction);

        parsePubmedIdentifier(line[ParserIndex.PUBMED_IDENTIFIER.value], interaction);

        // Get interaction ID column
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
     */
    private void parseAlternativeIds(String value, Interactor interactor) {
        if (value.equals("-")) { // In this case the alternative identifier is ONLY a dash "-"
            // In case alternative ID is null we will set the IntAct id as the accession. This is done in the IntactPortal
            interactor.setAcc("IntAct:" + getRawIdentifier(interactor.getIntactId()));

            InteractorResource interactorResource = interactorResourceMap.get("IntAct".toLowerCase());
            if (interactorResource != null) {
                interactor.setInteractorResourceId(interactorResource.getId());
            }

            parserErrorMessages.add("Interactor ID [" + interactor.getAcc() + "] - Interactor alternative ID(s) are null.");
        } else {
            // Considering only the first split. Otherwise ChEBI id breaks the split. e.g chebi:CHEBI:23423
            String[] alternativeId = value.split(":", 2);
            String databaseName = getDatabaseName(alternativeId[0]);
            interactor.setAcc(databaseName + ":" + getRawIdentifier(alternativeId[1]));

            // Set interactor resource id
            InteractorResource interactorResource = interactorResourceMap.get(databaseName.toLowerCase());
            if (interactorResource != null) {
                interactor.setInteractorResourceId(interactorResource.getId());
            }
        }
    }

    /**
     * Parse IntAct ID A and B
     * In cases where A or B is a dash (-), just skip it.
     */
    private void parseIntactId(String value, Interactor interactor) {
        if (!value.equals("-")) { // not null
            String[] intactId = value.split(":");
            interactor.setIntactId(intactId[1]);
        } else {
            // In case IntAct ID is - it means the molecule interacts/modify itself
            interactor.setIntactId(value);
        }
    }

    /**
     * Parsing the Aliases A and B for the identifiers. Separated by "|".
     */
    private void parseAliases(String value, Interactor interactor) {
        if (!value.equals("-")) { // not null
            String[] allAliases = value.split("\\|");
            for (String uniqueAlias : allAliases) {
                // databaseName:value
                String[] alias = uniqueAlias.split(":");

                // If alternatives IDs are null, try to figure the resource out in the alias
                if (interactor.getInteractorResourceId() == 0) {
                    String resourceName = getDatabaseName(alias[0]);
                    InteractorResource interactorResource = interactorResourceMap.get(resourceName.toLowerCase());
                    if (interactorResource != null) {
                        interactor.setInteractorResourceId(interactorResource.getId());
                    }
                }

                // first occurrence of psi-mi should be taken as the alias
                // however there cases where the first in the alias is the accession.
                if (alias[0].equalsIgnoreCase(PSI_MI_LABEL) && interactor.getAlias() == null && !getRawIdentifier(interactor.getAcc()).equalsIgnoreCase(alias[1])) {
                    interactor.setAlias(alias[1]);
                }
            }
        }

        // Some cases like EBI-7121639 there is no resource
        if (interactor.getInteractorResourceId() == 0) {
            parserErrorMessages.add("The Interactor ID [" + interactor.getIntactId() + "] does not have alternate identifiers. Can't get Resource.");
            InteractorResource interactorResource = interactorResourceMap.get("IntAct".toLowerCase());
            if (interactorResource != null) {
                interactor.setInteractorResourceId(interactorResource.getId());
            }
        }
    }

    private void parseSynonyms(String value, Interactor interactor) {
        if (!value.equals("-")) { // not null
            // Now the alias has also | on it and make invalid the following split.
            String[] allAliases = value.split("\\|(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            Set<String> uniqueSynonyms = new LinkedHashSet<>();
            for (String uniqueAlias : allAliases) {
                // databaseName:value
                String[] alias = uniqueAlias.split(":", 2);
                // Saving all the alias in the same column. We don't query by alias, so it is ok.
                // We can't save it as CSV, otherwise when splitting the list it will split alias that has comma.
                try {
                    String synonym = alias[1];
                    // at this point, accession has the databaseName, ignoring the aliases that are equals to accession
                    if (!synonym.equalsIgnoreCase(getRawIdentifier(interactor.getAcc())) && !synonym.equalsIgnoreCase(interactor.getAlias())) {
                        uniqueSynonyms.add(synonym);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }

            String synonyms = String.join("$", uniqueSynonyms);
            interactor.setSynonyms(synonyms);
            uniqueSynonyms.clear();
        }
    }

    private void parseConfidenceValue(String value, Interaction interaction) {
        if (!value.equals("-")) { // not null
            String[] alternativeIdsRaw = value.split("\\|");
            for (String alternativeIds : alternativeIdsRaw) {
                String[] alternativeId = alternativeIds.split(":");
                if (alternativeId[0].equalsIgnoreCase(AUTHOR_SCORE_LABEL)) {
                    if (Toolbox.isNumeric(alternativeId[1])) {
                        interaction.setAuthorScore(parseDouble(alternativeId[1]));
                    }
                }
                if (alternativeId[0].equalsIgnoreCase(INTACT_SCORE_LABEL)) {
                    if (Toolbox.isNumeric(alternativeId[1])) {
                        interaction.setIntactScore(valueOf(alternativeId[1]));
                    } else {
                        parserErrorMessages.add("Interactor A [" + interaction.getInteractorA().getIntactId() + "] - Interactor B [" + interaction.getInteractorB().getIntactId() + "] - The intact-miscore is not a number [" + alternativeId[1] + "]");
                    }
                }
            }
        }
    }

    private void parsePubmedIdentifier(String value, Interaction interaction) {
        if (!value.equals("-")) { // not null
            String[] pubmedIdsRaw = value.split("\\|");
            for (String pubmedIds : pubmedIdsRaw) {
                String[] pubmedId = pubmedIds.split(":");
                if (pubmedId[0].equalsIgnoreCase(PUBMED_LABEL)) {
                    if (Toolbox.isNumeric(pubmedId[1])) {
                        interaction.addPubmedIdentifier(pubmedId[1]);
                    } else {
                        parserErrorMessages.add("Interactor A [" + interaction.getInteractorA().getIntactId() + "] - Interactor B [" + interaction.getInteractorB().getIntactId() + "] - The intact-miscore is not a number [" + pubmedId[1] + "]");
                    }
                }
            }
        }
    }

    /**
     * Download the file from Intact server.
     *
     * @return the file path
     * @throws IOException thrown when there is a problem accessing to the IntAct file
     */
    private String downloadFile(String urlFtp, String directory) throws IOException {
        logger.info("Downloading IntAct File...");
        URL url = new URL(urlFtp);

        File intactFile = new File(directory, "intact-micluster.txt");
        if (intactFile.exists()) {
            if (!intactFile.renameTo(new File(directory, "intact-micluster-" + new Date().toString() + ".txt"))) {
                logger.warn("IntAct Static file has not been renamed properly");
            }
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
            // Load INTERACTOR resources (chebi, uniprot)
            List<InteractorResource> interactorResources = interactorResourceService.getAll();
            for (InteractorResource interactorResource : interactorResources) {
                interactorResourceMap.put(interactorResource.getName().toLowerCase(), interactorResource);
            }
            //  Load INTERACTION resources (intact, ...)
            List<InteractionResource> interactionResources = interactionResourceService.getAll();
            for (InteractionResource interactionResource : interactionResources) {
                interactionResourceMap.put(interactionResource.getName().toLowerCase(), interactionResource);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving resources from database", e);
        }
        logger.info("Caching is Done. InteractionResource [{}] and Interactor Resources[{}]", interactionResourceMap.size(), interactorResourceMap.size());
    }

    private enum ParserIndex {
        ID_INTERACTOR_A(0),
        ID_INTERACTOR_B(1),
        ALTERNATIVE_INTERACTOR_A(2),
        ALTERNATIVE_INTERACTOR_B(3),
        ALIAS_INTERACTOR_A(4),
        ALIAS_INTERACTOR_B(5),
        PUBMED_IDENTIFIER(8),
        TAXID_INTERACTOR_A(9),
        TAXID_INTERACTOR_B(10),
        INTERACTION_IDENTIFIER(13),
        CONFIDENCE_VALUE(14);

        final int value;

        ParserIndex(int value) {
            this.value = value;
        }
    }
}

