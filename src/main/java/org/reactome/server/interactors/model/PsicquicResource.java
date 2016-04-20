package org.reactome.server.interactors.model;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class PsicquicResource implements Comparable<PsicquicResource> {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PsicquicResource that = (PsicquicResource) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public int compareTo(PsicquicResource o) {
        return name.toLowerCase().compareTo(o.name.toLowerCase());
    }
}
