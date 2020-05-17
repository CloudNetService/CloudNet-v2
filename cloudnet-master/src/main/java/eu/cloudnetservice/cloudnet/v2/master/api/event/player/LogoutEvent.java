package eu.cloudnetservice.cloudnet.v2.master.api.event.player;

import eu.cloudnetservice.cloudnet.v2.event.Event;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;

/**
 * Calls if a Player loguts from the proxy
 */
public class LogoutEvent extends Event {

    private final CloudPlayer playerWhereAmI;

    public LogoutEvent(CloudPlayer playerWhereAmI) {
        this.playerWhereAmI = playerWhereAmI;
    }

    public CloudPlayer getPlayerWhereAmI() {
        return playerWhereAmI;
    }
}