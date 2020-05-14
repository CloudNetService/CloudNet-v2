package eu.cloudnetservice.cloudnet.v2.master.network.components.screen;

import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;

/**
 * Created by Tareko on 25.08.2017.
 */
public class EnabledScreen {

    private final ServiceId serviceId;

    private final Wrapper wrapper;

    public EnabledScreen(ServiceId serviceId, Wrapper wrapper) {
        this.serviceId = serviceId;
        this.wrapper = wrapper;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }
}