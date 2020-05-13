package eu.cloudnetservice.v2.master.api.event.player;

import eu.cloudnetservice.v2.event.Event;
import eu.cloudnetservice.v2.lib.player.CloudPlayer;

/**
 * Calls if a Player loguts from the proxy
 */
public class LogoutEvent extends Event {

    private CloudPlayer playerWhereAmI;

    public LogoutEvent(CloudPlayer playerWhereAmI) {
        this.playerWhereAmI = playerWhereAmI;
    }

    public CloudPlayer getPlayerWhereAmI() {
        return playerWhereAmI;
    }
}