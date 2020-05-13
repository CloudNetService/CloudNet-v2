package eu.cloudnetservice.v2.lib.server;

import eu.cloudnetservice.v2.lib.interfaces.Nameable;
import eu.cloudnetservice.v2.lib.server.template.Template;

/**
 * Created by Tareko on 03.07.2017.
 */
public class ServerGroupProfile implements Nameable {

    private final String name;

    private final int maxPlayerCount;

    private final Template config;

    public ServerGroupProfile(String name, int maxPlayerCount, Template config) {
        this.name = name;
        this.maxPlayerCount = maxPlayerCount;
        this.config = config;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public Template getConfig() {
        return config;
    }
}
