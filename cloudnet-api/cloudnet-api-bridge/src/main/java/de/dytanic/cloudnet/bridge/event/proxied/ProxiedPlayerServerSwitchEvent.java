package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Tareko on 19.01.2018.
 */
@Getter
@AllArgsConstructor
public class ProxiedPlayerServerSwitchEvent extends ProxiedCloudEvent {

    private CloudPlayer cloudPlayer;

    private String server;

}