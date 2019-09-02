/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.screen;

import de.dytanic.cloudnet.lib.service.ServiceId;

public interface Screenable {

	ServiceId getServiceId();

	Process getInstance();

}