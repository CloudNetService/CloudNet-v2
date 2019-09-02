/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.handlers.adapter;

import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.UUID;

/**
 * Simpled Adapter for the network server.
 * You can extends this Class for use some methods
 */
public class NetworkHandlerAdapter implements NetworkHandler {

    @Override
    public void onServerAdd(ServerInfo serverInfo) {

    }

    @Override
    public void onServerInfoUpdate(ServerInfo serverInfo) {

    }

    @Override
    public void onServerRemove(ServerInfo serverInfo) {

    }

    @Override
    public void onProxyAdd(ProxyInfo proxyInfo) {

    }

    @Override
    public void onProxyInfoUpdate(ProxyInfo proxyInfo) {

    }

    @Override
    public void onProxyRemove(ProxyInfo proxyInfo) {

    }

    @Override
    public void onCloudNetworkUpdate(CloudNetwork cloudNetwork) {

    }

    @Override
    public void onCustomChannelMessageReceive(String channel, String message, Document document) {

    }

    @Override
    public void onCustomSubChannelMessageReceive(String channel, String message, Document document) {

    }

    @Override
    public void onPlayerLoginNetwork(CloudPlayer cloudPlayer) {

    }

    @Override
    public void onPlayerDisconnectNetwork(CloudPlayer cloudPlayer) {

    }

    @Override
    public void onPlayerDisconnectNetwork(UUID uniqueId) {

    }

    @Override
    public void onPlayerUpdate(CloudPlayer cloudPlayer) {

    }

    @Override
    public void onOfflinePlayerUpdate(OfflinePlayer offlinePlayer) {

    }

    @Override
    public void onUpdateOnlineCount(int onlineCount) {

    }
}
