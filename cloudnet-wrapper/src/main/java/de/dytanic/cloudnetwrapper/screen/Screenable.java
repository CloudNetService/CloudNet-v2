package de.dytanic.cloudnetwrapper.screen;

import de.dytanic.cloudnet.lib.service.ServiceId;

public interface Screenable {

    ServiceId getServiceId();

    Process getInstance();

}