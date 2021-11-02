package org.reactome.server.tool.interactors;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.googlecode.concurrenttrees.radix.node.util.AtomicReferenceArrayListAdapter;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.reactome.server.interactors.tuple.exception.ParserException;
import org.reactome.server.interactors.tuple.model.TupleResult;
import org.reactome.server.interactors.tuple.parser.Parser;
import org.reactome.server.interactors.tuple.parser.ParserFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Guilherme S Viteri <gviteri@ebi.ac.uk>
 */
public class ParserTest {

    private static final String PATH = "tuple.samples/";
    private static final String EXTENDED_TXT = PATH.concat("extended.txt");
    private static final String EXTENDED_CSV = PATH.concat("extended.csv");


    @Test
    public void testExtendedTxt() throws IOException, ParserException {
        File file = getFileFromResources(EXTENDED_TXT);

        List<String> lines = IOUtils.readLines(new FileInputStream(file), Charset.defaultCharset());

        Parser p = ParserFactory.build(lines);
        TupleResult result = p.parse(lines);

        Assertions.assertEquals(6, (int) result.getSummary().getInteractions(), "Haven't found six interactions");
        Assertions.assertNotNull(result.getWarningMessages(), "Warning messages list is null");
        Assertions.assertEquals(1, result.getWarningMessages().size(), "Haven't found one warning messages");

        Assertions.assertEquals(2, result.getCustomResource().get("Q9H0R8").size(), "Q9H0R8 should be 2 times");
        Assertions.assertEquals(2, result.getCustomResource().get("Q14596").size(), "Q14596 should be 2 times");
        Assertions.assertEquals(4, result.getCustomResource().get("Q13501").size(), "Q13501 should be 4 times");

        //Testing serialisation
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(AtomicReferenceArrayListAdapter.class, new FieldSerializer<>(kryo, AtomicReferenceArrayListAdapter.class));
        Output output = new Output(new ByteArrayOutputStream());
        kryo.writeClassAndObject(output, result);
        output.close();
        Input input = new Input(new ByteArrayInputStream(output.getBuffer()));
        TupleResult aux = (TupleResult) kryo.readClassAndObject(input);
        output.close();

        //Checking whether aux contains what result had
        Assertions.assertEquals(2, aux.getCustomResource().get("Q9H0R8").size(), "Q9H0R8 should be 2 times");
        Assertions.assertEquals(2, aux.getCustomResource().get("Q14596").size(), "Q14596 should be 2 times");
        Assertions.assertEquals(4, aux.getCustomResource().get("Q13501").size(), "Q13501 should be 4 times");
    }

    @Test
    public void testExtendedCsv() throws IOException, ParserException {
        File file = getFileFromResources(EXTENDED_CSV);

        List<String> lines = IOUtils.readLines(new FileInputStream(file), Charset.defaultCharset());

        Parser p = ParserFactory.build(lines);
        TupleResult result = p.parse(lines);

        Assertions.assertEquals(6, (int) result.getSummary().getInteractions(), "Haven't found six interactions");
        Assertions.assertNotNull(result.getWarningMessages(), "Warning messages list is null");
        Assertions.assertEquals(1, result.getWarningMessages().size(), "Haven't found one warning messages");

        //Testing serialisation
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.register(AtomicReferenceArrayListAdapter.class, new FieldSerializer<>(kryo, AtomicReferenceArrayListAdapter.class));
        Output output = new Output(new ByteArrayOutputStream());
        kryo.writeClassAndObject(output, result);
        output.close();
        Input input = new Input(new ByteArrayInputStream(output.getBuffer()));
        TupleResult aux = (TupleResult) kryo.readClassAndObject(input);
        output.close();

        //Checking whether aux contains what result had
        Assertions.assertEquals(2, aux.getCustomResource().get("Q9H0R8").size(), "Q9H0R8 should be 2 times");
        Assertions.assertEquals(2, aux.getCustomResource().get("Q14596").size(), "Q14596 should be 2 times");
        Assertions.assertEquals(4, aux.getCustomResource().get("Q13501").size(), "Q13501 should be 4 times");
    }

    // TODO: create more test cases

    private File getFileFromResources(String fileName) {
        String msg = "Can't get an instance of ".concat(fileName);

        ClassLoader classLoader = getClass().getClassLoader();
        if (classLoader == null) {
            Assertions.fail("[1] - ".concat(msg));
        }

        URL url = classLoader.getResource(fileName);
        if (url == null) {
            Assertions.fail("[2] - ".concat(msg));
        }

        File file = new File(url.getFile());
        if (!file.exists()) {
            Assertions.fail("[3] - ".concat(msg));
        }

        return file;
    }
}
