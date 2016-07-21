package org.reactome.server.interactors.exception;

/**
 * Exception thrown when trying to Query PSICQUIC
 * using an invalid resource name.
 * In the PWB we use the correct list, but in the content service
 * it can be any value.
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class PsicquicResourceNotFoundException extends Exception {

    public PsicquicResourceNotFoundException(String message) {
        super(message);
    }

}
