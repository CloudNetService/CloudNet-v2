/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.screen;

import de.dytanic.cloudnet.lib.service.ServiceId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScreenInfo {

    private ServiceId serviceId;

    private String line;

}