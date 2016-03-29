package org.reactome.server.tools.interactors.tuple.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public final class CustomPsicquicRepository {
    private static Map<String, String> data = new HashMap<>();

    /**
     * Save token and user data
     *
     * @param token unique id for the submission
     * @param url   psicquic service url
     */
    public static void save(String token, String url) {
        data.put(token, url);
    }

    /**
     * Return all tokens in a Set of string
     *
     * @return all tokens represented by String.
     */
    public static Set<String> getKeys() {
        return data.keySet();
    }

    /**
     * Retrieve everything in the repository
     *
     * @return the whole map
     */
    public static Map<String, String> getAll() {
        return data;
    }

    /**
     * Retrieve url associated with a token
     */
    public static String getByToken(String token) {
        return data.get(token);
    }
}
