package org.reactome.server.tools.interactors.model;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class PsicquicSync {

    private Long id;
    private String psicquicResource;
    private Timestamp timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPsicquicResource() {
        return psicquicResource;
    }

    public void setPsicquicResource(String psicquicResource) {
        this.psicquicResource = psicquicResource;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PsicquicSync{" +
                "id='" + id + '\'' +
                ", psicquicResource=" + psicquicResource +
                ", timestamp=" + timestamp +
                '}';
    }
}
