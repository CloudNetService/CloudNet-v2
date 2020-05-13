package eu.cloudnetservice.v2.bridge.event.proxied;

import eu.cloudnetservice.v2.lib.player.CloudPlayer;

/**
 * This event is called whenever a player has switched their current server.
 */
public class ProxiedPlayerServerSwitchEvent extends ProxiedCloudEvent {

    private final CloudPlayer cloudPlayer;

    private final String server;

    public ProxiedPlayerServerSwitchEvent(CloudPlayer cloudPlayer, String server) {
        this.cloudPlayer = cloudPlayer;
        this.server = server;
    }

    /**
     * @return the player that just switched to another server.
     */
    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    /**
     * @return the server-id the player switched to.
     */
    public String getServer() {
        return server;
    }
}