package se.technipelago.minio.cmd;

import se.technipelago.minio.config.McConfig;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
public interface MinioCommand<T extends MinioCommand> {
    T getParent();

    boolean isVerbose();

    String getRegion();

    McConfig readConfiguration();

    void writeConfiguration(McConfig item);
}
