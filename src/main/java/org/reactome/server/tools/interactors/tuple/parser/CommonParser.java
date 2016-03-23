package org.reactome.server.tools.interactors.tuple.parser;

import org.apache.log4j.Logger;
import org.reactome.server.tools.interactors.tuple.util.FileDefinition;

import java.util.LinkedList;
import java.util.List;

/**
 * This is what is common between our parsers.
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public abstract class CommonParser implements Parser {

    protected static Logger logger = Logger.getLogger(CommonParser.class);

    protected List<String> errorResponses = new LinkedList<>();
    protected List<String> warningResponses = new LinkedList<>();

    protected boolean hasHeaderLine(String line) {
        return line.startsWith("#") || line.startsWith("//");
    }

    /**
     * An easy handy method for determining if the parse succeeded
     *
     * @return true if data is wrong, false otherwise
     */
    protected boolean hasError() {
        return errorResponses.size() >= 1;
    }

    /**
     * Retrieve the parser definition based on the specific implementation.
     * Each implementation of CommonParser has its own mechanism to identify the file definition
     *
     * @param lines is the content to be analysed
     * @return the FileDefinition
     */
    public abstract FileDefinition getParserDefinition(List<String> lines);


}
