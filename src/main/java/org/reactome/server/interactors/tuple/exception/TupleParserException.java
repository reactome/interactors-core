package org.reactome.server.interactors.tuple.exception;

import java.util.List;

/**
 * @author Guilherme Viteri <gviteri@ebi.ac.uk>
 */
public class TupleParserException extends ParserException {

    private List<String> errorMessages;

    public TupleParserException(String message, List<String> errorMessages){
        super(message);
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
