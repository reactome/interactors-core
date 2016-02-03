package org.reactome.server.tools.interactors.mapper;

import java.util.List;

/**
 * Maps a list of unique synonyms for the given identifier in the JSON
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class SynonymMapper {

    private List<String> text;

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

}
