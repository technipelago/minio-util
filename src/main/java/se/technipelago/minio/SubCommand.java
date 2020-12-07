package se.technipelago.minio;

import se.technipelago.minio.cmd.BaseCommand;
import se.technipelago.minio.config.McConfig;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
public abstract class SubCommand<T extends BaseCommand> implements Runnable {

    protected abstract T getParent();

    protected String getHost(String uri) {
        return uri.split("/", 3)[0];
    }

    protected String getPath(String uri) {
        return uri.split("/", 3)[2];
    }

    protected boolean isVerbose() {
        return getParent().isVerbose();
    }

    protected String getRegion() {
        return getParent().getRegion();
    }

    protected McConfig readConfiguration() {
        return getParent().readConfiguration();
    }

    public void writeConfiguration(McConfig item) {
        getParent().writeConfiguration(item);
    }
}
