package se.technipelago.minio.cmd;

import com.fasterxml.jackson.databind.ObjectMapper;
import picocli.CommandLine;
import se.technipelago.minio.config.McConfig;

import java.io.File;
import java.io.IOException;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
public abstract class BaseCommand implements Runnable {

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Show verbose output")
    protected boolean verbose;

    @CommandLine.Option(names = {"-c", "--config"}, description = "Path to mc config file")
    protected String config = System.getProperty("user.home") + File.separator + ".mc/config.json";

    @CommandLine.Option(names = {"-r", "--region"}, description = "S3 region")
    protected String region = "localhost";

    public McConfig readConfiguration() {
        File configFile = getConfigFile();
        return configFile.exists() && configFile.canRead() ? McConfig.read(configFile) : new McConfig();
    }

    public void writeConfiguration(McConfig item) {
        File configFile = getConfigFile();
        try {
            configFile.getParentFile().mkdirs();
            new ObjectMapper().writeValue(configFile, item);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getConfigFile() {
        return new File(config);
    }

    public boolean isVerbose() {
        return verbose;
    }

    public String getRegion() {
        return region;
    }
}
