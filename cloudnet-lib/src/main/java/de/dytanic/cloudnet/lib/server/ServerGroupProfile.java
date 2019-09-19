package de.dytanic.cloudnet.lib.server;

import de.dytanic.cloudnet.lib.interfaces.Nameable;
import de.dytanic.cloudnet.lib.server.template.Template;

/**
 * Created by Tareko on 03.07.2017.
 */
public class ServerGroupProfile implements Nameable {

    private String name;

    private int maxPlayerCount;

    private Template config;

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
