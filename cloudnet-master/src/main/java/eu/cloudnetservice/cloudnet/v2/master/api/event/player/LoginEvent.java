package eu.cloudnetservice.cloudnet.v2.master.api.event.player;

import eu.cloudnetservice.cloudnet.v2.event.async.AsyncEvent;
import eu.cloudnetservice.cloudnet.v2.event.async.AsyncPosterAdapter;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;

/**
 * Calls if a Player was logged into the network
 */
public class LoginEvent extends AsyncEvent<LoginEvent> {

    private final CloudPlayer cloudPlayer;

    public LoginEvent(CloudPlayer cloudPlayer) {
        super(new AsyncPosterAdapter<>());
        this.cloudPlayer = cloudPlayer;
    }

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }
}
