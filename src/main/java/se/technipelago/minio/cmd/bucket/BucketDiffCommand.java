package se.technipelago.minio.cmd.bucket;

import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import picocli.CommandLine;
import se.technipelago.minio.cmd.AbstractBucketCommand;
import se.technipelago.minio.config.McConfig;
import se.technipelago.minio.config.McConfigAlias;

import java.util.concurrent.CountDownLatch;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@CommandLine.Command(name = "diff", description = "Verify that same files exist in two buckets", mixinStandardHelpOptions = true)
public class BucketDiffCommand extends AbstractBucketCommand<BucketCommand> implements Runnable {

    @CommandLine.ParentCommand
    protected BucketCommand parent;
    @CommandLine.Parameters(index = "0", paramLabel = "url1", description = "Reference bucket")
    private String url1;
    @CommandLine.Parameters(index = "1", paramLabel = "url2", description = "Bucket to verify")
    private String url2;

    @Override
    public BucketCommand getParent() {
        return parent;
    }

    @Override
    public void run() {
        if (isVerbose()) {
            System.out.println("Verifying objects in bucket " + getBucket(url1) + " and " + getBucket(url2));
        }
        final McConfig config = readConfiguration();
        final String host1 = getHost(url1);
        final String host2 = getHost(url2);
        final McConfigAlias alias1 = config.getAlias(host1).orElseThrow(() -> new RuntimeException("No such host: " + host1));
        final McConfigAlias alias2 = config.getAlias(host2).orElseThrow(() -> new RuntimeException("No such host: " + host2));
        final MinioClient client1 = getClient(alias1.getUrl(), getRegion(), alias1.getAccessKey(), alias1.getSecretKey());
        final MinioClient client2 = getClient(alias2.getUrl(), getRegion(), alias2.getAccessKey(), alias2.getSecretKey());
        final String theSecondBucket = getBucket(url2);
        final CountDownLatch latch = new CountDownLatch(1);

        getObjects(client1, getBucket(url1)).subscribe(new Subscriber<Item>() {

            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                this.subscription = s;
                s.request(1);
            }

            @Override
            public void onNext(Item item) {
                try {
                    StatObjectResponse response = client2.statObject(StatObjectArgs.builder()
                            .bucket(theSecondBucket)
                            .object(item.objectName())
                            .build());
                    if (response.size() != item.size()) {
                        System.out.println("Different size: " + item.objectName() + " (" + item.size() + " != " + response.size() + ")");
                    } else if (!response.etag().equals(canonicalizeEtag(item.etag()))) {
                        System.out.println("Different etag: " + item.objectName() + " (" + canonicalizeEtag(item.etag()) + " != " + response.etag() + ")");
                    } else if (isVerbose()) {
                        System.out.println("OK: " + item.objectName());
                    }
                } catch (ErrorResponseException e) {
                    System.out.println(e.errorResponse().code() + ": " + item.objectName());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                subscription.cancel();
                this.subscription = null;
                latch.countDown();
            }

            @Override
            public void onComplete() {
                this.subscription = null;
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String canonicalizeEtag(String etag) {
        if (etag != null && etag.indexOf('"') == 0 && etag.lastIndexOf('"') == (etag.length() - 1)) {
            return etag.substring(1, etag.length() - 1);
        }
        return etag;
    }
}
