package org.reactome.server.tools.interactors.tuple.model;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class Summary {

    private String token;

    private Integer interactors;
    private Integer interactions;
    private String fileName;
    private String name;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getInteractors() {
        return interactors;
    }

    public void setInteractors(Integer interactors) {
        this.interactors = interactors;
    }

    public Integer getInteractions() {
        return interactions;
    }

    public void setInteractions(Integer interactions) {
        this.interactions = interactions;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
