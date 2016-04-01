package org.reactome.server.tools.interactors.tuple.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public final class CustomInteractorRepository {
    private static Map<String, UserDataContainer> data = new HashMap<>();

    /**
     * Save token and user data
     * @param token unique id for the submission
     * @param userDataContainer data submitted by user
     */
    public static void save(String token, UserDataContainer userDataContainer){
        data.put(token, userDataContainer);
    }

    /**
     * Return all tokens in a Set of string
     * @return all tokens represented by String.
     */
    public static Set<String> getKeys(){
        return data.keySet();
    }

    /**
     * Retrieve everything in the repository
     * @return the whole map
     */
    public static Map<String, UserDataContainer> getAll(){
        return data;
    }

    /**
     * Retrieve data associated with a token
     */
    public static UserDataContainer getByToken(String token){
        return data.get(token);
    }
}
