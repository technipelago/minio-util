package se.technipelago.minio.cmd.bucket;

import io.minio.MinioClient;
import picocli.CommandLine;
import se.technipelago.minio.cmd.AbstractBucketCommand;
import se.technipelago.minio.config.McConfig;
import se.technipelago.minio.config.McConfigAlias;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@CommandLine.Command(name = "create", description = "Create bucket", mixinStandardHelpOptions = true)
public class BucketCreateCommand extends AbstractBucketCommand<BucketCommand> implements Runnable {

    @CommandLine.ParentCommand
    protected BucketCommand parent;
    @CommandLine.Parameters(paramLabel = "bucket", description = "Bucket name")
    private String url;

    @Override
    public BucketCommand getParent() {
        return parent;
    }

    @Override
    public void run() {
        final McConfig config = readConfiguration();
        final String host = getHost(url);
        final String bucket = getBucket(url);
        final McConfigAlias alias = config.getAlias(host).orElseThrow(() -> new RuntimeException("No such host: " + host));
        final MinioClient client = getClient(alias.getUrl(), getRegion(), alias.getAccessKey(), alias.getSecretKey());

        createBucket(client, bucket);

        if (isVerbose()) {
            System.out.println("Created bucket " + bucket);
        }
    }
}
