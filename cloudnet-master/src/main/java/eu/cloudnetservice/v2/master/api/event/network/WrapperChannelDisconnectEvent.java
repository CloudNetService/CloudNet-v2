package eu.cloudnetservice.v2.master.api.event.network;

import eu.cloudnetservice.v2.event.async.AsyncEvent;
import eu.cloudnetservice.v2.event.async.AsyncPosterAdapter;
import eu.cloudnetservice.v2.master.network.components.Wrapper;

/**
 * Calls if the wrapper is disconnected
 */
public class WrapperChannelDisconnectEvent extends AsyncEvent<WrapperChannelDisconnectEvent> {

    private Wrapper wrapper;

    public WrapperChannelDisconnectEvent(Wrapper wrapper) {
        super(new AsyncPosterAdapter<>());
        this.wrapper = wrapper;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }
}
