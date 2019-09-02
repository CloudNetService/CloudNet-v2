/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.server.info.ServerInfo;

/**
 * Calls if a game server was add into the network
 */
public class ProxiedServerAddEvent extends ProxiedCloudEvent {

    private ServerInfo serverInfo;

    public ProxiedServerAddEvent(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }
}
