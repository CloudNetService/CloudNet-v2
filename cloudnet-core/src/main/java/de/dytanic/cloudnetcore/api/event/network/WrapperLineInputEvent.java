/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.network;

import de.dytanic.cloudnet.event.async.AsyncEvent;
import de.dytanic.cloudnet.event.async.AsyncPosterAdapter;
import de.dytanic.cloudnet.lib.service.wrapper.WrapperScreen;
import de.dytanic.cloudnetcore.network.components.Wrapper;

/**
 * Created by Tareko on 23.09.2017.
 */
public class WrapperLineInputEvent extends AsyncEvent<WrapperLineInputEvent> {

    private WrapperScreen wrapperScreen;

    private Wrapper wrapper;

    public WrapperLineInputEvent(Wrapper wrapper, WrapperScreen wrapperScreen) {
        super(new AsyncPosterAdapter<>());
        this.wrapper = wrapper;
        this.wrapperScreen = wrapperScreen;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }

    public WrapperScreen getWrapperScreen() {
        return wrapperScreen;
    }
}
