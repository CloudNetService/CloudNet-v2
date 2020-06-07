package eu.cloudnetservice.cloudnet.v2.master.module.model;

import com.google.gson.JsonObject;

public final class CloudModuleDeployFile {

    private final String key;
    private final JsonObject properties;

    public CloudModuleDeployFile(final String key, final JsonObject properties) {
        this.key = key;
        this.properties = properties;
    }

    public String getKey() {
        return key;
    }

    public JsonObject getRole() {
        return properties;
    }
}
