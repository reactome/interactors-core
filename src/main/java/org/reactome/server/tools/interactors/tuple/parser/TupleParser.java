package org.reactome.server.tools.interactors.tuple.parser;

import org.apache.commons.lang.StringUtils;
import org.reactome.server.tools.interactors.tuple.custom.CustomResource;
import org.reactome.server.tools.interactors.tuple.exception.TupleParserException;
import org.reactome.server.tools.interactors.tuple.model.CustomInteraction;
import org.reactome.server.tools.interactors.tuple.model.Summary;
import org.reactome.server.tools.interactors.tuple.model.TupleResult;
import org.reactome.server.tools.interactors.tuple.util.FileDefinition;
import org.reactome.server.tools.interactors.util.InteractorConstant;

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import static org.reactome.server.tools.interactors.tuple.parser.response.Response.*;


/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class TupleParser extends CommonParser {

    /**
     * Regex used to split the content of a multiple line file
     **/
    private static final String MULTI_LINE_CONTENT_SPLIT_REGEX = "[\\s,;]+";
    private static final String HEADER_SPLIT_REGEX = "[\\t,;]+";

    /**
     * Regex for parsing the content when we do not have the header, trying to build a default one
     **/
    private static final String NO_HEADER_DEFAULT_REGEX = "[\\s,;]+";

    /**
     * Threshold number for columns, based on the first line we count columns.
     * All the following lines must match this threshold.
     */
    private static Integer thresholdColumn = 0;

    /**
     * Ignoring the initial blank lines and start parsing from the
     * first valid line.
     */
    private int startOnLine = 0;
    private boolean hasHeader = false;

    @Override
    public TupleResult parse(List<String> input) throws TupleParserException {
        CustomResource customResource = new CustomResource();

        long start = System.currentTimeMillis();

        if (input.size() == 0) {
            errorResponses.add(getMessage(EMPTY_FILE));
        } else {

            analyseHeaderColumns(input);

            // Prepare content
            analyseContent(input, customResource);
        }

        long end = System.currentTimeMillis();
        logger.debug("Elapsed Time Parsing the data: " + (end - start) + ".ms");

        if (hasError()) {
            logger.error("Error parsing your interactors overlay");
            throw new TupleParserException("Error parsing your interactors overlay", errorResponses);
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

    /**
     * Analyse header based on the first line.
     * Headers must start with # or //
     *
     * @param data is the file lines
     */
    private void analyseHeaderColumns(List<String> data) {
        String headerLine = "";

        for (int end = 0; end < data.size(); ++end) {
            if (StringUtils.isNotEmpty(data.get(end))) {
                headerLine = data.get(end);
                this.startOnLine = end;
                break;
            }
        }

        if (hasHeaderLine(headerLine)) {
            this.getHeaderLabel(headerLine);
            this.hasHeader = true;
        } else {
            warningResponses.add(getMessage(NO_HEADER_WARNING));

            /**
             * We cannot use the HEADER REGEX for parsing the header and prepare the default.
             * Why? Tab is a delimiter for header, but space isn't. Colon is a delimiter fo the content
             * but not for the header.
             **/
            int columns = 0;
            for (String line : data) {
                if (StringUtils.isNotEmpty(line)) {
                    String[] firstLine = line.split(NO_HEADER_DEFAULT_REGEX);
                    columns = firstLine.length;
                    break;
                }
            }

            buildDefaultHeader(columns);
        }
    }

    /**
     * Get header labels and also define a standard pattern in the column length
     *
     * @param line The line to be analysed as a header
     */
    private void getHeaderLabel(String line) {
        // remove chars which categorizes a comment.
        line = line.replaceAll("^(#|//)", "");

        // Split header line by our known delimiters
        String[] cols = line.split(HEADER_SPLIT_REGEX);

        thresholdColumn = cols.length;
    }

    /**
     * The default header will be built based on the first line.
     */
    private void buildDefaultHeader(Integer colsLength) {
        thresholdColumn = colsLength;
    }

    /**
     * Analyse all the data itself.
     * Replace any character like space, comma, semicolon, tab into a space and then replace split by space.
     */
    private void analyseContent(List<String> content, CustomResource customResource) {
        if (hasHeader) {
            startOnLine += 1;
        }

        /** Note also that using + instead of * avoids replacing empty strings and therefore might also speed up the process. **/
        String regexp = MULTI_LINE_CONTENT_SPLIT_REGEX;

        Pattern p = Pattern.compile(regexp);

        for (int i = startOnLine; i < content.size(); ++i) {

            String line = content.get(i);
            if (line.isEmpty()) {
                warningResponses.add(getMessage(EMPTY_LINE, i + 1));
                // maybe here we can just ignore empty lines instead of report them
                continue;
            }

            /** Note that using String.replaceAll() will compile the regular expression each time you call it. **/
            line = p.matcher(line).replaceAll(" "); // slow slow slow

            //StringTokenizer has more performance to offer than String.slit.
//            StringTokenizer st = new StringTokenizer(line); //space is default delimiter.
            String[] values = line.split("\\s+");

            if (values.length > 0) {
                if (values.length == thresholdColumn) {
                    CustomInteraction customInteraction = new CustomInteraction();

                    customInteraction.setInteractorIdA(values[CustomInteraction.CustomInteractionColumn.ID_INTERACTOR_A.ordinal()]);
                    customInteraction.setInteractorIdB(values[CustomInteraction.CustomInteractionColumn.ID_INTERACTOR_B.ordinal()]);
                    customInteraction.setConfidenceValue(InteractorConstant.TUPLE_DEFAULT_SCORE);

                    if (customResource.checkForDuplicates(customInteraction)) {
                        warningResponses.add(getMessage(DUPLICATE_AB, (i + 1), customInteraction.getInteractorIdA(), customInteraction.getInteractorIdB()));
                    } else {
                        customResource.add(customInteraction);
                    }
                } else {
                    //errorResponses.add(Response.getMessage(Response.COLUMN_MISMATCH));
                    errorResponses.add(getMessage(COLUMN_MISMATCH, i + 1, thresholdColumn, values.length));
                }
            }
        }
    }

    @Override
    public FileDefinition getParserDefinition(List<String> lines) {
        int attempts = 0;

        /** Note also that using + instead of * avoids replacing empty strings and therefore might also speed up the process. **/
        String regexp = MULTI_LINE_CONTENT_SPLIT_REGEX;

        Pattern p = Pattern.compile(regexp);

        int right = 0;
        int wrong = 0;

        for (String line : lines) {

            if (StringUtils.isEmpty(line)) {
                continue;
            }

            /** Note that using String.replaceAll() will compile the regular expression each time you call it. **/
            line = p.matcher(line).replaceAll(" "); // slow slow slow

            //StringTokenizer has more performance to offer than String.slit.
            StringTokenizer st = new StringTokenizer(line); //space is default delimiter.
            int tokens = st.countTokens();

            // it is not a tuple... stop it and return null;
            if (tokens == 2) {
                right++;
            } else {
                wrong++;
            }

            attempts++;
            if (attempts == 50) {
                break;
                // don't need to iterate through all the file
            }
        }

        if (right > wrong) {
            return FileDefinition.REDUCED_DATA;
        }

        return null;
    }
}
