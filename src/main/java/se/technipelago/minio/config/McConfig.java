package se.technipelago.minio.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.annotation.Introspected;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Goran Ehrsson
 * @since 1.0
 */
@Introspected
public class McConfig {

    private String version = "10";
    private Map<String, McConfigAlias> aliases;

    public static McConfig read(File file) {
        try {
            return new ObjectMapper().readValue(file, McConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read mc config file " + file.getAbsolutePath(), e);
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, McConfigAlias> getAliases() {
        return aliases;
    }

    public void setAliases(Map<String, McConfigAlias> aliases) {
        this.aliases = aliases;
    }

    public Optional<McConfigAlias> getAlias(String name) {
        return Optional.ofNullable(aliases != null ? aliases.get(name) : null);
    }

    public McConfigAlias setAlias(String alias, URI uri, String accessKey, String secretKey) {
        McConfigAlias item = new McConfigAlias(uri, accessKey, secretKey);
        if (aliases == null) {
            aliases = new HashMap<>();
        }
        aliases.put(alias, item);

        return item;
    }

    public void write(File file) throws IOException {
        new ObjectMapper().writeValue(file, this);
    }
}
