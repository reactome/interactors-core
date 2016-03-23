package org.reactome.server.tool.interactors.dao;


import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.reactome.server.tools.interactors.tuple.exception.ParserException;
import org.reactome.server.tools.interactors.tuple.exception.TupleParserException;
import org.reactome.server.tools.interactors.tuple.parser.CommonParser;
import org.reactome.server.tools.interactors.tuple.parser.ExtendedParser;
import org.reactome.server.tools.interactors.tuple.parser.Parser;
import org.reactome.server.tools.interactors.tuple.parser.ParserFactory;
import org.reactome.server.tools.interactors.tuple.util.FileDefinition;
import org.reactome.server.tools.interactors.util.Toolbox;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

//import org.reflections.Reflections;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */

public class TestParser {

    public String tupleFile = "/Users/gsviteri/Sites/extended.txt";

    //@Test
    public void testParser() throws IOException, ParserException {
        InputStream is = new FileInputStream(tupleFile);

        List<String> lines = IOUtils.readLines(is);

        IOUtils.toString(is);
        Parser p = ParserFactory.build(lines);

    }

    //@Test
    public void testExtendedParser() throws IOException {
        Parser p = new ExtendedParser();

//        List<String> lines = IOUtils.readLines(new FileInputStream("/Users/gsviteri/Sites/extended2.txt"));
//        List<String> lines = IOUtils.readLines(new FileInputStream("/Users/gsviteri/Sites/extended.csv"));
        List<String> lines = IOUtils.readLines(new FileInputStream("/Users/gsviteri/Sites/extended2.txt"));

        try {
            p.parse(lines);
        } catch (TupleParserException e) {
            System.out.println(e.getErrorMessages());
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void testReadSubClass() throws Exception {
        List<String> lines = IOUtils.readLines(new FileInputStream("/Users/gsviteri/Sites/extended2.txt"));

        Set<Class<? extends CommonParser>> c = Toolbox.getSubTypesOf("org.reactome.server.tools.interactors.tuple.parser", CommonParser.class);

        FileDefinition def = null;
        for (Class aClass : c) {
            CommonParser cp = (CommonParser)aClass.newInstance();
            def = cp.getParserDefinition(lines);
            if (def != null) {
                break;
            }
        }

        // will fail if is null.
        Assert.assertNotNull(def);

    }


}
