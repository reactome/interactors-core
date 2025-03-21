package org.reactome.server.interactors;


import org.junit.jupiter.api.BeforeAll;
import org.reactome.server.interactors.database.InteractorsDatabase;
import org.reactome.server.interactors.util.InteractorDatabaseGenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;

public class BaseImplTest {
    protected static InteractorsDatabase interactors;
    protected static IntactParser parser;

    @BeforeAll
    public static void beforeAll() {
        Properties prop = new Properties();
        String database;

        try {
            InputStream is = InteractionImplTest.class.getResourceAsStream("/db.properties");
            prop.load(is);
            database = prop.getProperty("database");

            File dbFile = new File(database);
            if (dbFile.exists()) dbFile.delete();

            InteractorDatabaseGenerator.create(new InteractorsDatabase(database).getConnection());

            interactors = new InteractorsDatabase(database);

            // Open database connection.
            parser = new IntactParser(interactors);
            parser.cacheResources();

            parser.parser(BaseImplTest.class.getResource("/tuple.samples/extended.txt").getFile());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
