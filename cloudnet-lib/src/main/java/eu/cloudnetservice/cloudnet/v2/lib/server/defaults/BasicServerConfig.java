package eu.cloudnetservice.cloudnet.v2.lib.server.defaults;

import eu.cloudnetservice.cloudnet.v2.lib.server.ServerConfig;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 24.10.2017.
 */
public class BasicServerConfig extends ServerConfig {

    public BasicServerConfig() {
        super(false, new Document(), System.currentTimeMillis());
    }
}
