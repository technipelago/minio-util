package se.technipelago.minio;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.http.uri.UriBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import se.technipelago.minio.cmd.BucketCommand;
import se.technipelago.minio.cmd.ObjectCommand;
import se.technipelago.minio.config.McConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@Testcontainers
public class BucketCommandTests {

    private static File configFile;

    @Container
    private static final GenericContainer minio = new GenericContainer(DockerImageName.parse("minio/minio"))
            .withEnv("MINIO_ACCESS_KEY", "1234567890")
            .withEnv("MINIO_SECRET_KEY", "ABCDEFGHIJKLMNOPQRSTUVWXYZ")
            .withCommand("server /data")
            .withExposedPorts(9000)
            .waitingFor(new HostPortWaitStrategy());

    @BeforeEach
    public void setup() {
        URI address = UriBuilder.of("http://" + minio.getHost() + ":" + minio.getFirstMappedPort()).build();

        final McConfig config = new McConfig();
        config.setAlias("test",
                address,
                (String) minio.getEnvMap().get("MINIO_ACCESS_KEY"),
                (String) minio.getEnvMap().get("MINIO_SECRET_KEY")
        );
        try {
            configFile = File.createTempFile("test", ".json");
            configFile.deleteOnExit();
            config.write(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void cleanup() {
        if (configFile != null) {
            configFile.delete();
            configFile = null;
        }
    }

    @Test
    public void createBucketAndStoreObject() {

        try (ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream out = System.out;
            System.setOut(new PrintStream(baos));

            PicocliRunner.execute(BucketCommand.class, new String[]{"-v", "-c", configFile.getAbsolutePath(), "create", "test/bucket-1"});
            out.println(baos.toString());
            assertTrue(baos.toString().contains("Created bucket bucket-1"));

            baos.reset();
            PicocliRunner.execute(ObjectCommand.class, new String[]{"-v", "-c", configFile.getAbsolutePath(), "cp", configFile.getAbsolutePath(), "test/bucket-1/config.json"});
            out.println(baos.toString());
            assertTrue(baos.toString().contains("Saved object config.json"));

            baos.reset();
            PicocliRunner.execute(BucketCommand.class, new String[]{"-v", "-c", configFile.getAbsolutePath(), "list", "test/bucket-1"});
            out.println(baos.toString());
            assertTrue(baos.toString().contains("config.json"));

            baos.reset();
            PicocliRunner.execute(BucketCommand.class, new String[]{"-v", "-c", configFile.getAbsolutePath(), "create", "test/bucket-2"});
            out.println(baos.toString());
            assertTrue(baos.toString().contains("Created bucket bucket-2"));

            baos.reset();
            PicocliRunner.execute(ObjectCommand.class, new String[]{"-v", "-c", configFile.getAbsolutePath(), "cp", configFile.getAbsolutePath(), "test/bucket-2/config.json"});
            out.println(baos.toString());
            assertTrue(baos.toString().contains("Saved object config.json"));

            baos.reset();
            PicocliRunner.execute(BucketCommand.class, new String[]{"-v", "-c", configFile.getAbsolutePath(), "diff", "test/bucket-1", "test/bucket-2"});
            out.println(baos.toString());
            assertTrue(baos.toString().contains("Comparing objects in bucket bucket-1 and bucket-2"));

            baos.reset();
            PicocliRunner.execute(BucketCommand.class, new String[]{"-v", "-c", configFile.getAbsolutePath(), "verify", "test/bucket-1", "test/bucket-2"});
            out.println(baos.toString());
            assertTrue(baos.toString().contains("Verifying objects in bucket bucket-1 and bucket-2"));

            baos.reset();
            System.setOut(new PrintStream(baos));
            PicocliRunner.execute(ObjectCommand.class, new String[]{"-v", "-c", configFile.getAbsolutePath(), "cp", configFile.getAbsolutePath(), "test/bucket-1/config.json.bak"});
            out.println(baos.toString());
            assertTrue(baos.toString().contains("Saved object config.json"));

            baos.reset();
            PicocliRunner.execute(BucketCommand.class, new String[]{"-v", "-c", configFile.getAbsolutePath(), "diff", "test/bucket-1", "test/bucket-2"});
            out.println(baos.toString());
            assertTrue(baos.toString().contains("Comparing objects in bucket bucket-1 and bucket-2"));

            baos.reset();
            PicocliRunner.execute(BucketCommand.class, new String[]{"-c", configFile.getAbsolutePath(), "verify", "test/bucket-1", "test/bucket-2"});
            out.println(baos.toString());
            assertTrue(baos.toString().contains("NoSuchKey: config.json.bak"));
        }
    }
}
