package org.reactome.server.interactors.tuple.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.reactome.server.interactors.tuple.custom.CustomResource;

import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TupleResult {

    private Summary summary;

    private CustomResource customResource;

    private List<String> warningMessages;

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    @JsonIgnore
    public CustomResource getCustomResource() {
        return customResource;
    }

    public void setCustomResource(CustomResource customResource) {
        this.customResource = customResource;
    }

    public List<String> getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(List<String> warningMessages) {
        this.warningMessages = warningMessages;
    }

    @Override
    public String toString() {
        return "TupleResult{" +
                "summary=" + summary +
                ", warningMessages=" + warningMessages +
                '}';
    }
}
