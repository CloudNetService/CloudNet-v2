package eu.cloudnetservice.v2.wrapper.screen;

import de.dytanic.cloudnet.lib.service.ServiceId;

public interface Screenable {

    ServiceId getServiceId();

    Process getInstance();

}