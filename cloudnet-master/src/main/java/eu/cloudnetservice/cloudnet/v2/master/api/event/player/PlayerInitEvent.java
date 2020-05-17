package eu.cloudnetservice.cloudnet.v2.master.api.event.player;

import eu.cloudnetservice.cloudnet.v2.event.Event;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;

/**
 * Created by Tareko on 17.10.2017.
 */
public class PlayerInitEvent extends Event {

    private final CloudPlayer cloudPlayer;

    public PlayerInitEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}