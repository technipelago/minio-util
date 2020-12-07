package se.technipelago.minio.cmd;

import io.micronaut.http.uri.UriBuilder;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import se.technipelago.minio.SubCommand;

import java.net.URI;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
public abstract class AbstractBucketCommand<T extends BaseCommand> extends SubCommand<T> {

    protected MinioClient getClient(URI url, String region, String username, String password) {
        URI uri = UriBuilder.of(url).replacePath("/").build();
        return MinioClient.builder()
                .endpoint(uri.toString())
                .credentials(username, password)
                .region(region)
                .build();
    }

    protected String getBucket(String uri) {
        return uri.split("/", 3)[1];
    }

    protected void createBucket(MinioClient minioClient, String bucketName) {
        MakeBucketArgs args = MakeBucketArgs.builder()
                .bucket(bucketName)
                .build();
        try {
            minioClient.makeBucket(args);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create bucket " + bucketName, e);
        }
    }

    protected Publisher<Item> getObjects(MinioClient minioClient, String bucketName) {
        try {
            ListObjectsArgs args = new ListObjectsArgs().builder()
                    .bucket(bucketName)
                    .recursive(true)
                    .build();
            return Flowable.fromIterable(minioClient.listObjects(args)).map(Result::get);
        } catch (Exception e) {
            throw new RuntimeException("Unable to list objects in bucket " + bucketName, e);
        }
    }


}
