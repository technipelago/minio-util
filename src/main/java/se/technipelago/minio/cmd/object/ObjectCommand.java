package se.technipelago.minio.cmd.object;

import picocli.CommandLine;
import se.technipelago.minio.cmd.MainCommand;
import se.technipelago.minio.cmd.SubCommand;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@CommandLine.Command(name = "object", description = "Handle objects", mixinStandardHelpOptions = true,
        subcommands = {ObjectCreateCommand.class})
public class ObjectCommand extends SubCommand<MainCommand> {

    @CommandLine.ParentCommand
    protected MainCommand parent;

    @Override
    public MainCommand getParent() {
        return parent;
    }
}
