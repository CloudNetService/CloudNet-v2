package eu.cloudnetservice.v2.master.api.event.player;

import eu.cloudnetservice.v2.event.async.AsyncEvent;
import eu.cloudnetservice.v2.event.async.AsyncPosterAdapter;

import java.util.UUID;

/**
 * Calls if a player
 */
public class LogoutEventUnique extends AsyncEvent<LogoutEventUnique> {

    private final UUID uniqueId;

    public LogoutEventUnique(UUID uniqueId) {
        super(new AsyncPosterAdapter<>());
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
}
