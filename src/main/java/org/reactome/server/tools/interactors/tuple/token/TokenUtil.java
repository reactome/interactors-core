package org.reactome.server.tools.interactors.tuple.token;

import java.util.UUID;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TokenUtil {

    public static String generateToken(){
        return UUID.randomUUID().toString();
    }

}
