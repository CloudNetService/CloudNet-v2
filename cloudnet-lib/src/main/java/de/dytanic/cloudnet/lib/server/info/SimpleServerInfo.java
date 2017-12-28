/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.lib.server.info;

import de.dytanic.cloudnet.lib.service.ServiceId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleServerInfo {

    private ServiceId serviceId;

    private String hostAddress;

    private int port;

    private int onlineCount;

    private int maxPlayers;

}