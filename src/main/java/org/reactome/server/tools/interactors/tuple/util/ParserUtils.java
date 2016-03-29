package org.reactome.server.tools.interactors.tuple.util;

import org.apache.commons.io.IOUtils;
import org.reactome.server.tools.interactors.tuple.exception.ParserException;
import org.reactome.server.tools.interactors.tuple.model.CustomPsicquicRepository;
import org.reactome.server.tools.interactors.tuple.model.Summary;
import org.reactome.server.tools.interactors.tuple.model.TupleResult;
import org.reactome.server.tools.interactors.tuple.parser.Parser;
import org.reactome.server.tools.interactors.tuple.parser.ParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
@SuppressWarnings("unused")
public class ParserUtils {

    public static TupleResult getUserDataContainer(String name, String filename, InputStream is) throws IOException, ParserException {
        List<String> linesList = IOUtils.readLines(is);

        TupleResult ret = processData(linesList);

        /** set filename and name to be part as the json **/
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

    public static TupleResult processCustomPsicquic(String name, String url) throws ParserException {

        TupleResult ret = new TupleResult();

        Summary s = new Summary();
        s.setName(name);
        s.setToken("{PSI}" + UUID.randomUUID().toString());

        ret.setSummary(s);

        if(!url.endsWith("/")) {
            url = url + "/";
        }

        CustomPsicquicRepository.save(s.getToken(), url);

        return ret;
    }

}
