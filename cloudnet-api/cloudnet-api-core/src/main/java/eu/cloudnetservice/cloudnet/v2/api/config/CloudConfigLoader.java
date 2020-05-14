package eu.cloudnetservice.cloudnet.v2.api.config;

import eu.cloudnetservice.cloudnet.v2.lib.ConnectableAddress;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.nio.file.Path;

public class CloudConfigLoader {

    private final Path pathConnectionJson;

    private final Path pathConfigJson;

    private final ConfigTypeLoader type;

    public CloudConfigLoader(Path pathConnectionJson, Path pathConfigJson, ConfigTypeLoader type) {
        this.pathConnectionJson = pathConnectionJson;
        this.pathConfigJson = pathConfigJson;
        this.type = type;
    }

    public ConfigTypeLoader getType() {
        return type;
    }

    public Path getPathConfigJson() {
        return pathConfigJson;
    }

    public Path getPathConnectionJson() {
        return pathConnectionJson;
    }

    public Document loadConfig() {
        return Document.loadDocument(pathConfigJson);
    }

    public ConnectableAddress loadConnnection() {
        return Document.loadDocument(pathConnectionJson).getObject("connection", ConnectableAddress.TYPE);
    }

}
