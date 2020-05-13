package eu.cloudnetservice.v2.wrapper.screen;

import eu.cloudnetservice.v2.lib.service.ServiceId;

public interface Screenable {

    ServiceId getServiceId();

    Process getInstance();

}