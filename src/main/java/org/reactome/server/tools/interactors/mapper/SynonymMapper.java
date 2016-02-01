package org.reactome.server.tools.interactors.mapper;

import java.util.List;

/**
 * Maps a list of unique synonyms for the given identifier in the JSON
 *
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class SynonymMapper {

//    private String acc;
    private List<String> text;
    private String imageUrl;

//    public String getAcc() {
//        return acc;
//    }
//
//    public void setAcc(String acc) {
//        this.acc = acc;
//    }


    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
