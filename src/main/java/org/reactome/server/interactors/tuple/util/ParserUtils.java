package org.reactome.server.interactors.tuple.util;

import org.apache.commons.io.IOUtils;
import org.reactome.server.interactors.model.CustomPsicquicResource;
import org.reactome.server.interactors.tuple.exception.ParserException;
import org.reactome.server.interactors.tuple.model.TupleResult;
import org.reactome.server.interactors.tuple.parser.Parser;
import org.reactome.server.interactors.tuple.parser.ParserFactory;
import org.reactome.server.interactors.util.InteractorConstant;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
@SuppressWarnings("unused")
public class ParserUtils {

    public static TupleResult getUserDataContainer(String name, String filename, InputStream is) throws IOException, ParserException {
        List<String> linesList = IOUtils.readLines(is, Charset.defaultCharset());

        TupleResult ret = processData(linesList);

        // set filename and name to be part as the json
        ret.getSummary().setFileName(filename);
        ret.getSummary().setName(name);

        return ret;
    }

    /**
     * Invoke the parser
     */
    private static TupleResult processData(List<String> lines) throws ParserException{
        Parser p = ParserFactory.build(lines);
        return p.parse(lines);

    }

    public static CustomPsicquicResource processCustomPsicquic(String name, String url) throws ParserException {
        //TODO: Validate whether this is a PSICQUIC end or not!
        if(!url.endsWith("/")) {
            url = url + "/";
        }
        int token = name.hashCode() * url.hashCode();
        return new CustomPsicquicResource(name, url, InteractorConstant.TUPLE_PREFIX + token);
    }

}
