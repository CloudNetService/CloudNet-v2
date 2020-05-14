package eu.cloudnetservice.cloudnet.v2.lib.server.screen;

import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;

public class ScreenInfo {

    private final ServiceId serviceId;

    private final String line;

    public ScreenInfo(ServiceId serviceId, String line) {
        this.serviceId = serviceId;
        this.line = line;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public String getLine() {
        return line;
    }
}