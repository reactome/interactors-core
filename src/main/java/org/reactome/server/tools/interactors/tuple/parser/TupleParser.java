package org.reactome.server.tools.interactors.tuple.parser;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.reactome.server.tools.interactors.tuple.exception.TupleParserException;
import org.reactome.server.tools.interactors.tuple.model.CustomInteraction;
import org.reactome.server.tools.interactors.tuple.model.Summary;
import org.reactome.server.tools.interactors.tuple.model.UserDataContainer;
import org.reactome.server.tools.interactors.tuple.token.TokenUtil;

import java.util.LinkedList;
import java.util.List;
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
     * This is the default header for oneline file and multiple line file
     * Changing here will propagate in both.
     */
    private static final String DEFAULT_IDENTIFIER_HEADER = "Identifier A";
    private static final String DEFAULT_EXPRESSION_HEADER = "Identifier B";

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

    private List<String> headerColumnNames = new LinkedList<>();

    @Override
    public Summary parse(List<String> input) throws TupleParserException {
        UserDataContainer userDataContainer = new UserDataContainer();

        long start = System.currentTimeMillis();

        if (input.size() == 0) {
            errorResponses.add(getMessage(EMPTY_FILE));
        } else {

            analyseHeaderColumns(input);

            // Prepare content
            analyseContent(input, userDataContainer);
        }

        long end = System.currentTimeMillis();
        logger.debug("Elapsed Time Parsing the data: " + (end - start) + ".ms");

        if (hasError()) {
            logger.error("Error parsing your interactors overlay");
            throw new TupleParserException("Error parsing your interactors overlay", errorResponses);
        }


        Summary summary = new Summary(TokenUtil.generateToken(), userDataContainer);

        summary.setErrorMessages(errorResponses);
        summary.setWarningMessages(warningResponses);
        summary.setHeaderColumns(headerColumnNames);
        summary.setNumberOfInteractors(userDataContainer.getCustomInteractions().size());

        return summary;
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

        for (String columnName : cols) {
            headerColumnNames.add(StringEscapeUtils.escapeJava(columnName.trim()));
        }

        //Header must match the columns, then based on the name we can get columns properly

    }

    /**
     * The default header will be built based on the first line.
     */
    private void buildDefaultHeader(Integer colsLength) {
        thresholdColumn = colsLength;

        headerColumnNames.add(DEFAULT_IDENTIFIER_HEADER);
        for (int i = 1; i < colsLength; i++) {
            headerColumnNames.add(DEFAULT_EXPRESSION_HEADER + i);
        }

    }

    /**
     * Analyse all the data itself.
     * Replace any character like space, comma, semicolon, tab into a space and then replace split by space.
     */
    private void analyseContent(List<String> content, UserDataContainer userDataContainer) {
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

                    /** Check if an interaction exists based on AccessionA and AccessionB **/
                    if (userDataContainer.getCustomInteractions() != null && userDataContainer.getCustomInteractions().contains(customInteraction)) {
                        warningResponses.add(getMessage(DUPLICATE_AB, (i + 1), customInteraction.getInteractorIdA(), customInteraction.getInteractorIdB()));
                    }

                    /** Flip a and b and check again if the interactions exists **/
                    customInteraction.flip(customInteraction.getInteractorIdA(), customInteraction.getInteractorIdB());

                    if (userDataContainer.getCustomInteractions() != null && userDataContainer.getCustomInteractions().contains(customInteraction)) {
                        warningResponses.add(getMessage(DUPLICATE_BA, (i + 1), customInteraction.getInteractorIdB(), customInteraction.getInteractorIdA(), customInteraction.getInteractorIdA(), customInteraction.getInteractorIdB()));
                    } else {
                        /** Flip back to original form **/
                        customInteraction.flip(customInteraction.getInteractorIdA(), customInteraction.getInteractorIdB());

                        /** Add to the list **/
                        userDataContainer.addCustomInteraction(customInteraction);
                    }

                } else {
                    //errorResponses.add(Response.getMessage(Response.COLUMN_MISMATCH));
                    errorResponses.add(getMessage(COLUMN_MISMATCH, i + 1, thresholdColumn, values.length));
                }
            }
        }
    }
}
