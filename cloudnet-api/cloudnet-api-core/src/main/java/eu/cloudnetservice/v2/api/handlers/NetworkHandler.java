package eu.cloudnetservice.v2.api.handlers;

import eu.cloudnetservice.v2.lib.CloudNetwork;
import eu.cloudnetservice.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.v2.lib.player.OfflinePlayer;
import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.lib.utility.document.Document;

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
     * Called if the ServerInfo is updated from a game server
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
     * Called if some proxy server is disconnected from network
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
     * Called if a Player Login to the network from the cloudnet successfully
     *
     * @param cloudPlayer
     */
    void onPlayerLoginNetwork(CloudPlayer cloudPlayer);

    /**
     * Called if a Player Disconnect to the network from cloudnet
     *
     * @param cloudPlayer
     */
    void onPlayerDisconnectNetwork(CloudPlayer cloudPlayer);

    /**
     * Called if a Player Disconnect to the network from cloudnet
     *
     * @param uniqueId
     */
    void onPlayerDisconnectNetwork(UUID uniqueId);

    /**
     * Called if a Player was updated on network
     *
     * @param cloudPlayer
     */
    void onPlayerUpdate(CloudPlayer cloudPlayer);

    /**
     * Called if a OfflinePlayer was updated
     */
    void onOfflinePlayerUpdate(OfflinePlayer offlinePlayer);

    /**
     * Called if the player online count was changed
     *
     * @param onlineCount
     */
    void onUpdateOnlineCount(int onlineCount);

}