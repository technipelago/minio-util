package se.technipelago.minio.cmd.object;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import picocli.CommandLine;
import se.technipelago.minio.cmd.AbstractBucketCommand;
import se.technipelago.minio.config.McConfig;
import se.technipelago.minio.config.McConfigAlias;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */

@CommandLine.Command(name = "cp", aliases = {"put", "create"}, description = "Save object in bucket", mixinStandardHelpOptions = true)
public class ObjectCreateCommand extends AbstractBucketCommand<ObjectCommand> implements Runnable {

    @CommandLine.ParentCommand
    protected ObjectCommand parent;
    @CommandLine.Parameters(index = "0", paramLabel = "file", description = "The file to store")
    private String file;
    @CommandLine.Parameters(index = "1", paramLabel = "path", description = "Object path")
    private String uri;

    @Override
    public ObjectCommand getParent() {
        return parent;
    }

    @Override
    public void run() {
        final McConfig config = readConfiguration();
        final String host = getHost(uri);
        final String bucket = getBucket(uri);
        final String path = getPath(uri);
        final McConfigAlias alias = config.getAlias(host).orElseThrow(() -> new RuntimeException("No such host: " + host));
        final MinioClient client = getClient(alias.getUrl(), getRegion(), alias.getAccessKey(), alias.getSecretKey());
        final File inputFile = new File(file);

        //createBucket(client, bucket);

        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    //.contentType("application/octet-stream")
                    .stream(in, inputFile.length(), -1)
                    .build();
            ObjectWriteResponse response = client.putObject(args);
            if (isVerbose()) {
                System.out.println("Saved object " + path + " (etag=" + response.etag() + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
