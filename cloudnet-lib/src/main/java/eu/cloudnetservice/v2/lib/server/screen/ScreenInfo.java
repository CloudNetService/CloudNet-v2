package eu.cloudnetservice.v2.lib.server.screen;

import eu.cloudnetservice.v2.lib.service.ServiceId;

public class ScreenInfo {

    private ServiceId serviceId;

    private String line;

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