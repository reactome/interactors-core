package org.reactome.server.tools.interactors.exception;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class InvalidInteractionResourceException extends Exception {

    public InvalidInteractionResourceException() {
        super();
    }

    public InvalidInteractionResourceException(String message) {
        super(message);
    }

    public InvalidInteractionResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInteractionResourceException(Throwable cause) {
        super(cause);
    }

}
