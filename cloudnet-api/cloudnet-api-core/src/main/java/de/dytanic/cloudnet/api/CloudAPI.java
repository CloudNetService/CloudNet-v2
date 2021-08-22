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
import de.dytanic.cloudnet.lib.interfaces.MetaObj;
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
import de.dytanic.cloudnet.lib.scheduler.TaskScheduler;
import de.dytanic.cloudnet.lib.server.*;
import de.dytanic.cloudnet.lib.server.defaults.BasicServerConfig;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CloudAPI implements MetaObj {

    private static CloudAPI instance;

    private Document config;
    private ServiceId serviceId;
    private CloudConfigLoader cloudConfigLoader;

    private NetworkConnection networkConnection;
    private int memory;
    private Runnable shutdownTask;

    private ICloudService cloudService = null;

    //Init
    private CloudNetwork cloudNetwork = new CloudNetwork();
    private NetworkHandlerProvider networkHandlerProvider = new NetworkHandlerProvider();
    private DatabaseManager databaseManager = new DatabaseManager();

    /**
     * Logger instance set by the respective bootstrap.
     * Don't use in constructor!
     */
    private Logger logger;

    public CloudAPI(CloudConfigLoader loader, Runnable cancelTask) {
        instance = this;
        this.cloudConfigLoader = loader;
        this.config = loader.loadConfig();
        this.networkConnection = new NetworkConnection(loader.loadConnnection());
        this.serviceId = config.getObject("serviceId", new TypeToken<ServiceId>() {}.getType());
        this.shutdownTask = cancelTask;
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
     * Returns the instance of the CloudAPI
     *
     * @return the instance of the CloudAPI
     */
    public static CloudAPI getInstance() {
        return instance;
    }

    @Deprecated
    public void bootstrap() {
        this.networkConnection.tryConnect(config.getBoolean("ssl"),
                                          new NetDispatcher(networkConnection, false),
                                          new Auth(serviceId),
                                          shutdownTask);
        NetworkUtils.header();
    }

    @Deprecated
    public void shutdown() {
        TaskScheduler.runtimeScheduler().shutdown();
        this.networkConnection.tryDisconnect();
    }

    public CloudAPI update(ServerInfo serverInfo) {
        this.logger.logp(Level.FINEST, this.getClass().getSimpleName(), "update", String.format("Updating server info: %s", serverInfo));
        if (networkConnection.isConnected()) {
            networkConnection.sendPacket(new PacketOutUpdateServerInfo(serverInfo));
        }
        return this;
    }

    /*================= API =====================*/

    public CloudAPI update(ProxyInfo proxyInfo) {
        this.logger.logp(Level.FINEST, this.getClass().getSimpleName(), "update", String.format("Updating proxy info: %s", proxyInfo));
        if (networkConnection.isConnected()) {
            networkConnection.sendPacket(new PacketOutUpdateProxyInfo(proxyInfo));
        }
        return this;
    }

    /**
     * Returns synchronized the OnlineCount from the group
     *
     * @param group the name of the server group
     *
     * @return the amount of players currently online on the given server group
     */
    public int getOnlineCount(String group) {
        AtomicInteger integer = new AtomicInteger(0);
        CollectionWrapper.iterator(getServers(group), new Runnabled<ServerInfo>() {
            @Override
            public void run(ServerInfo obj) {
                integer.addAndGet(obj.getOnlineCount());
            }
        });
        return integer.get();
    }

    /**
     * Returns all serverInfos from group #group
     *
     * @param group the name of the server group
     *
     * @return a collection containing all servers of the given server group
     */
    public Collection<ServerInfo> getServers(String group) {
        if (cloudService != null && cloudService.isProxyInstance()) {
            return CollectionWrapper.filterMany(cloudService.getServers().values(), new Acceptable<ServerInfo>() {
                @Override
                public boolean isAccepted(ServerInfo serverInfo) {
                    return serverInfo.getServiceId().getGroup() != null && serverInfo.getServiceId().getGroup().equalsIgnoreCase(group);
                }
            });
        }

        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetServers(group), networkConnection);
        return result.getResult().getObject("serverInfos", new TypeToken<Collection<ServerInfo>>() {}.getType());
    }

    @Deprecated
    public ICloudService getCloudService() {
        return cloudService;
    }

    @Deprecated
    public void setCloudService(ICloudService cloudService) {
        this.cloudService = cloudService;
    }

    /**
     * Returns the Configuration Loader from this Plugin
     *
     * @return the loader for the configuration of this cloud instance
     */
    public CloudConfigLoader getCloudConfigLoader() {
        return cloudConfigLoader;
    }

    /**
     * Returns the configuration
     *
     * @return A document containing the current configuration
     */
    public Document getConfig() {
        return config;
    }

    /**
     * Returns a simple cloudnetwork information base
     *
     * @return An instance of the current cloud network state
     */
    public CloudNetwork getCloudNetwork() {
        return cloudNetwork;
    }

    /**
     * Internal CloudNetwork update set
     *
     * @param cloudNetwork the new cloud network state instance
     */
    public void setCloudNetwork(CloudNetwork cloudNetwork) {
        this.cloudNetwork = cloudNetwork;
    }

    /**
     * Returns the network server manager from cloudnet
     *
     * @return the provider for network handlers
     */
    public NetworkHandlerProvider getNetworkHandlerProvider() {
        return networkHandlerProvider;
    }

    /**
     * Returns the internal network connection to the cloudnet root
     *
     * @return the network connection to the cloudnet core
     */
    public NetworkConnection getNetworkConnection() {
        return networkConnection;
    }

    /**
     * Returns the cloud prefix
     *
     * @return The prefix for messages from the cloudnet process
     */
    public String getPrefix() {
        return cloudNetwork.getMessages().getString("prefix");
    }

    /**
     * Returns the memory from this instance calc by Wrapper
     *
     * @return the configured maximum heap memory in mb
     */
    public int getMemory() {
        return memory;
    }

    /**
     * Returns the shutdownTask which is default init
     *
     * @return the task to run, when this cloud instance is shut down
     */
    public Runnable getShutdownTask() {
        return shutdownTask;
    }

    /**
     * Returns the ServiceId from this instance
     *
     * @return the service id of this instance
     */
    public ServiceId getServiceId() {
        return serviceId;
    }

    /**
     * Returns the Database Manager for the CloudNetDB functions
     *
     * @return the database manager
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    /**
     * Returns the group name from this instance
     *
     * @return the name of the group of this running instance
     */
    public String getGroup() {
        return serviceId.getGroup();
    }

    /**
     * Returns the UUID from this instance
     *
     * @return the UUID of this running instance
     */
    public UUID getUniqueId() {
        return serviceId.getUniqueId();
    }

    /**
     * Returns the serverId (Lobby-1)
     *
     * @return the server id of this running instance
     */
    public String getServerId() {
        return serviceId.getServerId();
    }

    /**
     * Returns the Id (Lobby-1 -&gt; "1")
     *
     * @return the numerical id of this running instance
     */
    public int getGroupInitId() {
        return serviceId.getId();
    }

    /**
     * Returns the wrapperid from this instance
     *
     * @return the wrapper id of this running instance
     */
    public String getWrapperId() {
        return serviceId.getWrapperId();
    }

    /**
     * Returns the SimpleServerGroup of the parameter
     *
     * @param group the name of the server group
     *
     * @return the server group with the given name, if available
     */
    public SimpleServerGroup getServerGroupData(String group) {
        return cloudNetwork.getServerGroups().get(group);
    }

    /**
     * Returns the ProxyGroup of the parameter
     *
     * @param group the name of the proxy group
     *
     * @return the proxy group with the given name, if available
     */
    public ProxyGroup getProxyGroupData(String group) {
        return cloudNetwork.getProxyGroups().get(group);
    }

    /**
     * Returns the global onlineCount
     *
     * @return the amount of players online
     */
    public int getOnlineCount() {
        return cloudNetwork.getOnlineCount();
    }

    /**
     * Returns the amount of players that are registered in the Cloud
     *
     * @return the total number of players registered on the cloud network
     */
    public int getRegisteredPlayerCount() {
        return cloudNetwork.getRegisteredPlayerCount();
    }

    /**
     * Returns all the module properties
     *
     * @return a document with all module properties
     */
    public Document getModuleProperties() {
        return cloudNetwork.getModules();
    }

    /**
     * Returns the permissionPool of the cloudnetwork
     *
     * @return the permission pool
     */
    public PermissionPool getPermissionPool() {
        return cloudNetwork.getModules().getObject("permissionPool", PermissionPool.TYPE);
    }

    /**
     * Returns all active wrappers on cloudnet
     *
     * @return a collection of all wrappers
     */
    public Collection<WrapperInfo> getWrappers() {
        return cloudNetwork.getWrappers();
    }

    /**
     * Returns the permission group from the permissions-system
     *
     * @param group the name of the permission group
     *
     * @return the permission group with the given name, if available
     */
    public PermissionGroup getPermissionGroup(String group) {
        if (cloudNetwork.getModules().contains("permissionPool")) {
            return ((PermissionPool) cloudNetwork.getModules().getObject("permissionPool", PermissionPool.TYPE)).getGroups().get(group);
        }
        return null;
    }

    /**
     * Returns one of the wrapper infos
     *
     * @param wrapperId the id/name of the wrapper
     *
     * @return the wrapper with the given name
     */
    public WrapperInfo getWrapper(String wrapperId) {
        return CollectionWrapper.filter(cloudNetwork.getWrappers(), new Acceptable<WrapperInfo>() {
            @Override
            public boolean isAccepted(WrapperInfo value) {
                return value.getServerId().equalsIgnoreCase(wrapperId);
            }
        });
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
     * Update the permission group
     *
     * @param permissionGroup the permission group to update
     */
    public void updatePermissionGroup(PermissionGroup permissionGroup) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "updatePermissionGroup",
                         String.format("Updating permission group: %s", permissionGroup));
        networkConnection.sendPacket(new PacketOutUpdatePermissionGroup(permissionGroup));
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
     *
     * @return the generated url
     */
    public String createServerLogUrl(String serverId) {
        this.logger.logp(Level.FINEST,
                         this.getClass().getSimpleName(),
                         "createServerLogUrl",
                         String.format("Creating server log url: %s", serverId));
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
     * @param proxyGroup the proxy group
     */
    public void startProxy(ProxyGroup proxyGroup) {
        startProxy(proxyGroup, proxyGroup.getMemory(), new String[] {});
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup        the proxy group
     * @param memory            the amount of heap memory
     * @param processParameters the parameters for the process
     */
    public void startProxy(ProxyGroup proxyGroup, int memory, String[] processParameters) {
        startProxy(proxyGroup, memory, processParameters, null, new ArrayList<>(), new Document());
    }

    /**
     * Start a proxy server with a group
     *
     * @param proxyGroup        the proxy group
     * @param memory            the amount of heap memory
     * @param processParameters the parameters for the process
     * @param url               the url to download the template from
     * @param plugins           a collection of plugins that should be installed
     * @param properties        additional properties
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
                         String.format("Starting proxy: %s, %d, %s, %s, %s, %s",
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
     * @param proxyGroup        the proxy group
     * @param memory            the amount of heap memory
     * @param processParameters the parameters for the process
     * @param properties        additional properties
     */
    public void startProxy(ProxyGroup proxyGroup, int memory, String[] processParameters, Document properties) {
        startProxy(proxyGroup, memory, processParameters, null, new ArrayList<>(), properties);
    }

    /**
     * Start a proxy server with a group
     *
     * @param wrapperInfo the wrapper to start the proxy on
     * @param proxyGroup  the proxy group
     */
    public void startProxy(WrapperInfo wrapperInfo, ProxyGroup proxyGroup) {
        startProxy(wrapperInfo, proxyGroup, proxyGroup.getMemory(), new String[] {});
    }

    /*=====================================================================================*/

    /**
     * Start a proxy server with a group
     *
     * @param wrapperInfo       the wrapper to start the proxy on
     * @param proxyGroup        the proxy group
     * @param memory            the amount of heap memory
     * @param processParameters the parameters for the process
     */
    public void startProxy(WrapperInfo wrapperInfo, ProxyGroup proxyGroup, int memory, String[] processParameters) {
        startProxy(wrapperInfo, proxyGroup, memory, processParameters, null, new ArrayList<>(), new Document());
    }

    /**
     * Start a proxy server with a group
     *
     * @param wrapperInfo       the wrapper to start the proxy on
     * @param proxyGroup        the proxy group
     * @param memory            the amount of heap memory
     * @param processParameters the parameters for the process
     * @param url               the url to download the template from
     * @param plugins           a collection of plugins that should be installed
     * @param properties        additional properties
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
                         String.format("Starting proxy: %s, %s, %d, %s, %s, %s, %s",
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
     * @param wrapperInfo       the wrapper to start the proxy on
     * @param proxyGroup        the proxy group
     * @param memory            the amount of heap memory
     * @param processParameters the parameters for the process
     * @param properties        additional properties
     */
    public void startProxy(WrapperInfo wrapperInfo, ProxyGroup proxyGroup, int memory, String[] processParameters, Document properties) {
        startProxy(wrapperInfo, proxyGroup, memory, processParameters, null, new ArrayList<>(), properties);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup) {
        startGameServer(simpleServerGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()));
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig) {
        startGameServer(simpleServerGroup, serverConfig, false);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param priorityStop      whether priority stop is enabled
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, boolean priorityStop) {
        startGameServer(simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param priorityStop      whether priority stop is enabled
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, int memory, boolean priorityStop) {
        startGameServer(simpleServerGroup, serverConfig, memory, priorityStop, new Properties());
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param priorityStop      whether priority stop is enabled
     * @param properties        additional properties
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Properties properties) {
        startGameServer(simpleServerGroup,
                        serverConfig,
                        memory,
                        new String[] {},
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
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param processParameters the parameters for the process
     * @param template          the template to start
     * @param customServerName  the name of the server, null if default
     * @param onlineMode        whether online mode is enabled
     * @param priorityStop      whether priority stop is enabled
     * @param url               the url to download the template from
     * @param plugins           a collection of plugins that should be installed
     * @param properties        additional properties
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
                         String.format("Starting game server: %s, %s, %d, %s, %s, %s, %s, %s, %s, %s, %s",
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
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverId          a custom server id for this server
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, String serverId) {
        startGameServer(simpleServerGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()), serverId);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param serverId          a custom server id for this server
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, String serverId) {
        startGameServer(simpleServerGroup, serverConfig, false, serverId);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param priorityStop      whether priority stop is enabled
     * @param serverId          a custom server id for this server
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, boolean priorityStop, String serverId) {
        startGameServer(simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop, serverId);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param priorityStop      whether priority stop is enabled
     * @param serverId          a custom server id for this server
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                String serverId) {
        startGameServer(simpleServerGroup, serverConfig, memory, priorityStop, new Properties(), serverId);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param priorityStop      whether priority stop is enabled
     * @param properties        additional properties
     * @param serverId          a custom server id for this server
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
                        new String[] {},
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
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param processParameters the parameters for the process
     * @param template          the template to start
     * @param customServerName  the name of the server, null if default
     * @param onlineMode        whether online mode is enabled
     * @param priorityStop      whether priority stop is enabled
     * @param url               the url to download the template from
     * @param plugins           a collection of plugins that should be installed
     * @param properties        additional properties
     * @param serverId          a custom server id for this server
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
                         String.format("Starting game server: %s, %s, %d, %s, %s, %s, %s, %s, %s, %s, %s",
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
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param template          the template to start
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, Template template) {
        startGameServer(simpleServerGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()), template);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param template          the template to start
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, Template template) {
        startGameServer(simpleServerGroup, serverConfig, false, template);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param template          the template to start
     * @param priorityStop      whether priority stop is enabled
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, boolean priorityStop, Template template) {
        startGameServer(simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop, template);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param template          the template to start
     * @param priorityStop      whether priority stop is enabled
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop,
                                Template template) {
        startGameServer(simpleServerGroup, serverConfig, memory, priorityStop, new Properties(), template);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param template          the template to start
     * @param priorityStop      whether priority stop is enabled
     * @param properties        additional properties
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
                        new String[] {},
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
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param template          the template to start
     * @param serverId          a custom server id for this server
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup, ServerConfig serverConfig, Template template, String serverId) {
        startGameServer(simpleServerGroup, serverConfig, false, template, serverId);
    }

    /*==================================================================*/

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param template          the template to start
     * @param priorityStop      whether priority stop is enabled
     * @param serverId          a custom server id for this server
     */
    public void startGameServer(SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                boolean priorityStop,
                                Template template,
                                String serverId) {
        startGameServer(simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop, template, serverId);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param template          the template to start
     * @param priorityStop      whether priority stop is enabled
     * @param serverId          a custom server id for this server
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
     * Start a new game server with full parameters
     *
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param template          the template to start
     * @param priorityStop      whether priority stop is enabled
     * @param properties        additional properties
     * @param serverId          a custom server id for this server
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
                        new String[] {},
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
     * Start a new game server with full parameters
     *
     * @param wrapperInfo       the wrapper to start this server on
     * @param simpleServerGroup the server group to start
     */
    public void startGameServer(WrapperInfo wrapperInfo, SimpleServerGroup simpleServerGroup) {
        startGameServer(wrapperInfo, simpleServerGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()));
    }

    /**
     * Start a new game server with full parameters
     *
     * @param wrapperInfo       the wrapper to start this server on
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     */
    public void startGameServer(WrapperInfo wrapperInfo, SimpleServerGroup simpleServerGroup, ServerConfig serverConfig) {
        startGameServer(wrapperInfo, simpleServerGroup, serverConfig, false);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param wrapperInfo       the wrapper to start this server on
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param priorityStop      whether priority stop is enabled
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                boolean priorityStop) {
        startGameServer(wrapperInfo, simpleServerGroup, serverConfig, simpleServerGroup.getMemory(), priorityStop);
    }

    /**
     * Start a new game server with full parameters
     *
     * @param wrapperInfo       the wrapper to start this server on
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param priorityStop      whether priority stop is enabled
     */
    public void startGameServer(WrapperInfo wrapperInfo,
                                SimpleServerGroup simpleServerGroup,
                                ServerConfig serverConfig,
                                int memory,
                                boolean priorityStop) {
        startGameServer(wrapperInfo, simpleServerGroup, serverConfig, memory, priorityStop, new Properties());
    }

    /**
     * Start a new game server with full parameters
     *
     * @param wrapperInfo       the wrapper to start this server on
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param priorityStop      whether priority stop is enabled
     * @param properties        additional properties
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
                        new String[] {},
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
     * @param wrapperInfo       the wrapper to start this server on
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param processParameters the parameters for the process
     * @param template          the template to start
     * @param customServerName  the name of the server, null if default
     * @param onlineMode        whether online mode is enabled
     * @param priorityStop      whether priority stop is enabled
     * @param url               the url to download the template from
     * @param plugins           a collection of plugins that should be installed
     * @param properties        additional properties
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
                         String.format("Starting game server: %s, %s, %s, %d, %s, %s, %s, %s, %s, %s, %s, %s",
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
     * Start a new game server with full parameters
     *
     * @param wrapperInfo       the wrapper to start this server on
     * @param simpleServerGroup the server group to start
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param template          the template to start
     * @param priorityStop      whether priority stop is enabled
     * @param properties        additional properties
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
                        new String[] {},
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
     * @param wrapperInfo       the wrapper to start this server on
     * @param simpleServerGroup the server group to start
     * @param serverId          the custom server id for this server
     * @param serverConfig      the custom server configuration
     * @param memory            the amount of heap memory
     * @param processParameters the parameters for the process
     * @param template          the template to start
     * @param customServerName  the name of the server, null if default
     * @param onlineMode        whether online mode is enabled
     * @param priorityStop      whether priority stop is enabled
     * @param url               the url to download the template from
     * @param plugins           a collection of plugins that should be installed
     * @param properties        additional properties
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
                         String.format("Starting game server: %s, %s, %s, %s, %d, %s, %s, %s, %s, %s, %s, %s, %s",
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
     * Start a new cloud server with full parameters
     *
     * @param wrapperInfo          the wrapper to start this server on
     * @param serverName           the name of this server
     * @param memory               the amount of heap memory
     * @param priorityStop         whether priority stop is enabled
     */
    public void startCloudServer(WrapperInfo wrapperInfo, String serverName, int memory, boolean priorityStop) {
        startCloudServer(wrapperInfo, serverName, new BasicServerConfig(), memory, priorityStop);
    }

    /**
     * Start a new cloud server with full parameters
     *
     * @param wrapperInfo          the wrapper to start this server on
     * @param serverName           the name of this server
     * @param serverConfig         the custom server configuration
     * @param memory               the amount of heap memory
     * @param priorityStop         whether priority stop is enabled
     */
    public void startCloudServer(WrapperInfo wrapperInfo, String serverName, ServerConfig serverConfig, int memory, boolean priorityStop) {
        startCloudServer(wrapperInfo,
                         serverName,
                         serverConfig,
                         memory,
                         priorityStop,
                         new String[0],
                         new ArrayList<>(),
                         new Properties(),
                         ServerGroupType.BUKKIT);
    }

    /**
     * Start a new cloud server with full parameters
     *
     * @param wrapperInfo          the wrapper to start this server on
     * @param serverName           the name of this server
     * @param serverConfig         the custom server configuration
     * @param memory               the amount of heap memory
     * @param priorityStop         whether priority stop is enabled
     * @param processPreParameters parameters for the java process
     * @param plugins              a collection of plugins that should be installed
     * @param properties           additional properties
     * @param serverGroupType      the type of server group to start
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
                         String.format("Starting cloud server: %s, %s, %s, %d, %s, %s, %s, %s, %s",
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
     * Start a new cloud server with full parameters
     *
     * @param serverName           the name of this server
     * @param memory               the amount of heap memory
     * @param priorityStop         whether priority stop is enabled
     */
    public void startCloudServer(String serverName, int memory, boolean priorityStop) {
        startCloudServer(serverName, new BasicServerConfig(), memory, priorityStop);
    }

    /**
     * Start a new cloud server with full parameters
     *
     * @param serverName           the name of this server
     * @param serverConfig         the custom server configuration
     * @param memory               the amount of heap memory
     * @param priorityStop         whether priority stop is enabled
     */
    public void startCloudServer(String serverName, ServerConfig serverConfig, int memory, boolean priorityStop) {
        startCloudServer(serverName,
                         serverConfig,
                         memory,
                         priorityStop,
                         new String[0],
                         new ArrayList<>(),
                         new Properties(),
                         ServerGroupType.BUKKIT);
    }

    /*==========================================================================*/

    /**
     * Start a new cloud server with full parameters
     *
     * @param serverName           the name of this server
     * @param serverConfig         the custom server configuration
     * @param memory               the amount of heap memory
     * @param priorityStop         whether priority stop is enabled
     * @param processPreParameters parameters for the java process
     * @param plugins              a collection of plugins that should be installed
     * @param properties           additional properties
     * @param serverGroupType      the type of server group to start
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
                         String.format("Starting cloud server: %s, %s, %d, %s, %s, %s, %s, %s",
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
        return result.getResult().getObject("serverInfos", new TypeToken<Collection<ServerInfo>>() {}.getType());
    }

    /**
     * Returns the ServerInfo from all CloudGameServers
     *
     * @return a collection containing all currently running cloud servers.
     */
    public Collection<ServerInfo> getCloudServers() {
        if (cloudService != null && cloudService.isProxyInstance()) {
            return CollectionWrapper.filterMany(cloudService.getServers().values(), new Acceptable<ServerInfo>() {
                @Override
                public boolean isAccepted(ServerInfo serverInfo) {
                    return serverInfo.getServiceId().getGroup() == null;
                }
            });
        }

        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetCloudServers(), networkConnection);
        return result.getResult().getObject("serverInfos", new TypeToken<Collection<ServerInfo>>() {}.getType());
    }

    /**
     * Queries the master for all running proxies.
     *
     * @return a collection containing all running proxies.
     */
    public Collection<ProxyInfo> getProxys() {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetProxys(), networkConnection);
        return result.getResult().getObject("proxyInfos", new TypeToken<Collection<ProxyInfo>>() {}.getType());
    }

    /**
     * Queries the master for all running proxies in the given group.
     *
     * @param group the name of the proxy group to get the proxies for.
     *
     * @return a collection containing all running proxies of the given proxy group.
     */
    public Collection<ProxyInfo> getProxys(String group) {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetProxys(group), networkConnection);
        return result.getResult().getObject("proxyInfos", new TypeToken<Collection<ProxyInfo>>() {}.getType());
    }

    /**
     * Returns all players currently online on the network.
     * This methods queries the master so it may take a short moment.
     *
     * @return all players currently online.
     */
    public Collection<CloudPlayer> getOnlinePlayers() {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetPlayers(), networkConnection);
        Collection<CloudPlayer> cloudPlayers = result.getResult().getObject("players",
                                                                            new TypeToken<Collection<CloudPlayer>>() {}.getType());

        if (cloudPlayers == null) {
            return new ArrayList<>();
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
        CloudPlayer instance = checkAndGet(uniqueId);
        if (instance != null) {
            return instance;
        }

        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetPlayer(uniqueId), networkConnection);
        CloudPlayer cloudPlayer = result.getResult().getObject("player", CloudPlayer.TYPE);
        if (cloudPlayer == null) {
            return null;
        }
        cloudPlayer.setPlayerExecutor(PlayerExecutorBridge.INSTANCE);
        return cloudPlayer;
    }

    private CloudPlayer checkAndGet(UUID uniqueId) {
        return cloudService != null ? cloudService.getCachedPlayer(uniqueId) : null;
    }

    /**
     * Returns an online player by their UUID.
     * If the player is not cached, the master is queried.
     *
     * @param uniqueId the UUID of the player.
     *
     * @return the online player or null, if the player is not currently online on the network.
     */
    public OfflinePlayer getOfflinePlayer(UUID uniqueId) {
        CloudPlayer cloudPlayer = checkAndGet(uniqueId);
        if (cloudPlayer != null) {
            return cloudPlayer;
        }

        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetOfflinePlayer(uniqueId), networkConnection);
        return result.getResult().getObject("player", new TypeToken<OfflinePlayer>() {}.getType());
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
        CloudPlayer cloudPlayer = checkAndGet(name);
        if (cloudPlayer != null) {
            return cloudPlayer;
        }

        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetOfflinePlayer(name), networkConnection);
        return result.getResult().getObject("player", new TypeToken<OfflinePlayer>() {}.getType());
    }

    private CloudPlayer checkAndGet(String name) {
        return cloudService != null ? cloudService.getCachedPlayer(name) : null;
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
        return result.getResult().getObject("serverGroup", new TypeToken<ServerGroup>() {}.getType());
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
        return result.getResult().getObject("uniqueId", new TypeToken<UUID>() {}.getType());
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
     * @param serverName the server id to query for.
     *
     * @return the server information of the server with the given sever id.
     */
    public ServerInfo getServerInfo(String serverName) {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetServer(serverName), networkConnection);
        return result.getResult().getObject("serverInfo", new TypeToken<ServerInfo>() {}.getType());
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


    @Deprecated
    private Map<UUID, OfflinePlayer> getRegisteredPlayers() {
        Result result = networkConnection.getPacketManager().sendQuery(new PacketAPIOutGetRegisteredPlayers(), networkConnection);

        if (result.getResult() != null) {
            return result.getResult().getObject("players", new TypeToken<Map<UUID, OfflinePlayer>>() {}.getType());
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
