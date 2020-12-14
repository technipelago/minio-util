package se.technipelago.minio.cmd;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import picocli.CommandLine;
import se.technipelago.minio.cmd.alias.AliasCommand;
import se.technipelago.minio.cmd.bucket.BucketCommand;
import se.technipelago.minio.cmd.object.ObjectCommand;
import se.technipelago.minio.config.McConfig;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@CommandLine.Command(name = "mu", description = "Minio util", version = "1.0.0.BUILD-SNAPSHOT",
        mixinStandardHelpOptions = true, subcommands = {AliasCommand.class, BucketCommand.class, ObjectCommand.class})
public class MainCommand implements MinioCommand<MinioCommand<?>>, Runnable {

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Show verbose output")
    protected boolean verbose;

    @CommandLine.Option(names = {"-c", "--config"}, description = "Path to mc config file")
    protected String config = System.getProperty("user.home") + File.separator + ".mc/config.json";

    @CommandLine.Option(names = {"-r", "--region"}, description = "S3 region")
    protected String region = "localhost";

    @Inject
    protected ObjectMapper objectMapper;

    @CommandLine.Spec
    protected CommandLine.Model.CommandSpec spec;

    public static void main(String... args) {
        try (ApplicationContext ctx = ApplicationContext.run(Environment.CLI)) {
            PicocliRunner.run(MainCommand.class, ctx, args);
        }
    }

    @Override
    public void run() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand");
    }

    @Override
    public MinioCommand getParent() {
        return null;
    }

    @Override
    public boolean isVerbose() {
        return verbose;
    }

    @Override
    public String getRegion() {
        return region;
    }

    @Override
    public McConfig readConfiguration() {
        File configFile = getConfigFile();
        if (configFile.exists() && configFile.canRead()) {
            try {
                return objectMapper.readValue(configFile, McConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Unable to read mc config file " + configFile.getAbsolutePath(), e);
            }
        }
        return new McConfig();
    }

    @Override
    public void writeConfiguration(McConfig item) {
        File configFile = getConfigFile();
        try {
            File parent = configFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IOException("Unable to create directory: " + parent);
            }
            objectMapper.writeValue(configFile, item);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getConfigFile() {
        return new File(config);
    }
}
