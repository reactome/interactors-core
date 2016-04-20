package org.reactome.server.interactors.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.reactome.server.interactors.tuple.model.Summary;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class CustomPsicquicResource {

    private Summary summary;
    private String url;

    public CustomPsicquicResource(String name, String url, String token) {
        this.summary = new Summary();
        this.summary.setName(name);
        this.summary.setToken(token);
        this.url = url;
    }

    public Summary getSummary() {
        return summary;
    }

    @JsonIgnore
    public String getUrl() {
        return url;
    }
}
