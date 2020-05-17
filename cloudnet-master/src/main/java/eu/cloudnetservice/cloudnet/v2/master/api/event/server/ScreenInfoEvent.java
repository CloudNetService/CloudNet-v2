package eu.cloudnetservice.cloudnet.v2.master.api.event.server;

import eu.cloudnetservice.cloudnet.v2.event.async.AsyncEvent;
import eu.cloudnetservice.cloudnet.v2.event.async.AsyncPosterAdapter;
import eu.cloudnetservice.cloudnet.v2.lib.server.screen.ScreenInfo;

import java.util.Collection;

/**
 * Calls if a screen lines was received by wrapper
 */
public class ScreenInfoEvent extends AsyncEvent<ScreenInfoEvent> {

    private final Collection<ScreenInfo> screenInfos;

    public ScreenInfoEvent(Collection<ScreenInfo> screenInfos) {
        super(new AsyncPosterAdapter<>());
        this.screenInfos = screenInfos;
    }

    public Collection<ScreenInfo> getScreenInfos() {
        return screenInfos;
    }
}
