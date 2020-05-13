package eu.cloudnetservice.v2.master.api.event.network;

import eu.cloudnetservice.v2.event.async.AsyncEvent;
import eu.cloudnetservice.v2.event.async.AsyncPosterAdapter;
import eu.cloudnetservice.v2.lib.service.wrapper.WrapperScreen;
import eu.cloudnetservice.v2.master.network.components.Wrapper;

/**
 * Created by Tareko on 23.09.2017.
 */
public class WrapperLineInputEvent extends AsyncEvent<WrapperLineInputEvent> {

    private final WrapperScreen wrapperScreen;

    private final Wrapper wrapper;

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
