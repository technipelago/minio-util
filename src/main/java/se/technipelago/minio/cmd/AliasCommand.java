package se.technipelago.minio.cmd;

import picocli.CommandLine;
import se.technipelago.minio.SubCommand;
import se.technipelago.minio.config.McConfig;
import se.technipelago.minio.config.McConfigAlias;

import java.net.URI;
import java.util.Map;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@CommandLine.Command(name = "alias", description = "Handle host aliases", mixinStandardHelpOptions = true,
        subcommands = {AliasCommand.ListAliasCommand.class, AliasCommand.AddAliasCommand.class})
public class AliasCommand extends BaseCommand {

    @Override
    public void run() {
        System.out.println("The BaseCommand!");
    }

    @CommandLine.Command(name = "list", description = "List all configured aliases")
    public static class ListAliasCommand extends SubCommand<AliasCommand> {

        @CommandLine.ParentCommand
        protected AliasCommand parent;

        public static void main(String... args) {
            int exitCode = new CommandLine(new ListAliasCommand()).execute(args);
            System.exit(exitCode);
        }

        @Override
        protected AliasCommand getParent() {
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

    @CommandLine.Command(name = "add", description = "Add host alias")
    public static class AddAliasCommand extends SubCommand<AliasCommand> {

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

        public static void main(String... args) {
            int exitCode = new CommandLine(new AddAliasCommand()).execute(args);
            System.exit(exitCode);
        }

        @Override
        protected AliasCommand getParent() {
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
}
