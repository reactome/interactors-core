package org.reactome.server.tools.interactors.tuple.parser;

import org.reactome.server.tools.interactors.tuple.exception.ParserException;
import org.reactome.server.tools.interactors.tuple.model.Summary;

import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface Parser {

    /**
     *
     * @param input lines
     * @throws ParserException
     */
    Summary parse(List<String> input) throws ParserException;

//    Summary parse(InputStream is) throws ParserException;


}
