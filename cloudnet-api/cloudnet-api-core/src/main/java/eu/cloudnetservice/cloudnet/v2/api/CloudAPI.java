/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.api;

import com.google.gson.reflect.TypeToken;
import eu.cloudnetservice.cloudnet.v2.api.config.CloudConfigLoader;
import eu.cloudnetservice.cloudnet.v2.api.database.DatabaseManager;
import eu.cloudnetservice.cloudnet.v2.api.handlers.NetworkHandlerProvider;
import eu.cloudnetservice.cloudnet.v2.api.network.packet.api.*;
import eu.cloudnetservice.cloudnet.v2.api.network.packet.api.sync.*;
import eu.cloudnetservice.cloudnet.v2.api.network.packet.in.*;
import eu.cloudnetservice.cloudnet.v2.api.network.packet.out.*;
import eu.cloudnetservice.cloudnet.v2.lib.CloudNetwork;
import eu.cloudnetservice.cloudnet.v2.lib.ConnectableAddress;
import eu.cloudnetservice.cloudnet.v2.lib.DefaultType;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.network.NetDispatcher;
import eu.cloudnetservice.cloudnet.v2.lib.network.NetworkConnection;
import eu.cloudnetservice.cloudnet.v2.lib.network.WrapperInfo;
import eu.cloudnetservice.cloudnet.v2.lib.network.auth.Auth;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketManager;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.result.Result;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.cloudnet.v2.lib.player.OfflinePlayer;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.SimpleServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class CloudAPI {

    private static final Type MAP_UUID_OFFLINEPLAYER_TYPE = TypeToken.getParameterized(Map.class, UUID.class, OfflinePlayer.TYPE).getType();
    private static final Type SERVER_INFO_COLLECTION_TYPE = TypeToken.getParameterized(Collection.class, ServerInfo.class).getType();
    private static final Type PROXY_INFO_COLLECTION_TYPE = TypeToken.getParameterized(Collection.class, ProxyInfo.class).getType();
    private static final Type COLLECTION_CLOUDPLAYER_TYPE = TypeToken.getParameterized(Collection.class, CloudPlayer.TYPE).getType();
    private static CloudAPI instance;

    private final Document config;
    private final ServiceId serviceId;
    private final CloudConfigLoader cloudConfigLoader;

    private final NetworkConnection networkConnection;
    private final int memory;
    private final NetworkHandlerProvider networkHandlerProvider = new NetworkHandlerProvider();
    private final DatabaseManager databaseManager = new DatabaseManager();
    /**
     * Logger instance set by the respective bootstrap.
     */
    private final Logger logger;
    private CloudService cloudService = null;
    //Init
    private CloudNetwork cloudNetwork = new CloudNetwork();

    public CloudAPI(CloudConfigLoader loader, final Logger logger) throws UnknownHostException {
        if (instance != null) {
            throw new IllegalStateException("CloudAPI already instantiated.");
        }
        instance = this;
        this.cloudConfigLoader = loader;
        this.logger = logger;
        this.config = loader.loadConfig();
        this.networkConnection = new NetworkConnection(loader.loadConnection(), new ConnectableAddress(InetAddress.getByName("0.0.0.0"), 0));
        this.serviceId = config.getObject("serviceId", ServiceId.TYPE);
        this.memory = config.getInt("memory");

        initDefaultHandlers();
    }

    private void initDefaultHandlers() {
        PacketManager packetManager = networkConnection.getPacketManager();

        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 1, PacketInCloudNetwork.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 2, PacketInServerAdd.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 3, PacketInServerInfoUpdate.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 4, PacketInServerRemove.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 5, PacketInProxyAdd.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 6, PacketInProxyInfoUpdate.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 7, PacketInProxyRemove.class);

        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 8, PacketInCustomChannelMessage.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 9, PacketInCustomSubChannelMessage.class);

        packetManager.registerHandler(PacketRC.PLAYER_HANDLE + 1, PacketInLoginPlayer.class);
        packetManager.registerHandler(PacketRC.PLAYER_HANDLE + 2, PacketInLogoutPlayer.class);
        packetManager.registerHandler(PacketRC.PLAYER_HANDLE + 3, PacketInUpdatePlayer.class);
        packetManager.registerHandler(PacketRC.PLAYER_HANDLE + 4, PacketInUpdateOnlineCount.class);
        packetManager.registerHandler(PacketRC.PLAYER_HANDLE + 5, PacketInUpdateOfflinePlayer.class);
    }

    /**
     * @return the singleton instance of the cloud API.
     */
    public static CloudAPI getInstance() {
        return instance;
    }

    /**
     * Bootstraps the API and initiates the connection to the master.
     */
    public void bootstrap() {
        this.networkConnection.tryConnect(new NetDispatcher(networkConnection, false),
                                          new Auth(serviceId),
                                          () -> {
                                              if (this.cloudService.isProxyInstance()) {
                                                  ProxyServer.getInstance().stop("CloudNet-Stop!");
                                              } else {
                                                  Bukkit.shutdown();
                                              }
                                          });
        NetworkUtils.header();
    }

    /**
     * Disconnects the API from the master.
     */
    public void shutdown() {
        this.networkConnection.tryDisconnect();
    }

    /**
     * Updates the given server on the master.
     *
     * @param serverInfo the new server info.
     *
     * @return this.
     */
    public CloudAPI update(ServerInfo serverInfo) {
        this.logger.logp(Level.FINEST, this.getClass().getSimpleName(), "update", String.format("Updating server info: %s%n", serverInfo));
        if (networkConnection.isConnected()) {
            networkConnection.sendPacket(new PacketOutUpdateServerInfo(serverInfo));
        }
        return this;
    }

    /**
     * Updates the given proxy on the master.
     *
     * @param proxyInfo the new proxy info.
     *
     * @return this.
     */
    public CloudAPI update(ProxyInfo proxyInfo) {
        this.logger.logp(Level.FINEST, this.getClass().getSimpleName(), "update", String.format("Updating proxy info: %s%n", proxyInfo));
        if (networkConnection.isConnected()) {
            networkConnection.sendPacket(new PacketOutUpdateProxyInfo(proxyInfo));
        }
        return this;
    }

    /**
     * Collects all servers from the given server group and sums their currently online players.
     *
     * @param group the server group name.
     *
     * @return the amount of players currently online on the entire server group.
     */
    public int getOnlineCount(String group) {
        return getServers(group).stream()
                                .mapToInt(ServerInfo::getOnlineCount)
                                .sum();
    }

    /**
     * Collects and returns all currently running servers of the given group on the network.
     * When calling this function on a proxy, the cache is used.
     * On servers this queries the master.
     *
     * @param group the name of the server group.
     *
     * @return a collection containing all currently running servers belonging to the given server group.
     */
    public Collection<ServerInfo> getServers(String group) {
        if (cloudService != null && cloudService.isProxyInstance()) {
            return cloudService.getServers().values().stream()
                               .filter(serverInfo -> serverInfo.getServiceId().getGroup().equals(group))
                               .collect(Collectors.toList());
        }

        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetServers(group), networkConnection);
        return result.getResult().getObject("serverInfos", SERVER_INFO_COLLECTION_TYPE);
    }

    /**
     * @return the cloud service backing this API, may be a proxy or a server.
     */
    public CloudService getCloudService() {
        return cloudService;
    }

    /**
     * Sets the cloud service for the API to use.
     * Internal use only!
     *
     * @param cloudService the cloud service that should back this API.
     */
    public void setCloudService(CloudService cloudService) {
        this.cloudService = cloudService;
    }

    /**
     * Returns the Configuration Loader from this Plugin
     */
    public CloudConfigLoader getCloudConfigLoader() {
        return cloudConfigLoader;
    }

    /**
     * @return the configuration for the running cloud service.
     */
    public Document getConfig() {
        return config;
    }

    /**
     * @return basic information about the running cloud network.
     */
    public CloudNetwork getCloudNetwork() {
        return cloudNetwork;
    }

    /**
     * Updates the cloud network information available to this API.
     * Internal use only!
     *
     * @param cloudNetwork the new information about the cloud network.
     */
    public void setCloudNetwork(CloudNetwork cloudNetwork) {
        this.cloudNetwork = cloudNetwork;
    }

    /**
     * @return the network handler provider for this API instance.
     */
    public NetworkHandlerProvider getNetworkHandlerProvider() {
        return networkHandlerProvider;
    }

    /**
     * @return the network connection to the master.
     */
    public NetworkConnection getNetworkConnection() {
        return networkConnection;
    }

    /**
     * @return the cloud prefix used for messages.
     */
    public String getPrefix() {
        return cloudNetwork.getMessages().getString("prefix");
    }

    /**
     * @return the memory configured to be used by this service.
     */
    public int getMemory() {
        return memory;
    }

    /**
     * @return the service id of this service.
     */
    public ServiceId getServiceId() {
        return serviceId;
    }

    /**
     * @return the database manager on this service. Usually queries the master.
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    /**
     * @return the name of the group this service belongs to.
     */
    public String getGroup() {
        return serviceId.getGroup();
    }

    /**
     * @return the UUID of this service.
     */
    public UUID getUniqueId() {
        return serviceId.getUniqueId();
    }

    /**
     * @return the server id of this service (e.g. Lobby-1).
     */
    public String getServerId() {
        return serviceId.getServerId();
    }

    /**
     * Returns the Id (Lobby-1 -> "1")
     */
    public int getGroupInitId() {
        return serviceId.getId();
    }

    /**
     * @return the id of wrapper that this service runs on.
     */
    public String getWrapperId() {
        return serviceId.getWrapperId();
    }

    /**
     * Returns a simple version of the server group of the given server group name.
     *
     * @param serverGroupName the name of the server group.
     */
    public SimpleServerGroup getServerGroupData(String serverGroupName) {
        return cloudNetwork.getServerGroups().get(serverGroupName);
    }

    /**
     * Returns the proxy group with the given name.
     *
     * @param proxyGroupName the name of the proxy group to get.
     */
    public ProxyGroup getProxyGroupData(String proxyGroupName) {
        return cloudNetwork.getProxyGroups().get(proxyGroupName);
    }

    /**
     * @return the amount of players currently online on the entire cloud network.
     */
    public int getOnlineCount() {
        return cloudNetwork.getOnlineCount();
    }

    /**
     * @return the amount of players currently registered on the cloud.
     */
    public int getRegisteredPlayerCount() {
        return cloudNetwork.getRegisteredPlayerCount();
    }

    /**
     * @return the merged properties of all modules for this service.
     */
    public Document getModuleProperties() {
        return cloudNetwork.getModules();
    }

    /**
     * @return all running wrappers on the cloud network.
     */
    public Collection<WrapperInfo> getWrappers() {
        return cloudNetwork.getWrappers();
    }

    /**
     * Finds the first wrapper with the given case-insensitive name.
     * If no wrapper with the given id can be found, this returns null.
     *
     * @param wrapperId the case-insensitive wrapper id of the wrapper to get
     *
     * @return the {@link WrapperInfo} instance of the wrapper with the given wrapper id or {@code null}
     */
    public WrapperInfo getWrapper(String wrapperId) {
        return cloudNetwork.getWrappers().stream()
                           .filter(wrapperInfo -> wrapperInfo.getServerId().equalsIgnoreCase(wrapperId))
                           .findFirst().orElse(null);
    }

    /**
     * Sends the  custom channel message to all proxy instances.
     *
     * @param channel the channel to send the message on.
     * @param message the message to send.
     * @param value   the document attached to the message.
     */
    public void sendCustomSubProxyMessage(String channel, String message, Document value) {
        networkConnection.sendPacket(new PacketOutCustomSubChannelMessage(DefaultType.BUNGEE_CORD, channel, message, value));
    }

    /**
     * Sends the custom channel message to all server instances.
     *
     * @param channel the channel to send the message on.
     * @param message the message to send.
     * @param value   the document attached to the message.
     */
    public void sendCustomSubServerMessage(String channel, String message, Document value) {
        networkConnection.sendPacket(new PacketOutCustomSubChannelMessage(DefaultType.BUKKIT, channel, message, value));
    }

    /**
     * Sends the custom channel message to the specified server.
     *
     * @param channel    the channel to send the message on.
     * @param message    the message to send.
     * @param value      the document attached to the message.
     * @param serverName the name of the server that this message is going to be sent to.
     */
    public void sendCustomSubServerMessage(String channel, String message, Document value, String serverName) {
        networkConnection.sendPacket(new PacketOutCustomSubChannelMessage(DefaultType.BUKKIT, serverName, channel, message, value));
    }

    /**
     * Sends the custom channel message to the specified server.
     *
     * @param channel   the channel to send the message on.
     * @param message   the message to send.
     * @param value     the document attached to the message.
     * @param proxyName the name of the proxy that this message is going to be sent to.
     */
    public void sendCustomSubProxyMessage(String channel, String message, Document value, String proxyName) {
        networkConnection.sendPacket(new PacketOutCustomSubChannelMessage(DefaultType.BUNGEE_CORD, proxyName, channel, message, value));
    }

    /**
     * Updates the given server group across the network.
     *
     * @param serverGroup the server group to update.
     */
    public void updateServerGroup(ServerGroup serverGroup) {
        networkConnection.sendPacket(new PacketOutUpdateServerGroup(serverGroup));
    }

    /**
     * Updates the given proxy group across the network.
     *
     * @param proxyGroup the proxy group to update.
     */
    public void updateProxyGroup(ProxyGroup proxyGroup) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "updateProxyGroup",
                         String.format("Updating proxy group: %s%n", proxyGroup));
        networkConnection.sendPacket(new PacketOutUpdateProxyGroup(proxyGroup));
    }

    /**
     * Dispatch a command on the CloudNet master.
     *
     * @param commandLine the entire command line with space-separated arguments.
     */
    public void sendCloudCommand(String commandLine) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "sendCloudCommand",
                         String.format("Sending cloud command: %s%n", commandLine));
        networkConnection.sendPacket(new PacketOutExecuteCommand(commandLine));
    }

    /**
     * Dispatches a console message to the CloudNet master.
     *
     * @param message the message to dispatch.
     */
    public void dispatchConsoleMessage(String message) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "dispatchConsoleMessage",
                         String.format("Dispatching console message: %s%n", message));
        networkConnection.sendPacket(new PacketOutDispatchConsoleMessage(message));
    }

    /**
     * Sends the given command line to the console of the given server or proxy.
     *
     * @param defaultType the type of service (server or proxy) to send the command line to.
     * @param serverId    the server id of the service.
     * @param commandLine the command line to send.
     */
    public void sendConsoleMessage(DefaultType defaultType, String serverId, String commandLine) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "sendConsoleMessage",
                         String.format("Sending console message: %s %s %s%n", defaultType, serverId, commandLine));
        networkConnection.sendPacket(new PacketOutServerDispatchCommand(defaultType, serverId, commandLine));
    }

    /**
     * @return a map containing all server groups mapped to their names.
     */
    public Map<String, SimpleServerGroup> getServerGroupMap() {
        return cloudNetwork.getServerGroups();
    }

    /**
     * @return a map containing all proxy groups mapped to their names.
     */
    public Map<String, ProxyGroup> getProxyGroupMap() {
        return cloudNetwork.getProxyGroups();
    }

    /**
     * Stops a game server with the given server id
     *
     * @param serverId the server id of the server to stop.
     */
    public void stopServer(String serverId) {
        this.logger.logp(Level.FINEST, this.getClass().getSimpleName(), "stopServer", String.format("Stopping server: %s%n", serverId));
        networkConnection.sendPacket(new PacketOutStopServer(serverId));
    }

    /**
     * Stops the proxy server with the given proxy id.
     *
     * @param proxyId the proxy id of the proxy server to stop.
     */
    public void stopProxy(String proxyId) {
        this.logger.logp(Level.FINEST, this.getClass().getSimpleName(), "stopProxy", String.format("Stopping proxy: %s%n", proxyId));
        networkConnection.sendPacket(new PacketOutStopProxy(proxyId));
    }

    /**
     * Creates a custom server log url for a service screen.
     *
     * @param serverId the id of the service.
     */
    public String createServerLogUrl(String serverId) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "createServerLogUrl",
                         String.format("Creating server log url: %s%n", serverId));
        String rnd = NetworkUtils.randomString(10);
        networkConnection.sendPacket(new PacketOutCreateServerLog(rnd, serverId));
        ConnectableAddress connectableAddress = cloudConfigLoader.loadConnection();
        return String.format("http://%s:%d/cloudnet/log?server=%s", connectableAddress.getHostName(), cloudNetwork.getWebPort(), rnd);
    }

    /**
     * Updates a player on the master.
     *
     * @param cloudPlayer the player to update.
     */
    public void updatePlayer(CloudPlayer cloudPlayer) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "updatePlayer",
                         String.format("Updating cloud player: %s%n", cloudPlayer));
        networkConnection.sendPacket(new PacketOutUpdatePlayer(CloudPlayer.newOfflinePlayer(cloudPlayer)));
    }

    /**
     * Updates an offline player on the master.
     *
     * @param offlinePlayer the offline player to update.
     */
    public void updatePlayer(OfflinePlayer offlinePlayer) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "updatePlayer",
                         String.format("Updating offline player: %s%n", offlinePlayer));
        networkConnection.sendPacket(new PacketOutUpdatePlayer(offlinePlayer));
    }

    /**
     * Collects and returns all currently running servers on the network.
     * When calling this function on a proxy, the cache is used.
     * On servers this queries the master.
     *
     * @return a collection containing all currently running servers.
     */
    public Collection<ServerInfo> getServers() {
        if (cloudService != null && cloudService.isProxyInstance()) {
            return new ArrayList<>(cloudService.getServers().values());
        }

        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetServers(), networkConnection);
        return result.getResult().getObject("serverInfos", SERVER_INFO_COLLECTION_TYPE);
    }

    /**
     * Queries the master for all running proxies.
     *
     * @return a collection containing all running proxies.
     */
    public Collection<ProxyInfo> getProxies() {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetProxies(), networkConnection);
        return result.getResult().getObject("proxyInfos", PROXY_INFO_COLLECTION_TYPE);
    }

    /**
     * Queries the master for all running proxies in the given group.
     *
     * @param group the name of the proxy group to get the proxies for.
     *
     * @return a collection containing all running proxies of the given proxy group.
     */
    public Collection<ProxyInfo> getProxies(String group) {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetProxies(group), networkConnection);
        return result.getResult().getObject("proxyInfos", PROXY_INFO_COLLECTION_TYPE);
    }

    /**
     * Returns all players currently online on the network.
     * This methods queries the master so it may take a short moment.
     *
     * @return all players currently online.
     */
    public Collection<CloudPlayer> getOnlinePlayers() {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetPlayers(), networkConnection);
        Collection<CloudPlayer> cloudPlayers = result.getResult().getObject("players", COLLECTION_CLOUDPLAYER_TYPE);

        if (cloudPlayers == null) {
            return Collections.emptyList();
        }

        return cloudPlayers;
    }

    /**
     * Returns an online player by their UUID.
     * If the player is not cached, the master is queried.
     *
     * @param uniqueId the UUID of the player.
     *
     * @return the online player or null, if the player is not currently online on the network.
     */
    public CloudPlayer getOnlinePlayer(UUID uniqueId) {
        if (cloudService != null) {
            CloudPlayer cloudPlayer = cloudService.getCachedPlayer(uniqueId);
            if (cloudPlayer != null) {
                return cloudPlayer;
            }
        }

        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetPlayer(uniqueId), networkConnection);
        return result.getResult().getObject("player", CloudPlayer.TYPE);
    }

    /**
     * Returns an offline player by their UUID.
     * If the player is not cached, the master is queried.
     *
     * @param uniqueId the UUID of the player.
     *
     * @return the offline player or null, if the player is not registered on the network.
     */
    public OfflinePlayer getOfflinePlayer(UUID uniqueId) {
        if (cloudService != null) {
            CloudPlayer cloudPlayer = cloudService.getCachedPlayer(uniqueId);
            if (cloudPlayer != null) {
                return cloudPlayer;
            }
        }

        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetOfflinePlayer(uniqueId), networkConnection);
        return result.getResult().getObject("player", OfflinePlayer.TYPE);
    }

    /**
     * Returns an offline player by their exact name.
     * If the player is not cached, the master is queried.
     *
     * @param name the exact name of the player.
     *
     * @return the offline player or null, if the player is not registered on the network.
     */
    public OfflinePlayer getOfflinePlayer(String name) {
        if (cloudService != null) {
            CloudPlayer cloudPlayer = cloudService.getCachedPlayer(name);
            if (cloudPlayer != null) {
                return cloudPlayer;
            }
        }

        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetOfflinePlayer(name), networkConnection);
        return result.getResult().getObject("player", OfflinePlayer.TYPE);
    }

    /**
     * Queries the master for a server group based on the given name.
     *
     * @param name the name of the server group.
     *
     * @return the server group object or null, if it doesn't exist.
     */
    public ServerGroup getServerGroup(String name) {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetServerGroup(name), networkConnection);
        return result.getResult().getObject("serverGroup", ServerGroup.TYPE);
    }

    /**
     * Queries the unique id of the player with the given name.
     *
     * @param name the name of the player; case-insensitive.
     *
     * @return the unique id of the player with the given name or {@code null},
     * if the player is not registered on the network.
     */
    public UUID getPlayerUniqueId(String name) {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutNameUUID(name), networkConnection);
        return result.getResult().getObject("uniqueId", UUID.class);
    }

    /**
     * Queries the name of the player with the given unique id.
     *
     * @param uniqueId the unique id of the player.
     *
     * @return the name of the player with the given unique id or {@code null},
     * if the player is not registered on the network.
     */
    public String getPlayerName(UUID uniqueId) {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutNameUUID(uniqueId), networkConnection);
        return result.getResult().getString("name");
    }

    /**
     * Queries the master for the server information about a specific server.
     *
     * @param serverId the server id to query for.
     *
     * @return the server information of the server with the given sever id.
     */
    public ServerInfo getServerInfo(String serverId) {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetServer(serverId), networkConnection);
        return result.getResult().getObject("serverInfo", ServerInfo.TYPE);
    }

    /**
     * Returns a document with all collected statistics.
     * This method queries the master.
     *
     * @return a document with collected statistics.
     */
    public Document getStatistics() {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetStatistic(), networkConnection);
        return result.getResult();
    }

    /**
     * Copies the given directory from the currently running server to its template.
     * This is done by requesting the master to handle the instructions for the wrapper.
     *
     * @param serverInfo the information about the currently running server.
     * @param directory  the directory which will be copied to the running server's template.
     */
    public void copyDirectory(ServerInfo serverInfo, String directory) {
        if (serverInfo == null || directory == null) {
            throw new NullPointerException("serverInfo or directory is null");
        }

        networkConnection.sendPacket(new PacketOutCopyDirectory(serverInfo, directory));
    }

    /**
     * Unsafe Method
     */
    @Deprecated
    private Map<UUID, OfflinePlayer> getRegisteredPlayers() {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetRegisteredPlayers(), networkConnection);

        if (result.getResult() != null) {
            return result.getResult().getObject("players", MAP_UUID_OFFLINEPLAYER_TYPE);
        }

        return new HashMap<>();
    }

    /**
     * The clouds private logger. Only use this in internal CloudNet code, plugin developers
     * should use their own plugin's logger.
     *
     * @return the logger for CloudNet's plugin.
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * This method determines, whether debug messages can be logged.
     *
     * @return whether or not the debug mode is enabled.
     */
    public boolean isDebug() {
        return logger.isLoggable(Level.FINEST);
    }

    /**
     * Enable or disable debugging output on the cloud logger instance.
     * This modifies the logger.
     *
     * @param debug whether to output debug information.
     */
    public void setDebug(boolean debug) {
        if (debug) {
            logger.setLevel(Level.ALL);
        } else {
            logger.setLevel(Level.INFO);
        }
    }
}
