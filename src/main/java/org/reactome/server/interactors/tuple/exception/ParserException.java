package org.reactome.server.interactors.tuple.exception;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class ParserException extends Exception {

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
