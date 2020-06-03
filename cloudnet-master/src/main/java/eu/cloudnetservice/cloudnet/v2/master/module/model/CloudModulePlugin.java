package eu.cloudnetservice.cloudnet.v2.master.module.model;

import com.google.gson.JsonObject;

public final class CloudModulePlugin {

    private final String path;
    private final JsonObject properties;

    public CloudModulePlugin(final String path, final JsonObject properties) {
        this.path = path;
        this.properties = properties;
    }

    public String getPath() {
        return path;
    }

    public JsonObject getRole() {
        return properties;
    }
}
