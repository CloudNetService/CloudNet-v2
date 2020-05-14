package eu.cloudnetservice.cloudnet.v2.wrapper.screen;

import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;

public interface Screenable {

    ServiceId getServiceId();

    Process getInstance();

}