package org.reactome.server.tool.interactors.dao;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.reactome.server.tools.interactors.tuple.exception.ParserException;
import org.reactome.server.tools.interactors.tuple.model.TupleResult;
import org.reactome.server.tools.interactors.tuple.parser.Parser;
import org.reactome.server.tools.interactors.tuple.parser.ParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class TestParser {

    private static final String PATH = "tuple.samples/";
    private static final String EXTENDED_TXT = PATH.concat("extended.txt");
    private static final String EXTENDED_CSV = PATH.concat("extended.csv");


    @Test
    public void testExtendedTxt() throws IOException, ParserException {
        File file = getFileFromResources(EXTENDED_TXT);

        List<String> lines = IOUtils.readLines(new FileInputStream(file));

        Parser p = ParserFactory.build(lines);
        TupleResult result = p.parse(lines);

        Assert.assertTrue("Haven't found six interactors", result.getSummary().getInteractions() == 6);
        Assert.assertNotNull("Warning messages list is null", result.getWarningMessages());
        Assert.assertTrue("Haven't found two warning messages", result.getWarningMessages().size() == 2);

    }

    @Test
    public void testExtendedCsv() throws IOException, ParserException {
        File file = getFileFromResources(EXTENDED_CSV);

        List<String> lines = IOUtils.readLines(new FileInputStream(file));

        Parser p = ParserFactory.build(lines);
        TupleResult result = p.parse(lines);

        Assert.assertTrue("Haven't found six interactors", result.getSummary().getInteractions() == 6);
        Assert.assertNotNull("Warning messages list is null", result.getWarningMessages());
        Assert.assertTrue("Haven't found two warning messages", result.getWarningMessages().size() == 2);
    }

    // TODO: create more test cases

    private File getFileFromResources(String fileName) {
        String msg = "Can't get an instance of ".concat(fileName);

        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader == null) {
            Assert.fail("[1] - ".concat(msg));
        }

        URL url = classLoader.getResource(fileName);
        if (url == null) {
            Assert.fail("[2] - ".concat(msg));
        }

        File file = new File(url.getFile());
        if (!file.exists()) {
            Assert.fail("[3] - ".concat(msg));
        }

        return file;
    }

}
