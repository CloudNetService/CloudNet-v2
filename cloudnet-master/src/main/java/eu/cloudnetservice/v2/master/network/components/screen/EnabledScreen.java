package eu.cloudnetservice.v2.master.network.components.screen;

import de.dytanic.cloudnet.lib.service.ServiceId;
import eu.cloudnetservice.v2.master.network.components.Wrapper;

/**
 * Created by Tareko on 25.08.2017.
 */
public class EnabledScreen {

    private ServiceId serviceId;

    private Wrapper wrapper;

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