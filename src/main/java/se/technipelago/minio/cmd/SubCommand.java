package se.technipelago.minio.cmd;

import se.technipelago.minio.config.McConfig;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
public abstract class SubCommand<T extends MinioCommand> implements MinioCommand<T> {

    public abstract T getParent();

    protected String getHost(String uri) {
        return uri.split("/", 3)[0];
    }

    protected String getPath(String uri) {
        return uri.split("/", 3)[2];
    }

    public boolean isVerbose() {
        return getParent().isVerbose();
    }

    public String getRegion() {
        return getParent().getRegion();
    }

    public McConfig readConfiguration() {
        return getParent().readConfiguration();
    }

    public void writeConfiguration(McConfig item) {
        getParent().writeConfiguration(item);
    }
}
