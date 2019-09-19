/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.network;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import de.dytanic.cloudnetcore.network.components.Wrapper;

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
