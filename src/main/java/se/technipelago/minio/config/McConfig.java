package se.technipelago.minio.config;

import io.micronaut.core.annotation.Introspected;

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

    public McConfig() {
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
        McConfigAlias item = new McConfigAlias(uri.toString(), accessKey, secretKey);
        if (aliases == null) {
            aliases = new HashMap<>();
        }
        aliases.put(alias, item);

        return item;
    }
}
