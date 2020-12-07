package se.technipelago.minio.config;

import io.micronaut.core.annotation.Introspected;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@Introspected
public class McConfigAlias {

    private String url;
    private String accessKey;
    private String secretKey;
    private String api;
    private String path;

    public McConfigAlias() {
    }

    public McConfigAlias(String uri, String accessKey, String secretKey) {
        this.url = uri;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.api = "S3v4";
        this.path = "auto";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
