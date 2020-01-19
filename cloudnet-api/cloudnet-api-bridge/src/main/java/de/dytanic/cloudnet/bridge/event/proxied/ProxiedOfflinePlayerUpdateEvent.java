package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.player.OfflinePlayer;

/**
 * This event is called whenever an {@link OfflinePlayer} is updated and the data is
 * forwarded to this service.
 */
public class ProxiedOfflinePlayerUpdateEvent extends ProxiedCloudEvent {

    private OfflinePlayer offlinePlayer;

    public ProxiedOfflinePlayerUpdateEvent(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    /**
     * @return the newly updated player instance.
     */
    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }
}