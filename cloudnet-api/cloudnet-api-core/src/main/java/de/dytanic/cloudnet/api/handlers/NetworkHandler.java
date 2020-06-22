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
     * @param serverInfo
     */
    void onServerAdd(ServerInfo serverInfo);

    /**
     * Called if the serverinfo of a game server is updated
     *
     * @param serverInfo
     */
    void onServerInfoUpdate(ServerInfo serverInfo);

    /**
     * Called if a game server was removed
     *
     * @param serverInfo
     */
    void onServerRemove(ServerInfo serverInfo);

    /**
     * Called if one proxy server was add into the network
     */
    void onProxyAdd(ProxyInfo proxyInfo);

    /**
     * Called if the proxyinfo from some proxy server was updated
     *
     * @param proxyInfo
     */
    void onProxyInfoUpdate(ProxyInfo proxyInfo);

    /**
     * Called if a proxy server is disconnected from network
     *
     * @param proxyInfo
     */
    void onProxyRemove(ProxyInfo proxyInfo);

    /**
     * Called if the cloudnetwork object is updated
     *
     * @param cloudNetwork
     */
    void onCloudNetworkUpdate(CloudNetwork cloudNetwork);

    /**
     * Called if a custom channel message was received
     *
     * @param channel
     * @param message
     * @param document
     */
    void onCustomChannelMessageReceive(String channel, String message, Document document);

    /**
     * Called if a custom channel message was received
     *
     * @param channel
     * @param message
     * @param document
     */
    void onCustomSubChannelMessageReceive(String channel, String message, Document document);

    /**
     * Called if a playerlogin to the network from cloudnet was successfull
     *
     * @param cloudPlayer
     */
    void onPlayerLoginNetwork(CloudPlayer cloudPlayer);

    /**
     * Called if a player disconnects from the network
     *
     * @param cloudPlayer
     */
    void onPlayerDisconnectNetwork(CloudPlayer cloudPlayer);

    /**
     * Called if a player disconnects from the network
     *
     * @param cloudPlayer
     */
    void onPlayerDisconnectNetwork(UUID uniqueId);

    /**
     * Called if a player was updated on network
     *
     * @param cloudPlayer
     */
    void onPlayerUpdate(CloudPlayer cloudPlayer);

    /**
     * Called if a offlineplayer was updated
     */
    void onOfflinePlayerUpdate(OfflinePlayer offlinePlayer);

    /**
     * Called if the player online count changed
     *
     * @param onlineCount
     */
    void onUpdateOnlineCount(int onlineCount);

}
