/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.config;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.utility.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;

@Getter
@AllArgsConstructor
public class CloudConfigLoader {

    private Path pathConnectionJson;

    private Path pathConfigJson;

    private ConfigTypeLoader type;

    public Document loadConfig()
    {
        return Document.loadDocument(pathConfigJson);
    }

    public ConnectableAddress loadConnnection()
    {
        return Document.loadDocument(pathConnectionJson).getObject("connection", new TypeToken<ConnectableAddress>(){}.getType());
    }

}