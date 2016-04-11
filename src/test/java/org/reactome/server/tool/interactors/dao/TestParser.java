package org.reactome.server.tool.interactors.dao;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.shaded.org.objenesis.strategy.StdInstantiatorStrategy;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.reactome.server.tools.interactors.tuple.exception.ParserException;
import org.reactome.server.tools.interactors.tuple.model.CustomInteraction;
import org.reactome.server.tools.interactors.tuple.model.TupleResult;
import org.reactome.server.tools.interactors.tuple.parser.Parser;
import org.reactome.server.tools.interactors.tuple.parser.ParserFactory;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Set;

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

        Assert.assertTrue("Haven't found six interactions", result.getSummary().getInteractions() == 6);
        Assert.assertNotNull("Warning messages list is null", result.getWarningMessages());
        Assert.assertTrue("Haven't found one warning messages", result.getWarningMessages().size() == 1);

        Assert.assertTrue("Q9H0R8 should be 2 times", result.getCustomResource().get("Q9H0R8").size() == 2);
        Assert.assertTrue("Q14596 should be 2 times", result.getCustomResource().get("Q14596").size() == 2);
        Assert.assertTrue("Q13501 should be 4 times", result.getCustomResource().get("Q13501").size() == 4);

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
        Assert.assertTrue("Q9H0R8 should be 2 times", aux.getCustomResource().get("Q9H0R8").size() == 2);
        Assert.assertTrue("Q14596 should be 2 times", aux.getCustomResource().get("Q14596").size() == 2);
        Assert.assertTrue("Q13501 should be 4 times", aux.getCustomResource().get("Q13501").size() == 4);
    }

    private static Object read(InputStream file) {
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        Input input = new Input(file);
        Object obj = kryo.readClassAndObject(input);
        input.close();
        return obj;
    }

    @Test
    public void testExtendedCsv() throws IOException, ParserException {
        File file = getFileFromResources(EXTENDED_CSV);

        List<String> lines = IOUtils.readLines(new FileInputStream(file));

        Parser p = ParserFactory.build(lines);
        TupleResult result = p.parse(lines);

        Assert.assertTrue("Haven't found six interactions", result.getSummary().getInteractions() == 6);
        Assert.assertNotNull("Warning messages list is null", result.getWarningMessages());
        Assert.assertTrue("Haven't found one warning messages", result.getWarningMessages().size() == 1);

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
        Assert.assertTrue("Q9H0R8 should be 2 times", aux.getCustomResource().get("Q9H0R8").size() == 2);
        Assert.assertTrue("Q14596 should be 2 times", aux.getCustomResource().get("Q14596").size() == 2);
        Assert.assertTrue("Q13501 should be 4 times", aux.getCustomResource().get("Q13501").size() == 4);
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

    //@Test
    public void testSillyTxt() throws IOException, ParserException {
        File file = getFileFromResources(PATH.concat("silly.txt"));

        List<String> lines = IOUtils.readLines(new FileInputStream(file));

        Parser p = ParserFactory.build(lines);
        TupleResult result = p.parse(lines);

        //Testing serialisation
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        Output output = new Output(new ByteArrayOutputStream());
        kryo.writeClassAndObject(output, result);
        output.close();
        Input input = new Input(new ByteArrayInputStream(output.getBuffer()));
        TupleResult aux = (TupleResult) kryo.readClassAndObject(input);
        output.close();


        Set<CustomInteraction> sssss = aux.getCustomResource().get("Q13501");

        //Checking whether aux contains what result had
        Assert.assertTrue("Q9H0R8 should be 2 times", aux.getCustomResource().get("Q9H0R8").size() == 2);
        Assert.assertTrue("Q14596 should be 2 times", aux.getCustomResource().get("Q14596").size() == 2);
        Assert.assertTrue("Q13501 should be 4 times", aux.getCustomResource().get("Q13501").size() == 4);
    }

}
