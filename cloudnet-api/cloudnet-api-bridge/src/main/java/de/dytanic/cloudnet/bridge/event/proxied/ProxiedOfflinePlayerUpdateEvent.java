package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.player.OfflinePlayer;

/**
 * Called if a offlinePlayer update was send from Master
 */
public class ProxiedOfflinePlayerUpdateEvent extends ProxiedCloudEvent {

    private OfflinePlayer offlinePlayer;

    public ProxiedOfflinePlayerUpdateEvent(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }
}