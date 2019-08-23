package org.reactome.server.tool.interactors.dao;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.shaded.org.objenesis.strategy.StdInstantiatorStrategy;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.reactome.server.interactors.tuple.exception.ParserException;
import org.reactome.server.interactors.tuple.model.TupleResult;
import org.reactome.server.interactors.tuple.parser.Parser;
import org.reactome.server.interactors.tuple.parser.ParserFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

        List<String> lines = IOUtils.readLines(new FileInputStream(file), Charset.defaultCharset());

        Parser p = ParserFactory.build(lines);
        TupleResult result = p.parse(lines);

        assertEquals("Haven't found six interactions", 6, (int) result.getSummary().getInteractions());
        assertNotNull("Warning messages list is null", result.getWarningMessages());
        assertEquals("Haven't found one warning messages", 1, result.getWarningMessages().size());

        assertEquals("Q9H0R8 should be 2 times", 2, result.getCustomResource().get("Q9H0R8").size());
        assertEquals("Q14596 should be 2 times", 2, result.getCustomResource().get("Q14596").size());
        assertEquals("Q13501 should be 4 times", 4, result.getCustomResource().get("Q13501").size());

        //Testing serialisation
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        Output output = new Output(new ByteArrayOutputStream());
        kryo.writeClassAndObject(output, result);
        output.close();
        Input input = new Input(new ByteArrayInputStream(output.getBuffer()));
        TupleResult aux = (TupleResult) kryo.readClassAndObject(input);
        output.close();

        //Checking whether aux contains what result had
        assertEquals("Q9H0R8 should be 2 times", 2, aux.getCustomResource().get("Q9H0R8").size());
        assertEquals("Q14596 should be 2 times", 2, aux.getCustomResource().get("Q14596").size());
        assertEquals("Q13501 should be 4 times", 4, aux.getCustomResource().get("Q13501").size());
    }

    @Test
    public void testExtendedCsv() throws IOException, ParserException {
        File file = getFileFromResources(EXTENDED_CSV);

        List<String> lines = IOUtils.readLines(new FileInputStream(file), Charset.defaultCharset());

        Parser p = ParserFactory.build(lines);
        TupleResult result = p.parse(lines);

        assertEquals("Haven't found six interactions", 6, (int) result.getSummary().getInteractions());
        assertNotNull("Warning messages list is null", result.getWarningMessages());
        assertEquals("Haven't found one warning messages", 1, result.getWarningMessages().size());

        //Testing serialisation
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        Output output = new Output(new ByteArrayOutputStream());
        kryo.writeClassAndObject(output, result);
        output.close();
        Input input = new Input(new ByteArrayInputStream(output.getBuffer()));
        TupleResult aux = (TupleResult) kryo.readClassAndObject(input);
        output.close();

        //Checking whether aux contains what result had
        assertEquals("Q9H0R8 should be 2 times", 2, aux.getCustomResource().get("Q9H0R8").size());
        assertEquals("Q14596 should be 2 times", 2, aux.getCustomResource().get("Q14596").size());
        assertEquals("Q13501 should be 4 times", 4, aux.getCustomResource().get("Q13501").size());
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
