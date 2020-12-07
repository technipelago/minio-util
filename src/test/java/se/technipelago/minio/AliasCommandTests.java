package se.technipelago.minio;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.technipelago.minio.cmd.AliasCommand;
import se.technipelago.minio.config.McConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
public class AliasCommandTests {

    private static File configFile;

    @BeforeAll
    public static void setup() throws IOException {
        final McConfig config = new McConfig();
        configFile = File.createTempFile("test", ".json");
        configFile.deleteOnExit();
        config.write(configFile);
    }

    @AfterAll
    public static void cleanup() {
        if (configFile != null) {
            configFile.delete();
            configFile = null;
        }
    }

    @Test
    public void listWithNoAliasesShouldReturnNothing() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = System.out;
        System.setOut(new PrintStream(baos));

        try (ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)) {
            String[] args = new String[]{"-v", "-c", configFile.getAbsolutePath(), "list"};
            assertEquals(0, PicocliRunner.execute(AliasCommand.class, args));
            assertEquals("", baos.toString());
        }
    }

    @Test
    public void listWithTwoAliasesShouldReturnTwoLines() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = System.out;
        System.setOut(new PrintStream(baos));

        McConfig config = new ObjectMapper().readValue(configFile, McConfig.class);
        config.setAlias("test2", URI.create("http://localhost:9001"), "username", "secret");
        config.setAlias("test1", URI.create("http://localhost:9000"), "admin", "password");
        config.write(configFile);

        try (ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)) {
            String[] args = new String[]{"-v", "-c", configFile.getAbsolutePath(), "list"};
            assertEquals(0, PicocliRunner.execute(AliasCommand.class, args));
            //out.println(baos.toString());
            assertEquals("test1\thttp://localhost:9000\ntest2\thttp://localhost:9001\n", baos.toString());
        }
    }
}
