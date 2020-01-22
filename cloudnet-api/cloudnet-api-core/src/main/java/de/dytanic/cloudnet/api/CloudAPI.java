package de.dytanic.cloudnet.api;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.config.CloudConfigLoader;
import de.dytanic.cloudnet.api.database.DatabaseManager;
import de.dytanic.cloudnet.api.handlers.NetworkHandlerProvider;
import de.dytanic.cloudnet.api.network.packet.api.*;
import de.dytanic.cloudnet.api.network.packet.api.sync.*;
import de.dytanic.cloudnet.api.network.packet.in.*;
import de.dytanic.cloudnet.api.network.packet.out.*;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.NetDispatcher;
import de.dytanic.cloudnet.lib.network.NetworkConnection;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.network.auth.Auth;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketManager;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.result.Result;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.player.permission.PermissionPool;
import de.dytanic.cloudnet.lib.server.*;
import de.dytanic.cloudnet.lib.server.defaults.BasicServerConfig;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class CloudAPI {

    public static final Type MAP_UUID_OFFLINEPLAYER_TYPE = TypeToken.getParameterized(Map.class, UUID.class, OfflinePlayer.TYPE).getType();
    private static final Type SERVER_INFO_COLLECTION_TYPE = TypeToken.getParameterized(Collection.class, ServerInfo.class).getType();
    private static final Type PROXY_INFO_COLLECTION_TYPE = TypeToken.getParameterized(Collection.class, ProxyInfo.class).getType();
    private static final Type COLLECTION_CLOUDPLAYER_TYPE = TypeToken.getParameterized(Collection.class, CloudPlayer.TYPE).getType();
    private static final String[] EMPTY_STRING_ARRAY = {};
    private static CloudAPI instance;

    private Document config;
    private ServiceId serviceId;
    private CloudConfigLoader cloudConfigLoader;

    private NetworkConnection networkConnection;
    private int memory;

    private CloudService cloudService = null;

    //Init
    private CloudNetwork cloudNetwork = new CloudNetwork();
    private NetworkHandlerProvider networkHandlerProvider = new NetworkHandlerProvider();
    private DatabaseManager databaseManager = new DatabaseManager();

    /**
     * Logger instance set by the respective bootstrap.
     * Don't use in constructor!
     */
    private Logger logger;

    public CloudAPI(CloudConfigLoader loader) {
        if (instance != null) {
            throw new IllegalStateException("CloudAPI already instantiated.");
        }
        instance = this;
        this.cloudConfigLoader = loader;
        this.config = loader.loadConfig();
        this.networkConnection = new NetworkConnection(loader.loadConnnection(),
                                                       new ConnectableAddress("0.0.0.0", 0));
        this.serviceId = config.getObject("serviceId", ServiceId.TYPE);
        this.memory = config.getInt("memory");

        initDefaultHandlers();
    }

    /*================= Internal =====================*/

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

    /*================= Internal =====================*/

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
        this.networkConnection.tryConnect(config.getBoolean("ssl"),
                                          new NetDispatcher(networkConnection, false),
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

    public CloudService getCloudService() {
        return cloudService;
    }

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
     * Returns the wingui Config
     */
    public Document getConfig() {
        return config;
    }

    /**
     * Returns a simple cloudnetwork information base
     */
    public CloudNetwork getCloudNetwork() {
        return cloudNetwork;
    }

    /**
     * Internal CloudNetwork update set
     *
     * @param cloudNetwork
     */
    public void setCloudNetwork(CloudNetwork cloudNetwork) {
        this.cloudNetwork = cloudNetwork;
    }

    /**
     * Returns the network server manager from cloudnet
     */
    public NetworkHandlerProvider getNetworkHandlerProvider() {
        return networkHandlerProvider;
    }

    /**
     * Returns the internal network connection to the cloudnet root
     */
    public NetworkConnection getNetworkConnection() {
        return networkConnection;
    }

    /**
     * Returns the cloud prefix
     */
    public String getPrefix() {
        return cloudNetwork.getMessages().getString("prefix");
    }

    /**
     * Returns the memory from this instance calc by Wrapper
     */
    public int getMemory() {
        return memory;
    }

    /**
     * Returns the ServiceId from this instance
     */
    public ServiceId getServiceId() {
        return serviceId;
    }

    /**
     * Returns the Database Manager for the CloudNetDB functions
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    /**
     * Returns the group name from this instance
     */
    public String getGroup() {
        return serviceId.getGroup();
    }

    /**
     * Returns the UUID from this instance
     */
    public UUID getUniqueId() {
        return serviceId.getUniqueId();
    }

    /**
     * Returns the serverId (Lobby-1)
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
     * Returns the wrapperid from this instance
     */
    public String getWrapperId() {
        return serviceId.getWrapperId();
    }

    /**
     * Returns the SimpleServerGroup of the parameter
     *
     * @param group
     */
    public SimpleServerGroup getServerGroupData(String group) {
        return cloudNetwork.getServerGroups().get(group);
    }

    /**
     * Returns the ProxyGroup of the parameter
     *
     * @param group
     */
    public ProxyGroup getProxyGroupData(String group) {
        return cloudNetwork.getProxyGroups().get(group);
    }

    /**
     * Returns the global onlineCount
     */
    public int getOnlineCount() {
        return cloudNetwork.getOnlineCount();
    }

    /**
     * Returns the amount of players that are registered in the Cloud
     */
    public int getRegisteredPlayerCount() {
        return cloudNetwork.getRegisteredPlayerCount();
    }

    /**
     * Returns all the module properties
     *
     * @return
     */
    public Document getModuleProperties() {
        return cloudNetwork.getModules();
    }

    /**
     * Returns the permissionPool of the cloudnetwork
     */
    public PermissionPool getPermissionPool() {
        return cloudNetwork.getModules().getObject("permissionPool", PermissionPool.TYPE);
    }

    /**
     * Returns all active wrappers on cloudnet
     */
    public Collection<WrapperInfo> getWrappers() {
        return cloudNetwork.getWrappers();
    }

    /**
     * Returns the permission group from the permissions-system
     */
    public PermissionGroup getPermissionGroup(String group) {
        if (cloudNetwork.getModules().contains("permissionPool")) {
            return ((PermissionPool) cloudNetwork.getModules().getObject("permissionPool", PermissionPool.TYPE)).getGroups().get(group);
        }
        return null;
    }

    /**
     * Finds the first wrapper with the given case-insensitive name.
     * If no wrapper with the given id can be found, this returns null
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
     * Sends the data of the custom channel message to all proxys
     */
    public void sendCustomSubProxyMessage(String channel, String message, Document value) {
        networkConnection.sendPacket(new PacketOutCustomSubChannelMessage(DefaultType.BUNGEE_CORD, channel, message, value));
    }

    /**
     * Sends the data of the custom channel message to all server
     */
    public void sendCustomSubServerMessage(String channel, String message, Document value) {
        networkConnection.sendPacket(new PacketOutCustomSubChannelMessage(DefaultType.BUKKIT, channel, message, value));
    }

    /**
     * Sends the data of the custom channel message to one server
     */
    public void sendCustomSubServerMessage(String channel, String message, Document value, String serverName) {
        networkConnection.sendPacket(new PacketOutCustomSubChannelMessage(DefaultType.BUKKIT, serverName, channel, message, value));
    }

    /**
     * Sends the data of the custom channel message to proxy server
     */
    public void sendCustomSubProxyMessage(String channel, String message, Document value, String serverName) {
        networkConnection.sendPacket(new PacketOutCustomSubChannelMessage(DefaultType.BUNGEE_CORD, serverName, channel, message, value));
    }

    /**
     * Update the server group
     *
     * @param serverGroup
     */
    public void updateServerGroup(ServerGroup serverGroup) {
        networkConnection.sendPacket(new PacketOutUpdateServerGroup(serverGroup));
    }

    /**
     * Update the permission group
     */
    public void updatePermissionGroup(PermissionGroup permissionGroup) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "updatePermissionGroup",
                         String.format("Updating permission group: %s%n", permissionGroup));
        networkConnection.sendPacket(new PacketOutUpdatePermissionGroup(permissionGroup));
    }

    /**
     * Update the proxy group
     *
     * @param proxyGroup
     */
    public void updateProxyGroup(ProxyGroup proxyGroup) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "updateProxyGroup",
                         String.format("Updating proxy group: %s%n", proxyGroup));
        networkConnection.sendPacket(new PacketOutUpdateProxyGroup(proxyGroup));
    }

    /**
     * Dispatch a command on cloudnet-core
     */
    public void sendCloudCommand(String commandLine) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "sendCloudCommand",
                         String.format("Sending cloud command: %s%n", commandLine));
        networkConnection.sendPacket(new PacketOutExecuteCommand(commandLine));
    }

    /**
     * Dispatch a console message
     *
     * @param output
     */
    public void dispatchConsoleMessage(String output) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "dispatchConsoleMessage",
                         String.format("Dispatching console message: %s%n", output));
        networkConnection.sendPacket(new PacketOutDispatchConsoleMessage(output));
    }

    /**
     * Writes into the console of the server/proxy the command line
     *
     * @param defaultType
     * @param serverId
     * @param commandLine
     */
    public void sendConsoleMessage(DefaultType defaultType, String serverId, String commandLine) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "sendConsoleMessage",
                         String.format("Sending console message: %s %s %s%n", defaultType, serverId, commandLine));
        networkConnection.sendPacket(new PacketOutServerDispatchCommand(defaultType, serverId, commandLine));
    }

    public Map<String, SimpleServerGroup> getServerGroupMap() {
        return cloudNetwork.getServerGroups();
    }

    public Map<String, ProxyGroup> getProxyGroupMap() {
        return cloudNetwork.getProxyGroups();
    }

    /**
     * Stop a game server with the parameter of the serverId
     *
     * @param serverId the server-id to stop
     */
    public void stopServer(String serverId) {
        this.logger.logp(Level.FINEST, this.getClass().getSimpleName(), "stopServer", String.format("Stopping server: %s%n", serverId));
        networkConnection.sendPacket(new PacketOutStopServer(serverId));
    }

    /*=====================================================================================*/

    /**
     * Stop a BungeeCord proxy server with the id @proxyId
     */
    public void stopProxy(String proxyId) {
        this.logger.logp(Level.FINEST, this.getClass().getSimpleName(), "stopProxy", String.format("Stopping proxy: %s%n", proxyId));
        networkConnection.sendPacket(new PacketOutStopProxy(proxyId));
    }

    /**
     * Creates a custom server log url for one server screen
     */
    public String createServerLogUrl(String serverId) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "createServerLogUrl",
                         String.format("Creating server log url: %s%n", serverId));
        String rnd = NetworkUtils.randomString(10);
        networkConnection.sendPacket(new PacketOutCreateServerLog(rnd, serverId));
        ConnectableAddress connectableAddress = cloudConfigLoader.loadConnnection();
        return new StringBuilder(config.getBoolean("ssl") ? "https://" : "http://").append(connectableAddress.getHostName())
                                                                                   .append(':')
                                                                                   .append(cloudNetwork.getWebPort())
                                                                                   .append("/cloudnet/log?server=")
                                                                                   .append(rnd)
                                                                                   .substring(0);
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(ProxyGroup proxyGroup) {
        startProxy(proxyGroup, proxyGroup.getMemory(), EMPTY_STRING_ARRAY);
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(ProxyGroup proxyGroup, int memory, String[] processParameters) {
        startProxy(proxyGroup, memory, processParameters, null, new ArrayList<>(), new Document());
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(ProxyGroup proxyGroup,
                           int memory,
                           String[] processParameters,
                           String url,
                           Collection<ServerInstallablePlugin> plugins,
                           Document properties) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "startProxy",
                         String.format("Starting proxy: %s, %d, %s, %s, %s, %s%n",
                                       proxyGroup,
                                       memory,
                                       Arrays.toString(processParameters),
                                       url,
                                       plugins,
                                       properties));
        networkConnection.sendPacket(new PacketOutStartProxy(proxyGroup, memory, processParameters, url, plugins, properties));
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(ProxyGroup proxyGroup, int memory, String[] processParameters, Document document) {
        startProxy(proxyGroup, memory, processParameters, null, new ArrayList<>(), document);
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(WrapperInfo wrapperInfo, ProxyGroup proxyGroup) {
        startProxy(wrapperInfo, proxyGroup, proxyGroup.getMemory(), EMPTY_STRING_ARRAY);
    }

    /*=====================================================================================*/

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(WrapperInfo wrapperInfo, ProxyGroup proxyGroup, int memory, String[] processParameters) {
        startProxy(wrapperInfo, proxyGroup, memory, processParameters, null, new ArrayList<>(), new Document());
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(WrapperInfo wrapperInfo,
                           ProxyGroup proxyGroup,
                           int memory,
                           String[] processParameters,
                           String url,
                           Collection<ServerInstallablePlugin> plugins,
                           Document properties) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "startProxy",
                         String.format("Starting proxy: %s, %s, %d, %s, %s, %s, %s%n",
                                       wrapperInfo,
                                       proxyGroup,
                                       memory,
                                       Arrays.toString(processParameters),
                                       url,
                                       plugins,
                                       properties));
        networkConnection.sendPacket(new PacketOutStartProxy(wrapperInfo.getServerId(),
                                                             proxyGroup,
                                                             memory,
                                                             processParameters,
                                                             url,
                                                             plugins,
                                                             properties));
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup
     */
    public void startProxy(WrapperInfo wrapperInfo, ProxyGroup proxyGroup, int memory, String[] processParameters, Document document) {
        startProxy(wrapperInfo, proxyGroup, memory, processParameters, null, new ArrayList<>(), document);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup) {
        startGameServer(simpleServerGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()));
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig) {
        startGameServer(simpleServerGroup, serverConfig, false);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, boolean priorityStop) {
        startGameServer(simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, int memory, boolean priorityStop) {
        startGameServer(simpleServerGroup, serverConfig, memory, priorityStop, new Properties());
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Properties properties) {
        startGameServer(simpleServerGroup,
                        serverConfig,
                        memory,
                        EMPTY_STRING_ARRAY,
                        null,
                        null,
                        false,
                        priorityStop,
                        properties,
                        null,
                        new ArrayList<>());
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup
     * @param serverConfig
     * @param memory
     * @param processParameters
     * @param template
     * @param onlineMode
     * @param priorityStop
     * @param properties
     * @param url
     * @param plugins
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                String[] processParameters,
                                Template template,
                                String customServerName,
                                boolean onlineMode,
                                boolean priorityStop,
                                Properties properties,
                                String url,
                                Collection<ServerInstallablePlugin> plugins) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "startGameServer",
                         String.format("Starting game server: %s, %s, %d, %s, %s, %s, %s, %s, %s, %s, %s%n",
                                       simpleServerGroup,
                                       serverConfig,
                                       memory,
                                       Arrays.toString(processParameters),
                                       template,
                                       customServerName,
                                       onlineMode,
                                       priorityStop,
                                       properties,
                                       url,
                                       plugins));
        networkConnection.sendPacket(new PacketOutStartServer(simpleServerGroup.getName(),
                                                              memory,
                                                              serverConfig,
                                                              properties,
                                                              priorityStop,
                                                              processParameters,
                                                              template,
                                                              customServerName,
                                                              onlineMode,
                                                              plugins,
                                                              url));
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, String serverId) {
        startGameServer(simpleServerGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()), serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, String serverId) {
        startGameServer(simpleServerGroup, serverConfig, false, serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, boolean priorityStop, String serverId) {
        startGameServer(simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop, serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                String serverId) {
        startGameServer(simpleServerGroup, serverConfig, memory, priorityStop, new Properties(), serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Properties properties,
                                String serverId) {
        startGameServer(simpleServerGroup,
                        serverConfig,
                        memory,
                        EMPTY_STRING_ARRAY,
                        null,
                        null,
                        false,
                        priorityStop,
                        properties,
                        null,
                        new ArrayList<>(),
                        serverId);
    }

    /*==================================================================*/

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup
     * @param serverConfig
     * @param memory
     * @param processParameters
     * @param template
     * @param onlineMode
     * @param priorityStop
     * @param properties
     * @param url
     * @param plugins
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                String[] processParameters,
                                Template template,
                                String customServerName,
                                boolean onlineMode,
                                boolean priorityStop,
                                Properties properties,
                                String url,
                                Collection<ServerInstallablePlugin> plugins,
                                String serverId) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "startGameServer",
                         String.format("Starting game server: %s, %s, %d, %s, %s, %s, %s, %s, %s, %s, %s, %s%n",
                                       simpleServerGroup,
                                       serverConfig,
                                       memory,
                                       Arrays.toString(processParameters),
                                       template,
                                       customServerName,
                                       onlineMode,
                                       priorityStop,
                                       properties,
                                       url,
                                       plugins,
                                       serverId));
        networkConnection.sendPacket(new PacketOutStartServer(simpleServerGroup.getName(),
                                                              memory,
                                                              serverConfig,
                                                              properties,
                                                              priorityStop,
                                                              processParameters,
                                                              template,
                                                              customServerName,
                                                              onlineMode,
                                                              plugins,
                                                              url));
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, Template template) {
        startGameServer(simpleServerGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()), template);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, Template template) {
        startGameServer(simpleServerGroup, serverConfig, false, template);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, boolean priorityStop, Template template) {
        startGameServer(simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop, template);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Template template) {
        startGameServer(simpleServerGroup, serverConfig, memory, priorityStop, new Properties(), template);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Properties properties,
                                Template template) {
        startGameServer(simpleServerGroup,
                        serverConfig,
                        memory,
                        EMPTY_STRING_ARRAY,
                        template,
                        null,
                        false,
                        priorityStop,
                        properties,
                        null,
                        new ArrayList<>());
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, Template template, String serverId) {
        startGameServer(simpleServerGroup, serverConfig, false, template, serverId);
    }

    /*==================================================================*/

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                boolean priorityStop,
                                Template template,
                                String serverId) {
        startGameServer(simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop, template, serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Template template,
                                String serverId) {
        startGameServer(simpleServerGroup, serverConfig, memory, priorityStop, new Properties(), template, serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Properties properties,
                                Template template,
                                String serverId) {
        startGameServer(simpleServerGroup,
                        serverConfig,
                        memory,
                        EMPTY_STRING_ARRAY,
                        template,
                        null,
                        false,
                        priorityStop,
                        properties,
                        null,
                        new ArrayList<>(),
                        serverId);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(WrapperInfo wrapperInfo, SimpleServerGroup simpleServerGroup) {
        startGameServer(wrapperInfo, simpleServerGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()));
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(WrapperInfo wrapperInfo, SimpleServerGroup simpleServerGroup, ServerConfig serverConfig) {
        startGameServer(wrapperInfo, simpleServerGroup, serverConfig, false);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                boolean priorityStop) {
        startGameServer(wrapperInfo, simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop);
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop) {
        startGameServer(wrapperInfo, simpleServerGroup, serverConfig, memory, priorityStop, new Properties());
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Properties properties) {
        startGameServer(wrapperInfo,
                        simpleServerGroup,
                        serverConfig,
                        memory,
                        EMPTY_STRING_ARRAY,
                        null,
                        null,
                        false,
                        priorityStop,
                        properties,
                        null,
                        new ArrayList<>());
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup
     * @param serverConfig
     * @param memory
     * @param processParameters
     * @param template
     * @param onlineMode
     * @param priorityStop
     * @param properties
     * @param url
     * @param plugins
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                String[] processParameters,
                                Template template,
                                String customServerName,
                                boolean onlineMode,
                                boolean priorityStop,
                                Properties properties,
                                String url,
                                Collection<ServerInstallablePlugin> plugins) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "startGameServer",
                         String.format("Starting game server: %s, %s, %s, %d, %s, %s, %s, %s, %s, %s, %s, %s%n",
                                       wrapperInfo,
                                       simpleServerGroup,
                                       serverConfig,
                                       memory,
                                       Arrays.toString(processParameters),
                                       template,
                                       customServerName,
                                       onlineMode,
                                       priorityStop,
                                       properties,
                                       url,
                                       plugins));
        networkConnection.sendPacket(new PacketOutStartServer(wrapperInfo,
                                                              simpleServerGroup.getName(),
                                                              memory,
                                                              serverConfig,
                                                              properties,
                                                              priorityStop,
                                                              processParameters,
                                                              template,
                                                              customServerName,
                                                              onlineMode,
                                                              plugins,
                                                              url));
    }

    /**
     * Start a game server
     *
     * @param simpleServerGroup
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Properties properties,
                                Template template) {
        startGameServer(wrapperInfo,
                        simpleServerGroup,
                        serverConfig,
                        memory,
                        EMPTY_STRING_ARRAY,
                        template,
                        null,
                        false,
                        priorityStop,
                        properties,
                        null,
                        new ArrayList<>());
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup
     * @param serverConfig
     * @param memory
     * @param processParameters
     * @param template
     * @param onlineMode
     * @param priorityStop
     * @param properties
     * @param url
     * @param plugins
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                String serverId,
                                ServerConfig serverConfig,
                                int memory,
                                String[] processParameters,
                                Template template,
                                String customServerName,
                                boolean onlineMode,
                                boolean priorityStop,
                                Properties properties,
                                String url,
                                Collection<ServerInstallablePlugin> plugins) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "startGameServer",
                         String.format("Starting game server: %s, %s, %s, %s, %d, %s, %s, %s, %s, %s, %s, %s, %s%n",
                                       wrapperInfo,
                                       simpleServerGroup,
                                       serverId,
                                       serverConfig,
                                       memory,
                                       Arrays.toString(processParameters),
                                       template,
                                       customServerName,
                                       onlineMode,
                                       priorityStop,
                                       properties,
                                       url,
                                       plugins));
        networkConnection.sendPacket(new PacketOutStartServer(wrapperInfo,
                                                              simpleServerGroup.getName(),
                                                              serverId,
                                                              memory,
                                                              serverConfig,
                                                              properties,
                                                              priorityStop,
                                                              processParameters,
                                                              template,
                                                              customServerName,
                                                              onlineMode,
                                                              plugins,
                                                              url));
    }

    /**
     * Start a Cloud-Server with those Properties
     */
    public void startCloudServer(WrapperInfo wrapperInfo, String serverName, int memory, boolean priorityStop) {
        startCloudServer(wrapperInfo, serverName, new BasicServerConfig(), memory, priorityStop);
    }

    /**
     * Start a Cloud-Server with those Properties
     */
    public void startCloudServer(WrapperInfo wrapperInfo, String serverName, ServerConfig serverConfig, int memory, boolean priorityStop) {
        startCloudServer(wrapperInfo,
                         serverName,
                         serverConfig,
                         memory,
                         priorityStop,
                         CloudAPI.EMPTY_STRING_ARRAY,
                         new ArrayList<>(),
                         new Properties(),
                         ServerGroupType.BUKKIT);
    }

    /**
     * Start a Cloud-Server with those Properties
     */
    public void startCloudServer(WrapperInfo wrapperInfo,
                                 String serverName,
                                 ServerConfig serverConfig,
                                 int memory,
                                 boolean priorityStop,
                                 String[] processPreParameters,
                                 Collection<ServerInstallablePlugin> plugins,
                                 Properties properties,
                                 ServerGroupType serverGroupType) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "startCloudServer",
                         String.format("Starting cloud server: %s, %s, %s, %d, %s, %s, %s, %s, %s%n",
                                       wrapperInfo,
                                       serverName,
                                       serverConfig,
                                       memory,
                                       priorityStop,
                                       Arrays.toString(processPreParameters),
                                       plugins,
                                       properties,
                                       serverGroupType));
        networkConnection.sendPacket(new PacketOutStartCloudServer(wrapperInfo,
                                                                   serverName,
                                                                   serverConfig,
                                                                   memory,
                                                                   priorityStop,
                                                                   processPreParameters,
                                                                   plugins,
                                                                   properties,
                                                                   serverGroupType));
    }

    /**
     * Start a Cloud-Server with those Properties
     */
    public void startCloudServer(String serverName, int memory, boolean priorityStop) {
        startCloudServer(serverName, new BasicServerConfig(), memory, priorityStop);
    }

    /**
     * Start a Cloud-Server with those Properties
     */
    public void startCloudServer(String serverName, ServerConfig serverConfig, int memory, boolean priorityStop) {
        startCloudServer(serverName,
                         serverConfig,
                         memory,
                         priorityStop,
                         CloudAPI.EMPTY_STRING_ARRAY,
                         new ArrayList<>(),
                         new Properties(),
                         ServerGroupType.BUKKIT);
    }

    /*==========================================================================*/

    /**
     * Start a Cloud-Server with those Properties
     */
    public void startCloudServer(String serverName,
                                 ServerConfig serverConfig,
                                 int memory,
                                 boolean priorityStop,
                                 String[] processPreParameters,
                                 Collection<ServerInstallablePlugin> plugins,
                                 Properties properties,
                                 ServerGroupType serverGroupType) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "startCloudServer",
                         String.format("Starting cloud server: %s, %s, %d, %s, %s, %s, %s, %s%n",
                                       serverName,
                                       serverConfig,
                                       memory,
                                       priorityStop,
                                       Arrays.toString(processPreParameters),
                                       plugins,
                                       properties,
                                       serverGroupType));
        networkConnection.sendPacket(new PacketOutStartCloudServer(serverName,
                                                                   serverConfig,
                                                                   memory,
                                                                   priorityStop,
                                                                   processPreParameters,
                                                                   plugins,
                                                                   properties,
                                                                   serverGroupType));
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
     * Queries the master for all running cloud servers on the network.
     * Cloud servers are servers, which do not belong to any server group.
     *
     * @return a collection containing all running cloud servers.
     */
    public Collection<ServerInfo> getCloudServers() {
        if (cloudService != null && cloudService.isProxyInstance()) {
            return cloudService.getServers().values().stream()
                               .filter(serverInfo -> serverInfo.getServiceId().getGroup() == null)
                               .collect(Collectors.toList());
        }

        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetCloudServers(), networkConnection);
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

        for (CloudPlayer cloudPlayer : cloudPlayers) {
            cloudPlayer.setPlayerExecutor(PlayerExecutorBridge.INSTANCE);
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
        CloudPlayer cloudPlayer = result.getResult().getObject("player", CloudPlayer.TYPE);
        if (cloudPlayer == null) {
            return null;
        }
        cloudPlayer.setPlayerExecutor(PlayerExecutorBridge.INSTANCE);
        return cloudPlayer;
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
     * @return the server group object.
     */
    public ServerGroup getServerGroup(String name) {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetServerGroup(name), networkConnection);
        return result.getResult().getObject("serverGroup", ServerGroup.TYPE);
    }

    /**
     * Queries the unique id of the player with the given name.
     *
     * @param name the unique id of the player.
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

    /*================================================================================*/

    /**
     *
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

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public boolean isDebug() {
        return logger.isLoggable(Level.FINEST);
    }

    public void setDebug(boolean debug) {
        if (debug) {
            logger.setLevel(Level.ALL);
        } else {
            logger.setLevel(Level.INFO);
        }
    }
}
