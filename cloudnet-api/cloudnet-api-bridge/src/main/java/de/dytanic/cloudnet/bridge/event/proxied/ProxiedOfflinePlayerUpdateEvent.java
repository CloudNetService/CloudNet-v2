package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Called if a offlinePlayer update was send from Master
 */
@Getter
@AllArgsConstructor
public class ProxiedOfflinePlayerUpdateEvent extends ProxiedCloudEvent {

    private OfflinePlayer offlinePlayer;

}