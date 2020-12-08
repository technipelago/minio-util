package se.technipelago.minio.cmd.bucket;

import io.minio.MinioClient;
import io.reactivex.Flowable;
import picocli.CommandLine;
import se.technipelago.minio.cmd.AbstractBucketCommand;
import se.technipelago.minio.config.McConfig;
import se.technipelago.minio.config.McConfigAlias;

import java.util.concurrent.CountDownLatch;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@CommandLine.Command(name = "list", aliases = {"ls"}, description = "List object", mixinStandardHelpOptions = true)
public class BucketListCommand extends AbstractBucketCommand<BucketCommand> implements Runnable {

    @CommandLine.ParentCommand
    protected BucketCommand parent;
    @CommandLine.Parameters(paramLabel = "path", description = "Object path")
    private String path;

    @Override
    public BucketCommand getParent() {
        return parent;
    }

    @Override
    public void run() {
        final McConfig config = readConfiguration();
        final String host = getHost(path);
        final String bucket = getBucket(path);
        final McConfigAlias alias = config.getAlias(host).orElseThrow(() -> new RuntimeException("No such host: " + host));
        final MinioClient client = getClient(alias.getUrl(), getRegion(), alias.getAccessKey(), alias.getSecretKey());
        final CountDownLatch latch = new CountDownLatch(1);

        Flowable.fromPublisher(getObjects(client, bucket))
                .doOnComplete(latch::countDown)
                .forEach(item -> System.out.println(item.objectName()));
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
