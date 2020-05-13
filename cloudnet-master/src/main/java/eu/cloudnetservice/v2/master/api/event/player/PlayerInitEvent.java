package eu.cloudnetservice.v2.master.api.event.player;

import eu.cloudnetservice.v2.event.Event;
import eu.cloudnetservice.v2.lib.player.CloudPlayer;

/**
 * Created by Tareko on 17.10.2017.
 */
public class PlayerInitEvent extends Event {

    private CloudPlayer cloudPlayer;

    public PlayerInitEvent(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}