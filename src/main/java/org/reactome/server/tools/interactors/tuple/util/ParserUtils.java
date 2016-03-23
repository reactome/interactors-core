package org.reactome.server.tools.interactors.tuple.util;

import org.apache.commons.io.IOUtils;
import org.reactome.server.tools.interactors.tuple.exception.ParserException;
import org.reactome.server.tools.interactors.tuple.model.TupleResult;
import org.reactome.server.tools.interactors.tuple.parser.Parser;
import org.reactome.server.tools.interactors.tuple.parser.ParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
@SuppressWarnings("unused")
public class ParserUtils {

    public static TupleResult getUserDataContainer(String name, String filename, InputStream is) throws IOException, ParserException {
        List<String> linesList = IOUtils.readLines(is);

        return processData(linesList);
    }

    private static TupleResult processData(List<String> lines) throws ParserException{
        Parser p = ParserFactory.build(lines);
        return p.parse(lines);

    }

}
