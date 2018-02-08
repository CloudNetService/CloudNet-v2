package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProxiedOfflinePlayerUpdateEvent extends ProxiedCloudEvent {

    private OfflinePlayer offlinePlayer;

}