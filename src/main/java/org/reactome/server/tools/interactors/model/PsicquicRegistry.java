package org.reactome.server.tools.interactors.model;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class PsicquicRegistry {

    private String name;
    private String soapURL;
    private String restURL;
    private boolean active;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSoapURL() {
        return soapURL;
    }

    public void setSoapURL(String soapURL) {
        this.soapURL = soapURL;
    }

    public String getRestURL() {
        return restURL;
    }

    public void setRestURL(String restURL) {
        this.restURL = restURL;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
