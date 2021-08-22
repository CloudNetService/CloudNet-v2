/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.handlers;

import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.UUID;

/**
 * This Clazz triggerd incoming Packet updates
 */
public interface NetworkHandler {

    /**
     * Called if a new server was add into the network
     *
     * @param serverInfo the server info
     */
    void onServerAdd(ServerInfo serverInfo);

    /**
     * Called if the ServerInfo is updated from a game server
     *
     * @param serverInfo the server info
     */
    void onServerInfoUpdate(ServerInfo serverInfo);

    /**
     * Called if a game server was removed
     *
     * @param serverInfo the server info
     */
    void onServerRemove(ServerInfo serverInfo);

    /**
     * Called if one proxy server was add into the network
     * 
     * @param proxyInfo the proxy info
     */
    void onProxyAdd(ProxyInfo proxyInfo);

    /**
     * Called if the proxyinfo from some proxy server was updated
     *
     * @param proxyInfo the proxy info
     */
    void onProxyInfoUpdate(ProxyInfo proxyInfo);

    /**
     * Called if some proxy server is disconnected from network
     *
     * @param proxyInfo the proxy info
     */
    void onProxyRemove(ProxyInfo proxyInfo);

    /**
     * Called if the cloudnetwork object is updated
     *
     * @param cloudNetwork the cloud network instance
     */
    void onCloudNetworkUpdate(CloudNetwork cloudNetwork);

    /**
     * Called if a custom channel message was received
     *
     * @param channel the channel
     * @param message the custom message
     * @param document the accompanying document
     */
    void onCustomChannelMessageReceive(String channel, String message, Document document);

    /**
     * Called if a custom channel message was received
     *
     * @param channel the channel
     * @param message the custom message
     * @param document the accompanying document
     */
    void onCustomSubChannelMessageReceive(String channel, String message, Document document);

    /**
     * Called if a Player Login to the network from the cloudnet successfully
     *
     * @param cloudPlayer the player
     */
    void onPlayerLoginNetwork(CloudPlayer cloudPlayer);

    /**
     * Called if a Player Disconnect to the network from cloudnet
     *
     * @param cloudPlayer the player
     */
    void onPlayerDisconnectNetwork(CloudPlayer cloudPlayer);

    /**
     * Called if a Player Disconnect to the network from cloudnet
     *
     * @param uniqueId the UUID of the player
     */
    void onPlayerDisconnectNetwork(UUID uniqueId);

    /**
     * Called if a Player was updated on network
     *
     * @param cloudPlayer the player
     */
    void onPlayerUpdate(CloudPlayer cloudPlayer);

    /**
     * Called if a OfflinePlayer was updated
     *
     * @param offlinePlayer the offline player
     */
    void onOfflinePlayerUpdate(OfflinePlayer offlinePlayer);

    /**
     * Called if the player online count was changed
     *
     * @param onlineCount the online count
     */
    void onUpdateOnlineCount(int onlineCount);

}
