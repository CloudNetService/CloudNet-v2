package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.player.CloudPlayer;

/**
 * Created by Tareko on 19.01.2018.
 */
public class ProxiedPlayerServerSwitchEvent extends ProxiedCloudEvent {

    private CloudPlayer cloudPlayer;

    private String server;

    public ProxiedPlayerServerSwitchEvent(CloudPlayer cloudPlayer, String server) {
        this.cloudPlayer = cloudPlayer;
        this.server = server;
    }

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    public String getServer() {
        return server;
    }
}