package de.dytanic.cloudnet.api.config;

import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.nio.file.Path;

public class CloudConfigLoader {

    private Path pathConnectionJson;

    private Path pathConfigJson;

    private ConfigTypeLoader type;

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
