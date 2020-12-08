package se.technipelago.minio.cmd.alias;

import picocli.CommandLine;
import se.technipelago.minio.cmd.SubCommand;
import se.technipelago.minio.config.McConfig;
import se.technipelago.minio.config.McConfigAlias;

import java.util.Map;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@CommandLine.Command(name = "list", description = "List all configured aliases")
public class ListAliasCommand extends SubCommand<AliasCommand> implements Runnable {

    @CommandLine.ParentCommand
    protected AliasCommand parent;

    @Override
    public AliasCommand getParent() {
        return parent;
    }

    @Override
    public void run() {
        McConfig config = readConfiguration();
        Map<String, McConfigAlias> aliases = config.getAliases();
        if (aliases != null) {
            aliases.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> System.out.println(entry.getKey() + "\t" + entry.getValue().getUrl()));
        }
    }
}
