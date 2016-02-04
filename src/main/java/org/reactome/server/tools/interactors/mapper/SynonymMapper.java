package org.reactome.server.tools.interactors.mapper;

import java.util.Set;

/**
 * Maps a list of unique synonyms for the given identifier in the JSON
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class SynonymMapper {

    private Set<String> text;

    public Set<String> getText() {
        return text;
    }

    public void setText(Set<String> text) {
        this.text = text;
    }

}
