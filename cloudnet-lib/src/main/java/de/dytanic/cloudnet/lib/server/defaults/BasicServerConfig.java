/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.defaults;

import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 24.10.2017.
 */
public class BasicServerConfig extends ServerConfig {

    public BasicServerConfig() {
        super(false, "null", new Document(), System.currentTimeMillis());
    }
}
