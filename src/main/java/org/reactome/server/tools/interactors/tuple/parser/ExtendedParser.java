package org.reactome.server.tools.interactors.tuple.parser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.reactome.server.tools.interactors.tuple.custom.CustomResource;
import org.reactome.server.tools.interactors.tuple.exception.ParserException;
import org.reactome.server.tools.interactors.tuple.exception.TupleParserException;
import org.reactome.server.tools.interactors.tuple.model.ColumnDefinition;
import org.reactome.server.tools.interactors.tuple.model.CustomInteraction;
import org.reactome.server.tools.interactors.tuple.model.Summary;
import org.reactome.server.tools.interactors.tuple.model.TupleResult;
import org.reactome.server.tools.interactors.tuple.parser.response.Response;
import org.reactome.server.tools.interactors.tuple.util.FileDefinition;
import org.reactome.server.tools.interactors.util.InteractorConstant;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static org.reactome.server.tools.interactors.tuple.parser.response.Response.*;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class ExtendedParser extends CommonParser {

    private String headerLine;

    @Override
    public TupleResult parse(List<String> input) throws ParserException {
        /** File clean up **/
        File file = writeContentInTempFile(input);

        /** Store file content **/
        CustomResource customResource = new CustomResource();

        /** Instantiate CsvBeanReader based on Standard Preferences **/
        ICsvBeanReader beanReader = null;
        try {
            beanReader = new CsvBeanReader(
                    new InputStreamReader(new FileInputStream(file)),
                    new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE)
                            .ignoreEmptyLines(true)
                            .surroundingSpacesNeedQuotes(false)
                            .build());

            /** Get our column definition **/
            Map<String, ColumnDefinition> headerColumnMapping = getHeaderMapping();

            /** read the CSV header (and set any unwanted columns to null) **/
            String[] header = beanReader.getHeader(true);
            for (int i = 0; i < header.length; i++) {
                if (headerColumnMapping.containsKey(header[i].toUpperCase())) {
                    header[i] = headerColumnMapping.get(header[i]).attribute;
                } else {
                    header[i] = null;
                }
            }

            setCustomInteractionFromCsvBeanReader(beanReader, header, customResource);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (beanReader != null) {
                    beanReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        file.deleteOnExit();

        if (hasError()) {
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

    private void setCustomInteractionFromCsvBeanReader(ICsvBeanReader beanReader, String[] header, CustomResource customResource) throws IOException {
        CustomInteraction customInteraction;

        try {

            int avoidedByScore = 0;

            /** Read method uses reflection in order to set all CustomInteraction attributes based on header **/
            while ((customInteraction = beanReader.read(CustomInteraction.class, header)) != null) {

                /** Check mandatory fields based on column definition enum **/
                List<String> mandatoryMessages = checkMandatoryAttributes(customInteraction);
                if (mandatoryMessages.size() == 0) {
                    if (customInteraction.getConfidenceValue() < InteractorConstant.MINIMUM_VALID_SCORE) {
                        avoidedByScore++;
                        continue;
                    }

                    if (customResource.checkForDuplicates(customInteraction)) {
                        warningResponses.add(getMessage(DUPLICATE_AB, beanReader.getLineNumber(), customInteraction.getInteractorIdA(), customInteraction.getInteractorIdB()));
                    } else {
                        customResource.add(customInteraction);
                    }

                } else {
                    errorResponses.add(getMessage(MISSING_MANDATORY_FIELDS, beanReader.getLineNumber(), mandatoryMessages));
                }
            }

            if (avoidedByScore > 0) {
                warningResponses.add(getMessage(AVOIDED_SCORE, avoidedByScore, InteractorConstant.MINIMUM_VALID_SCORE));
            }

        } catch (IllegalArgumentException e) {
            /**
             * SuperCSV throws IllegalArgumentException (RuntimeException) when columns do not match.
             * In order to keep parsing, save the errorResponse and invoke the reader again.
             **/
            errorResponses.add(Response.getMessage(Response.COLUMN_MISMATCH, beanReader.getLineNumber(), header.length, beanReader.length()));
            setCustomInteractionFromCsvBeanReader(beanReader, header, customResource);
        }
    }

    private Map<String, ColumnDefinition> getHeaderMapping() {
        /** key, column expected in the file value setter  **/
        Map<String, ColumnDefinition> columnMapping = new HashMap<>();

        columnMapping.put("ID A", ColumnDefinition.ID_A);
        columnMapping.put("ID B", ColumnDefinition.ID_B);
        columnMapping.put("ALIAS A", ColumnDefinition.ALIAS_A);
        columnMapping.put("ALIAS B", ColumnDefinition.ALIAS_B);
        columnMapping.put("TAX_ID A", ColumnDefinition.TAX_ID_A);
        columnMapping.put("TAX_ID B", ColumnDefinition.TAX_ID_B);
        columnMapping.put("EVIDENCE", ColumnDefinition.EVIDENCE);
        columnMapping.put("SCORE", ColumnDefinition.SCORE);

        return columnMapping;
    }

    private List<String> cleanAndConvertToCSV(List<String> input) {
        String header = "";
        int firstLineIndex = 0;
        for (String line : input) {
            firstLineIndex++;
            if (StringUtils.isNotEmpty(line)) {
                header = line.replaceAll("\\t+", ",");
                headerLine = header;
                break;
            }
        }

        List<String> newInput = new ArrayList<>(input.size());
        newInput.add(header.replaceAll("^(#|//)", ""));

        String cleanLine;
        for (int i = firstLineIndex; i < input.size(); i++) {
            String line = input.get(i);
            if (StringUtils.isNotEmpty(line)) {
                cleanLine = line.trim().replaceAll("\\t+", ","); // convert to CSV file
                newInput.add(cleanLine);
            }
        }

        return newInput;
    }

    private File writeContentInTempFile(List<String> input) throws ParserException {
        List<String> newInput = cleanAndConvertToCSV(input);

        if (!hasHeaderLine(headerLine)) {
            errorResponses.add(Response.getMessage(Response.NO_HEADER_ERROR));
            throw new TupleParserException("Error parsing. Header is not present", errorResponses);
        }

        try {
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".txt");
            FileUtils.writeLines(tempFile, newInput);

            return tempFile;

        } catch (IOException e) {
            throw new ParserException("Error parsing your file. ", e);
        }
    }

    @Override
    public FileDefinition getParserDefinition(List<String> lines) {

        int right = 0;
        int wrong = 0;
        int attempts = 0;

        boolean isOK;

        List<String> csvContent = cleanAndConvertToCSV(lines);

        for (String line : csvContent) {
            isOK = true;

            attempts++;
            if (attempts == 50) {
                break; // don't need to iterate through all the file
            }

            String[] values = line.split(",");

            if (values.length <= 2) { // wrong
                wrong++;
                continue;
            }

            for (String value : values) { // wrong
                if (value.contains(":") || value.contains("|")) { // then it is potential PSIMITAB file
                    isOK = false;
                    break;
                }
            }

            if (isOK) {
                right++;
            } else {
                wrong++;
            }
        }

        if (right > wrong) {
            return FileDefinition.EXTENDED_DATA;
        }

        return null;
    }
}
