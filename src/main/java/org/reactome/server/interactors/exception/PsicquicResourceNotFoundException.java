package org.reactome.server.interactors.exception;

/**
 * Exception thrown when trying to Query PSICQUIC
 * PSICQUIC Registry shows the service is up and running
 * but we can't query this particular server.
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class PsicquicResourceNotFoundException extends Exception {

    public PsicquicResourceNotFoundException(String message) {
        super(message);
    }

}
