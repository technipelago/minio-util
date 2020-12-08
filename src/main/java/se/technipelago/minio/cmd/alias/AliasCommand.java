package se.technipelago.minio.cmd.alias;

import picocli.CommandLine;
import se.technipelago.minio.cmd.MainCommand;
import se.technipelago.minio.cmd.SubCommand;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@CommandLine.Command(name = "alias", description = "Handle host aliases", mixinStandardHelpOptions = true,
        subcommands = {ListAliasCommand.class, AddAliasCommand.class})
public class AliasCommand extends SubCommand<MainCommand> {

    @CommandLine.ParentCommand
    protected MainCommand parent;

    @Override
    public MainCommand getParent() {
        return parent;
    }

}
