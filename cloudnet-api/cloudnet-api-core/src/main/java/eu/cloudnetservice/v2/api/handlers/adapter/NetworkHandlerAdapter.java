package eu.cloudnetservice.v2.api.handlers.adapter;

import eu.cloudnetservice.v2.api.handlers.NetworkHandler;
import eu.cloudnetservice.v2.lib.CloudNetwork;
import eu.cloudnetservice.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.v2.lib.player.OfflinePlayer;
import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.lib.utility.document.Document;

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
