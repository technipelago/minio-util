package se.technipelago.minio.cmd.alias;

import picocli.CommandLine;
import se.technipelago.minio.cmd.SubCommand;
import se.technipelago.minio.config.McConfig;

import java.net.URI;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@CommandLine.Command(name = "add", description = "Add host alias")
public class AddAliasCommand extends SubCommand<AliasCommand> implements Runnable {

    @CommandLine.ParentCommand
    protected AliasCommand parent;
    @CommandLine.Parameters(index = "0", paramLabel = "alias", description = "Host alias")
    private String alias;
    @CommandLine.Parameters(index = "1", paramLabel = "url", description = "Endpoint URL")
    private URI uri;
    @CommandLine.Parameters(index = "2", paramLabel = "accessKey", description = "Access key")
    private String accessKey;
    @CommandLine.Parameters(index = "3", paramLabel = "secretrKey", description = "Secret key")
    private String secretKey;

    @Override
    public AliasCommand getParent() {
        return parent;
    }

    @Override
    public void run() {
        McConfig config = readConfiguration();
        config.setAlias(alias, uri, accessKey, secretKey);
        writeConfiguration(config);
        System.out.println("Alias [" + alias + "] added");
    }
}
