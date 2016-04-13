package org.reactome.server.tools.interactors.exception;

/**
 * Exception thrown when trying to Query PSICQUIC
 * PSICQUIC Registry shows the service is up and running
 * but we can't query this particular server.
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class PsicquicQueryException extends Exception {

    public PsicquicQueryException(Throwable cause) {
        super(cause);
    }
}
