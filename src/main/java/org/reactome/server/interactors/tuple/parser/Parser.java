package org.reactome.server.interactors.tuple.parser;

import org.reactome.server.interactors.tuple.exception.ParserException;
import org.reactome.server.interactors.tuple.model.TupleResult;

import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public interface Parser {

    /**
     * @param input lines
     * @throws ParserException
     */
    TupleResult parse(List<String> input) throws ParserException;

}
