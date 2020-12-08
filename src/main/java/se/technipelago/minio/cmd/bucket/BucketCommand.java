package se.technipelago.minio.cmd.bucket;

import picocli.CommandLine;
import se.technipelago.minio.cmd.MainCommand;
import se.technipelago.minio.cmd.SubCommand;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@CommandLine.Command(name = "bucket", description = "Bucket operations", mixinStandardHelpOptions = true,
        subcommands = {BucketCreateCommand.class, BucketListCommand.class, BucketDiffCommand.class})
public class BucketCommand extends SubCommand<MainCommand> {

    @CommandLine.ParentCommand
    protected MainCommand parent;

    @Override
    public MainCommand getParent() {
        return parent;
    }
}
