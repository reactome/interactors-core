package org.reactome.server.tools.interactors.model.psicquic;

import org.reactome.server.tools.interactors.model.psicquic.PsicquicClient;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public abstract class AbstractClient implements PsicquicClient {

    private String resource;

    public AbstractClient(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    // TODO: Maybe this should be in a Toolbox or Util class.
    public boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }

        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
}
