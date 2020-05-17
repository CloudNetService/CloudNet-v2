package eu.cloudnetservice.cloudnet.v2.master.api.event.player;

import eu.cloudnetservice.cloudnet.v2.event.async.AsyncEvent;
import eu.cloudnetservice.cloudnet.v2.event.async.AsyncPosterAdapter;
import eu.cloudnetservice.cloudnet.v2.lib.player.OfflinePlayer;

/**
 * Calls if a player is updated on cloudnet
 */
public class UpdatePlayerEvent extends AsyncEvent<UpdatePlayerEvent> {

    private final OfflinePlayer offlinePlayer;

    public UpdatePlayerEvent(OfflinePlayer offlinePlayer) {
        super(new AsyncPosterAdapter<>());
        this.offlinePlayer = offlinePlayer;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }
}
