/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore;

import de.dytanic.cloudnet.command.CommandManager;
import de.dytanic.cloudnet.database.DatabaseManager;
import de.dytanic.cloudnet.event.EventManager;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.hash.DyHash;
import de.dytanic.cloudnet.lib.interfaces.Executable;
import de.dytanic.cloudnet.lib.interfaces.Reloadable;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketManager;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.server.*;
import de.dytanic.cloudnet.lib.server.defaults.BasicServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.logging.CloudLogger;
import de.dytanic.cloudnet.modules.ModuleManager;
import de.dytanic.cloudnet.web.client.WebClient;
import de.dytanic.cloudnet.web.server.WebServer;
import de.dytanic.cloudnetcore.api.event.network.CloudInitEvent;
import de.dytanic.cloudnetcore.command.*;
import de.dytanic.cloudnetcore.database.DatabaseBasicHandlers;
import de.dytanic.cloudnetcore.handler.*;
import de.dytanic.cloudnetcore.modules.DefaultModuleManager;
import de.dytanic.cloudnetcore.network.CloudNetServer;
import de.dytanic.cloudnetcore.network.NetworkManager;
import de.dytanic.cloudnetcore.network.components.*;
import de.dytanic.cloudnetcore.network.components.screen.ScreenProvider;
import de.dytanic.cloudnetcore.network.packet.api.*;
import de.dytanic.cloudnetcore.network.packet.api.sync.*;
import de.dytanic.cloudnetcore.network.packet.dbsync.*;
import de.dytanic.cloudnetcore.network.packet.in.*;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutCloudNetwork;
import de.dytanic.cloudnetcore.serverlog.ServerLogManager;
import de.dytanic.cloudnetcore.util.FileCopy;
import de.dytanic.cloudnetcore.web.api.v1.*;
import de.dytanic.cloudnetcore.web.log.WebsiteLog;
import de.dytanic.cloudnetcore.wrapper.local.LocalCloudWrapper;
import joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Because this is an API class, we can and should suppress warnings about
// unused methods and weaker access.
@SuppressWarnings({"unused", "WeakerAccess"})
public final class CloudNet implements Executable, Reloadable {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static volatile boolean RUNNING = false;

    private static CloudNet instance;

    private final CommandManager commandManager = new CommandManager();
    private final ModuleManager moduleManager = new ModuleManager();
    private final DatabaseManager databaseManager = new DatabaseManager();
    private final PacketManager packetManager = new PacketManager();
    private final EventManager eventManager = new EventManager();
    private final ScreenProvider screenProvider = new ScreenProvider();
    private final ServerLogManager serverLogManager = new ServerLogManager();

    private final NetworkManager networkManager = new NetworkManager();
    private final Map<String, Wrapper> wrappers = new ConcurrentHashMap<>();
    private final Map<String, ServerGroup> serverGroups = new ConcurrentHashMap<>();
    private final Map<String, ProxyGroup> proxyGroups = new ConcurrentHashMap<>();
    private final LocalCloudWrapper localCloudWrapper = new LocalCloudWrapper();
    private Collection<CloudNetServer> cloudServers = new CopyOnWriteArrayList<>();
    private WebClient webClient = new WebClient();
    private WebServer webServer;
    private CloudConfig config;
    private CloudLogger logger;
    private OptionSet optionSet;
    private DefaultModuleManager defaultModuleManager;
    private List<String> arguments;
    private DatabaseBasicHandlers dbHandlers;
    private Collection<User> users;
    private long startupTime = System.currentTimeMillis();
    public CloudNet(CloudConfig config, CloudLogger cloudNetLogging, OptionSet optionSet, List<String> args) throws
        Exception {
        if (instance == null) {
            instance = this;
        }

        this.config = config;
        this.logger = cloudNetLogging;
        this.optionSet = optionSet;
        this.arguments = args;
        this.defaultModuleManager = new DefaultModuleManager();

        // We need the reader to stay open
        //noinspection resource
        this.logger.getReader().addCompleter(commandManager);
    }

    public static boolean isRUNNING() {
        return RUNNING;
    }

    public static CloudLogger getLogger() {
        return instance.logger;
    }

    public static CloudNet getInstance() {
        return instance;
    }

    @Override
    public boolean bootstrap() throws Exception {
        if (!optionSet.has("disable-autoupdate")) {
            checkForUpdates();
        }

        dbHandlers = new DatabaseBasicHandlers(databaseManager);
        dbHandlers.getStatisticManager().addStartup();

        this.moduleManager.setDisabledModuleList(config.getDisabledModules());

        if (!optionSet.has("disable-modules")) {
            System.out.println("Loading Modules...");
            moduleManager.loadModules();
        }

        for (WrapperMeta wrapperMeta : config.getWrappers()) {
            System.out.println("Loading Wrapper " + wrapperMeta.getId() + " @ " + wrapperMeta.getHostName());
            this.wrappers.put(wrapperMeta.getId(), new Wrapper(wrapperMeta));
        }

        this.users = config.getUsers();

        this.serverGroups.putAll(config.getServerGroups());
        this.serverGroups.forEach((name, serverGroup) -> {
            logger.info(String.format("Loading server group: %s", serverGroup.getName()));
            setupGroup(serverGroup);
        });

        this.proxyGroups.putAll(config.getProxyGroups());
        this.proxyGroups.forEach((name, proxyGroup) -> {
            logger.info(String.format("Loading proxy group: %s", proxyGroup.getName()));
            setupProxy(proxyGroup);
        });

        webServer = new WebServer(optionSet.has("ssl"), config.getWebServerConfig().getAddress(), config.getWebServerConfig().getPort());

        this.initialCommands();
        this.initWebHandlers();
        this.initPacketHandlers();

        for (ConnectableAddress connectableAddress : config.getAddresses()) {
            new CloudNetServer(optionSet, connectableAddress);
        }

        webServer.bind();

        RUNNING = true;

        {
            if (!optionSet.has("onlyConsole")) {
                CloudStartupHandler cloudStartupHandler = new CloudStartupHandler();
                CloudPriorityStartupHandler cloudPriorityStartupHandler = new CloudPriorityStartupHandler();
                CloudPriorityGroupStartupHandler cloudPriorityGroupStartupHandler = new CloudPriorityGroupStartupHandler();
                CloudPlayerRemoverHandler cloudPlayerRemoverHandler = new CloudPlayerRemoverHandler();

                getExecutor().scheduleWithFixedDelay(cloudStartupHandler, 0, 1, TimeUnit.SECONDS);
                getExecutor().scheduleWithFixedDelay(cloudPriorityGroupStartupHandler, 0, 1, TimeUnit.SECONDS);
                getExecutor().scheduleWithFixedDelay(cloudPriorityStartupHandler, 0, 1, TimeUnit.SECONDS);
                getExecutor().scheduleWithFixedDelay(cloudPriorityStartupHandler, 0, 200, TimeUnit.MILLISECONDS);
            }

            CloudStopCheckHandler cloudStopCheck = new CloudStopCheckHandler();

            getExecutor().scheduleWithFixedDelay(cloudStopCheck, 0, 2, TimeUnit.SECONDS);
            getExecutor().scheduleWithFixedDelay(serverLogManager, 0, 40, TimeUnit.SECONDS);
            getExecutor().scheduleWithFixedDelay(() -> {
                for (CloudPlayer cloudPlayer : networkManager.getWaitingPlayers().values()) {
                    if ((cloudPlayer.getLoginTimeStamp().getTime() + 10000L) < System.currentTimeMillis()) {
                        networkManager.getWaitingPlayers().remove(cloudPlayer.getUniqueId());
                    }
                }
            }, 0, 2, TimeUnit.SECONDS);
        }

        if (!optionSet.has("disable-modules")) {
            System.out.println("Enabling Modules...");
            moduleManager.enableModules();
        }

        eventManager.callEvent(new CloudInitEvent());
        this.localCloudWrapper.accept(optionSet);

        return true;
    }

    public static ScheduledExecutorService getExecutor() {
        return NetworkUtils.getExecutor();
    }

    @Override
    public boolean shutdown() {
        if (!RUNNING) {
            return false;
        }

        getExecutor().shutdownNow();

        for (Wrapper wrapper : wrappers.values()) {
            System.out.println("Disconnecting wrapper " + wrapper.getServerId());
            wrapper.disconnct();
        }

        if (!optionSet.has("disable-modules")) {
            System.out.println("Disabling Modules...");
            this.moduleManager.disableModules();
        }
        dbHandlers.getStatisticManager().cloudOnlineTime(startupTime);
        this.databaseManager.save().clear();

        for (CloudNetServer cloudNetServer : this.cloudServers) {
            cloudNetServer.close();
        }

        try {
            this.localCloudWrapper.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\n    _  _     _______   _                       _          \n" + "  _| || |_  |__   __| | |                     | |         \n" + " |_  __  _|    | |    | |__     __ _   _ __   | | __  ___ \n" + "  _| || |_     | |    | '_ \\   / _` | | '_ \\  | |/ / / __|\n" + " |_  __  _|    | |    | | | | | (_| | | | | | |   <  \\__ \\\n" + "   |_||_|      |_|    |_| |_|  \\__,_| |_| |_| |_|\\_\\ |___/\n" + "                                                          \n" + "                                                          ");

        RUNNING = false;
        this.logger.shutdownAll();
        try {
            getExecutor().awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
        return true;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Collection<CloudNetServer> getCloudServers() {
        return cloudServers;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public OptionSet getOptionSet() {
        return optionSet;
    }

    public CloudConfig getConfig() {
        return config;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public ScreenProvider getScreenProvider() {
        return screenProvider;
    }

    public ServerLogManager getServerLogManager() {
        return serverLogManager;
    }

    public Map<String, ServerGroup> getServerGroups() {
        return serverGroups;
    }

    public Map<String, ProxyGroup> getProxyGroups() {
        return proxyGroups;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public WebServer getWebServer() {
        return webServer;
    }

    public DatabaseBasicHandlers getDbHandlers() {
        return dbHandlers;
    }

    public DefaultModuleManager getDefaultModuleManager() {
        return defaultModuleManager;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public LocalCloudWrapper getLocalCloudWrapper() {
        return localCloudWrapper;
    }

    public void checkForUpdates() {
        if (!config.isAutoUpdate()) {
            return;
        }

        String version = webClient.getNewstVersion();

        if (version != null) {
            if (!version.equals(CloudNet.class.getPackage().getImplementationVersion())) {
                System.out.println("Preparing update...");
                localCloudWrapper.installUpdate(webClient);
                webClient.update(version);
                shutdown();

            } else {
                System.out.println("No updates were found!");
            }
        } else {
            System.out.println("Failed to check for updates");
        }
    }

    public void setupGroup(ServerGroup serverGroup) {
        Path path;
        for (Template template : serverGroup.getTemplates()) {
            path = Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName());
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                    Files.createDirectories(Paths.get("local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/plugins"));
                    FileCopy.insertData("files/server.properties",
                                        "local/templates/" + serverGroup.getName() + NetworkUtils.SLASH_STRING + template.getName() + "/server.properties");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        path = Paths.get("local/templates/" + serverGroup.getName() + "/globaltemplate");
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                Files.createDirectories(Paths.get("local/templates/" + serverGroup.getName() + "/globaltemplate/plugins"));
                FileCopy.insertData("files/server.properties",
                                    "local/templates/" + serverGroup.getName() + "/globaltemplate/server.properties");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void setupProxy(ProxyGroup proxyGroup) {
        Path path = Paths.get("local/templates/" + proxyGroup.getName());
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                Files.createDirectories(Paths.get("local/templates/" + proxyGroup.getName() + "/plugins"));
                FileCopy.insertData("files/server.properties", "local/templates/" + proxyGroup.getName() + "/server.properties");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initialCommands() {
        this.commandManager.registerCommand(new CommandReload())
                           .registerCommand(new CommandShutdown())
                           .registerCommand(new CommandClear())
                           .registerCommand(new CommandClearCache())
                           .registerCommand(new CommandList())
                           .registerCommand(new CommandScreen())
                           .registerCommand(new CommandHelp())
                           .registerCommand(new CommandModules())
                           .registerCommand(new CommandStop())
                           .registerCommand(new CommandCmd())
                           .registerCommand(new CommandStatistic())
                           .registerCommand(new CommandDelete())
                           .registerCommand(new CommandInstallPlugin())
                           .registerCommand(new CommandCopy())
                           .registerCommand(new CommandLog())
                           .registerCommand(new CommandCreate())
                           .registerCommand(new CommandVersion())
                           .registerCommand(new CommandInfo())
                           .registerCommand(new CommandDebug())
                           .registerCommand(new CommandUser())
                           .registerCommand(new CommandLocalWrapper());
    }

    private void initWebHandlers() {
        webServer.getWebServerProvider().registerHandler(new WebsiteUtils());
        webServer.getWebServerProvider().registerHandler(new WebsiteDocumentation());
        webServer.getWebServerProvider().registerHandler(new WebsiteAuthorization());
        webServer.getWebServerProvider().registerHandler(new WebsiteDeployment());
        webServer.getWebServerProvider().registerHandler(new WebsiteDownloadService());

        webServer.getWebServerProvider().registerHandler(new WebsiteLog());
    }

    private void initPacketHandlers() {
        packetManager.clearHandlers();
        packetManager.registerHandler(PacketRC.INTERNAL - 1, PacketInAuthHandler.class);

        packetManager.registerHandler(PacketRC.CN_CORE + 2, PacketInUpdateServerGroup.class);
        packetManager.registerHandler(PacketRC.CN_CORE + 3, PacketInUpdateProxyGroup.class);
        packetManager.registerHandler(PacketRC.CN_CORE + 4, PacketInExecuteCommand.class);
        packetManager.registerHandler(PacketRC.CN_CORE + 5, PacketInServerDispatchCommand.class);

        packetManager.registerHandler(PacketRC.CN_WRAPPER + 1, PacketInAddProxy.class);
        packetManager.registerHandler(PacketRC.CN_WRAPPER + 2, PacketInAddServer.class);
        packetManager.registerHandler(PacketRC.CN_WRAPPER + 3, PacketInDispatchConsoleMessage.class);
        packetManager.registerHandler(PacketRC.CN_WRAPPER + 4, PacketInRemoveProxy.class);
        packetManager.registerHandler(PacketRC.CN_WRAPPER + 5, PacketInRemoveServer.class);
        packetManager.registerHandler(PacketRC.CN_WRAPPER + 6, PacketInSendScreenLine.class);
        packetManager.registerHandler(PacketRC.CN_WRAPPER + 7, PacketInSetReadyWrapper.class);
        packetManager.registerHandler(PacketRC.CN_WRAPPER + 8, PacketInUpdateWrapperInfo.class);
        packetManager.registerHandler(PacketRC.CN_WRAPPER + 9, PacketInEnableScreen.class);
        packetManager.registerHandler(PacketRC.CN_WRAPPER + 10, PacketInDisableScreen.class);
        packetManager.registerHandler(PacketRC.CN_WRAPPER + 11, PacketInUpdateCPUUsage.class);
        packetManager.registerHandler(PacketRC.CN_WRAPPER + 12, PacketInWrapperScreen.class);

        packetManager.registerHandler(PacketRC.CN_WRAPPER + 13, PacketInAddCloudServer.class);
        packetManager.registerHandler(PacketRC.CN_WRAPPER + 14, PacketInRemoveCloudServer.class);

        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 1, PacketInUpdateServerInfo.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 2, PacketInUpdateProxyInfo.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 3, PacketInCustomChannelMessage.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 4, PacketInStartServer.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 5, PacketInStopServer.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 6, PacketInStartProxy.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 7, PacketInStopProxy.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 8, PacketInCustomSubChannelMessage.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 9, PacketInStartCloudServer.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 10, PacketInCopyDirectory.class);

        packetManager.registerHandler(PacketRC.PLAYER_HANDLE + 1, PacketInPlayerLoginRequest.class);
        packetManager.registerHandler(PacketRC.PLAYER_HANDLE + 2, PacketInUpdatePlayer.class);
        packetManager.registerHandler(PacketRC.PLAYER_HANDLE + 3, PacketInLogoutPlayer.class);
        packetManager.registerHandler(PacketRC.PLAYER_HANDLE + 4, PacketInCommandExecute.class);
        packetManager.registerHandler(PacketRC.PLAYER_HANDLE + 5, PacketInUpdateOnlinePlayer.class);
        packetManager.registerHandler(PacketRC.PLAYER_HANDLE + 6, PacketInLoginSuccess.class);

        packetManager.registerHandler(PacketRC.API + 1, PacketAPIInGetPlayer.class);
        packetManager.registerHandler(PacketRC.API + 2, PacketAPIInGetPlayers.class);
        packetManager.registerHandler(PacketRC.API + 3, PacketAPIInGetServers.class);
        packetManager.registerHandler(PacketRC.API + 4, PacketAPIInGetProxys.class);
        packetManager.registerHandler(PacketRC.API + 5, PacketAPIInGetOfflinePlayer.class);
        packetManager.registerHandler(PacketRC.API + 6, PacketAPIInGetServerGroup.class);
        packetManager.registerHandler(PacketRC.API + 7, PacketAPIInNameUUID.class);
        packetManager.registerHandler(PacketRC.API + 8, PacketAPIInGetServer.class);
        packetManager.registerHandler(PacketRC.API + 9, PacketAPIInGetCloudServers.class);
        packetManager.registerHandler(PacketRC.API + 10, PacketAPIInGetStatistic.class);
        packetManager.registerHandler(PacketRC.API + 11, PacketAPIInGetRegisteredPlayers.class);

        packetManager.registerHandler(PacketRC.DB + 1, PacketDBInGetDocument.class);
        packetManager.registerHandler(PacketRC.DB + 2, PacketDBInInsertDocument.class);
        packetManager.registerHandler(PacketRC.DB + 3, PacketDBInDeleteDocument.class);
        packetManager.registerHandler(PacketRC.DB + 4, PacketDBInExistsDocument.class);
        packetManager.registerHandler(PacketRC.DB + 5, PacketDBInGetSize.class);
        packetManager.registerHandler(PacketRC.DB + 6, PacketDBInSelectDatabase.class);

        packetManager.registerHandler(PacketRC.CN_INTERNAL_CHANNELS + 1, PacketInCreateServerLog.class);
    }

    public long getStartupTime() {
        return startupTime;
    }

    @Override
    public void reload() throws Exception {

        if (!optionSet.has("disable-modules")) {
            System.out.println("Disabling modules...");
            this.moduleManager.disableModules();
        }

        this.eventManager.clearAll();
        this.commandManager.clearCommands();
        this.webServer.getWebServerProvider().clear();
        this.networkManager.getModuleProperties().clear();

        databaseManager.save().clear();

        this.users.clear();
        this.serverGroups.clear();
        this.proxyGroups.clear();

        this.config.load();

        this.users = config.getUsers();

        this.serverGroups.putAll(config.getServerGroups());
        this.serverGroups.forEach((name, serverGroup) -> {
            logger.info(String.format("Loading server group: %s%n", serverGroup.getName()));
            setupGroup(serverGroup);
        });

        this.proxyGroups.putAll(config.getProxyGroups());
        this.proxyGroups.forEach((name, proxyGroup) -> {
            logger.info(String.format("Loading proxy group: %s%n", proxyGroup.getName()));
            setupProxy(proxyGroup);
        });

        this.initialCommands();
        this.initWebHandlers();
        this.initPacketHandlers();

        if (!optionSet.has("disable-modules")) {
            this.moduleManager.loadModules().enableModules();
        }

        System.out.println("Updating wrappers...");
        wrappers.values().forEach(Wrapper::updateWrapper);

        networkManager.reload();
        networkManager.updateAll();
    }

    public boolean authorization(String name, String token) {
        Optional<User> user = users.stream().filter(value -> value.getName().equalsIgnoreCase(name)).findFirst();
        return user.map(value -> value.getApiToken().equals(token)).orElse(false);
    }

    public boolean authorizationPassword(String name, String password) {
        Optional<User> user = users.stream().filter(value -> value.getName().equalsIgnoreCase(name)).findFirst();
        return user.map(value -> value.getHashedPassword().equals(DyHash.hashString(password))).orElse(false);
    }

    public ServerGroup getServerGroup(String group) {
        return serverGroups.get(group);
    }

    public ProxyGroup getProxyGroup(String group) {
        return proxyGroups.get(group);
    }

    public User getUser(String name) {
        Optional<User> user = users.stream().filter(value -> value.getName().equalsIgnoreCase(name)).findFirst();
        return user.orElse(null);
    }

    public int getGlobalUsedMemoryAndWaitings() {
        int usedMemory = 0;
        for (final Wrapper wrapper : wrappers.values()) {
            usedMemory += wrapper.getUsedMemory();
            for (final WaitingService service : wrapper.getWaitingServices().values()) {
                usedMemory += service.getUsedMemory();
            }
        }
        return usedMemory;
    }

    public Map<String, Wrapper> getWrappers() {
        return wrappers;
    }

    public int getOnlineCount(String group) {
        int onlineCount = 0;
        for (Wrapper wrapper : wrappers.values()) {
            for (MinecraftServer minecraftServer : wrapper.getServers().values()) {
                if (minecraftServer.getServiceId().getGroup().equalsIgnoreCase(group)) {
                    onlineCount = onlineCount + minecraftServer.getServerInfo().getOnlineCount();
                }
            }
        }
        return onlineCount;
    }

    public ServiceId newServiceId(ServerGroup serverGroup, Wrapper wrapper) {
        int id = 1;
        Collection<ServiceId> serviceIds = getServerServiceIdsAndWaitings(serverGroup.getName());
        List<Integer> serverIds = serviceIds.stream()
                                            .map(ServiceId::getId)
                                            .collect(Collectors.toList());
        while (serverIds.contains(id)) {
            id++;
        }

        return new ServiceId(serverGroup.getName(),
                             id,
                             UUID.randomUUID(),
                             wrapper.getNetworkInfo().getId(),
                             serverGroup.getName() + config.getFormatSplitter() + id);
    }

    public ServiceId newServiceId(ServerGroup serverGroup, Wrapper wrapper, int id) {
        return new ServiceId(serverGroup.getName(),
                             id,
                             UUID.randomUUID(),
                             wrapper.getNetworkInfo().getId(),
                             serverGroup.getName() + config.getFormatSplitter() + id);
    }

    public ServiceId newServiceId(ServerGroup serverGroup, Wrapper wrapper, int id, UUID uniqueId) {
        return new ServiceId(serverGroup.getName(),
                             id,
                             uniqueId,
                             wrapper.getNetworkInfo().getId(),
                             serverGroup.getName() + config.getFormatSplitter() + id);
    }

    public ServiceId newServiceId(ServerGroup serverGroup, Wrapper wrapper, UUID uniqueId) {
        int id = 0;
        Collection<ServiceId> serviceIds = getServerServiceIdsAndWaitings(serverGroup.getName());
        Collection<Integer> ids = serviceIds.stream()
                                            .map(ServiceId::getId)
                                            .collect(Collectors.toList());
        while (ids.contains(id)) {
            id++;
        }
        return new ServiceId(serverGroup.getName(),
                             id,
                             uniqueId,
                             wrapper.getNetworkInfo().getId(),
                             serverGroup.getName() + config.getFormatSplitter() + id);
    }

    public Collection<ServiceId> getServerServiceIdsAndWaitings(String group) {
        List<ServiceId> serviceIds = getServers(group).stream()
                                                      .map(MinecraftServer::getServiceId)
                                                      .collect(Collectors.toList());

        wrappers.values().stream()
                .flatMap(wrapper -> wrapper.getWaitingServices().values().stream())
                .filter(waitingService -> waitingService.getServiceId().getGroup().equals(group))
                .map(WaitingService::getServiceId)
                .forEach(serviceIds::add);

        return serviceIds;
    }

    public Collection<MinecraftServer> getServers(String group) {
        Collection<MinecraftServer> minecraftServers = new LinkedList<>();

        for (MinecraftServer minecraftServer : getServers().values()) {
            if (minecraftServer.getServiceId().getGroup().equalsIgnoreCase(group)) {
                minecraftServers.add(minecraftServer);
            }
        }
        return minecraftServers;
    }

    public Map<String, MinecraftServer> getServers() {
        Map<String, MinecraftServer> minecraftServerMap = new HashMap<>();

        for (Wrapper wrapper : wrappers.values()) {
            for (MinecraftServer minecraftServer : wrapper.getServers().values()) {
                minecraftServerMap.put(minecraftServer.getServerId(), minecraftServer);
            }
        }

        return minecraftServerMap;
    }

    public ServiceId newServiceId(ServerGroup serverGroup, Wrapper wrapper, int id, String serverId) {
        return new ServiceId(serverGroup.getName(), id, UUID.randomUUID(), wrapper.getNetworkInfo().getId(), serverId);
    }

    public ServiceId newServiceId(ServerGroup serverGroup, Wrapper wrapper, int id, UUID uniqueId, String serverId) {
        return new ServiceId(serverGroup.getName(), id, uniqueId, wrapper.getNetworkInfo().getId(), serverId);
    }

    public ServiceId newServiceId(ServerGroup serverGroup, Wrapper wrapper, UUID uniqueId, String serverId) {
        Collection<ServiceId> serviceIds = getServerServiceIdsAndWaitings(serverGroup.getName());
        List<Integer> ids = serviceIds.stream()
                                      .map(ServiceId::getId)
                                      .collect(Collectors.toList());
        int id = 0;
        while (ids.contains(id)) {
            id++;
        }
        if (serverId == null) {
            serverId = serverGroup.getName() + this.config.getFormatSplitter() + id;
        }
        return new ServiceId(serverGroup.getName(), id, uniqueId, wrapper.getNetworkInfo().getId(), serverId);
    }

    public long globalMaxMemory() {
        return wrappers.values().stream()
                       .mapToLong(Wrapper::getMaxMemory)
                       .sum();
    }

    public CloudServer getCloudGameServer(String serverId) {
        return getCloudGameServers().get(serverId);
    }

    public Map<String, CloudServer> getCloudGameServers() {
        Map<String, CloudServer> cloudServerMap = new HashMap<>();

        for (Wrapper wrapper : wrappers.values()) {
            cloudServerMap.putAll(wrapper.getCloudServers());
        }

        return cloudServerMap;
    }

    public Collection<CloudServer> getCloudGameServers(String group) {
        Collection<CloudServer> minecraftServers = new LinkedList<>();

        for (CloudServer minecraftServer : getCloudGameServers().values()) {
            if (minecraftServer.getServiceId().getGroup().equalsIgnoreCase(group)) {
                minecraftServers.add(minecraftServer);
            }
        }
        return minecraftServers;
    }

    public Collection<String> getServersAndWaitings(String group) {
        List<String> serverIds = getServers(group).stream()
                                                  .map(MinecraftServer::getServerId)
                                                  .collect(Collectors.toList());

        wrappers.values().stream()
                .flatMap(wrapper -> wrapper.getWaitingServices().entrySet().stream())
                .filter(entry -> entry.getValue().getServiceId().getGroup().equals(group))
                .map(Map.Entry::getKey)
                .forEach(serverIds::add);

        return serverIds;
    }

    public Collection<String> getServersAndWaitings() {
        List<String> serverIds = getServers().values().stream()
                                             .map(MinecraftServer::getServerId)
                                             .collect(Collectors.toList());

        wrappers.values().stream()
                .flatMap(wrapper -> wrapper.getWaitingServices().entrySet().stream())
                .map(Map.Entry::getKey)
                .forEach(serverIds::add);

        return serverIds;
    }

    public Collection<String> getProxysAndWaitings(String group) {
        List<String> proxyIds = getProxys(group).stream()
                                                .map(ProxyServer::getServerId)
                                                .collect(Collectors.toList());

        wrappers.values().stream()
                .flatMap(wrapper -> wrapper.getWaitingServices().entrySet().stream())
                .filter(entry -> entry.getValue().getServiceId().getGroup().equals(group))
                .map(Map.Entry::getKey)
                .forEach(proxyIds::add);

        return proxyIds;
    }

    public Collection<ProxyServer> getProxys(String group) {
        Collection<ProxyServer> minecraftServers = new LinkedList<>();

        for (ProxyServer minecraftServer : getProxys().values()) {
            if (minecraftServer.getServiceId().getGroup().equalsIgnoreCase(group)) {
                minecraftServers.add(minecraftServer);
            }
        }
        return minecraftServers;
    }

    public Map<String, ProxyServer> getProxys() {
        Map<String, ProxyServer> minecraftServerMap = new HashMap<>();

        for (Wrapper wrapper : wrappers.values()) {
            for (ProxyServer minecraftServer : wrapper.getProxys().values()) {
                minecraftServerMap.put(minecraftServer.getServerId(), minecraftServer);
            }
        }

        return minecraftServerMap;
    }

    public void startProxy(Wrapper wrapper,
                           ProxyGroup proxyGroup,
                           int memory,
                           String[] parameters,
                           String url,
                           Collection<ServerInstallablePlugin> plugins,
                           Document document) {
        if (wrapper == null) {
            return;
        }
        List<Integer> ports = wrapper.getBoundPorts();
        int startPort = proxyGroup.getStartPort();
        while (ports.contains(startPort)) {
            startPort++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper),
                                                                 memory,
                                                                 startPort,
                                                                 parameters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxy(proxyProcessMeta);
    }

    public ServiceId newServiceId(ProxyGroup proxyGroup, Wrapper wrapper, int id, UUID uuid) {
        return new ServiceId(proxyGroup.getName(),
                             id,
                             uuid,
                             wrapper.getNetworkInfo().getId(),
                             proxyGroup.getName() + config.getFormatSplitter() + id);
    }

    public void startProxy(Wrapper wrapper, ProxyGroup proxyGroup) {
        List<Integer> ports = wrapper.getBoundPorts();
        int startPort = proxyGroup.getStartPort();
        while (ports.contains(startPort)) {
            startPort++;
        }

        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper),
                                                                 proxyGroup.getMemory(),
                                                                 startPort,
                                                                 EMPTY_STRING_ARRAY,
                                                                 null,
                                                                 Collections.emptyList(),
                                                                 new Document());
        wrapper.startProxy(proxyProcessMeta);
    }

    public Collection<String> getServersByName() {
        Collection<String> x = new LinkedList<>();
        for (Wrapper wrapper : wrappers.values()) {
            for (MinecraftServer minecraftServer : wrapper.getServers().values()) {
                x.add(minecraftServer.getServerId());
            }
        }

        return x;
    }

    public Collection<String> getProxysByName() {
        Collection<String> x = new LinkedList<>();
        for (Wrapper wrapper : wrappers.values()) {
            for (ProxyServer minecraftServer : wrapper.getProxys().values()) {
                x.add(minecraftServer.getServerId());
            }
        }

        return x;
    }

    public void startProxy(Wrapper wrapper,
                           ProxyGroup proxyGroup,
                           int memory,
                           String[] parameters,
                           String url,
                           Collection<ServerInstallablePlugin> plugins,
                           Document document,
                           int id,
                           UUID uniqueId) {
        if (wrapper == null) {
            return;
        }

        List<Integer> ports = wrapper.getBoundPorts();
        int startPort = proxyGroup.getStartPort();
        while (ports.contains(startPort)) {
            startPort++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper, id, uniqueId),
                                                                 memory,
                                                                 startPort,
                                                                 parameters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxy(proxyProcessMeta);
    }

    public ServiceId newServiceId(ProxyGroup proxyGroup, Wrapper wrapper) {
        Collection<ServiceId> serviceIds = getProxysServiceIdsAndWaitings(proxyGroup.getName());
        List<Integer> collection = serviceIds.stream()
                                             .map(ServiceId::getId)
                                             .collect(Collectors.toList());
        int id = 1;
        while (collection.contains(id)) {
            id++;
        }

        return new ServiceId(proxyGroup.getName(),
                             id,
                             UUID.randomUUID(),
                             wrapper.getNetworkInfo().getId(),
                             proxyGroup.getName() + config.getFormatSplitter() + id);
    }

    public void updateNetwork() {
        CloudNetwork cloudNetwork = networkManager.newCloudNetwork();
        networkManager.sendAll(new PacketOutCloudNetwork(cloudNetwork));
    }

    public void stopServer(MinecraftServer minecraftServer) {
        minecraftServer.getWrapper().stopServer(minecraftServer);
    }

    public void stopProxy(ProxyServer proxyServer) {
        proxyServer.getWrapper().stopProxy(proxyServer);
    }

    public void stopServer(String server) {
        MinecraftServer minecraftServer = getServer(server);
        if (minecraftServer != null) {
            minecraftServer.getWrapper().stopServer(minecraftServer);
        }
    }

    public MinecraftServer getServer(String serverId) {
        for (Wrapper wrapper : wrappers.values()) {
            for (MinecraftServer minecraftServer : wrapper.getServers().values()) {
                if (minecraftServer.getServerId().equals(serverId)) {
                    return minecraftServer;
                }
            }
        }

        return null;
    }

    public void stopProxy(String proxy) {
        ProxyServer proxyServer = getProxy(proxy);
        if (proxyServer != null) {
            proxyServer.getWrapper().stopProxy(proxyServer);
        }
    }

    public ProxyServer getProxy(String serverId) {
        for (Wrapper wrapper : wrappers.values()) {
            for (ProxyServer minecraftServer : wrapper.getProxys().values()) {
                if (minecraftServer.getServerId().equals(serverId)) {
                    return minecraftServer;
                }
            }
        }

        return null;
    }

    public void startProxy(ProxyProcessMeta proxyProcessMeta, Wrapper wrapper) {
        wrapper.startProxy(proxyProcessMeta);
    }

    public void startProxy(ProxyGroup proxyGroup) {
        Wrapper wrapper = fetchPerformanceWrapper(proxyGroup.getMemory(), toWrapperInstances(proxyGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        this.startProxy(wrapper, proxyGroup);
    }

    public Wrapper fetchPerformanceWrapper(int memory, Collection<Wrapper> wrappers) {
        if (wrappers.size() == 0) {
            return null;
        }

        Wrapper user = null;
        int use = 0;

        for (Wrapper wrapper : wrappers) {

            int us = wrapper.getUsedMemoryAndWaitings() + memory;

            if (user == null && wrapper.getChannel() != null && wrapper.getWrapperInfo() != null && wrapper.getWrapperInfo()
                                                                                                           .getMemory() > us) {
                user = wrapper;
                use = wrapper.getUsedMemory() + memory;
            }

            if (wrapper.getWrapperInfo() != null && wrapper.getChannel() != null && wrapper.getWrapperInfo().getMemory() > us && us < use) {
                user = wrapper;
                use = us;
            }
        }

        return user;
    }

    public Collection<Wrapper> toWrapperInstances(Collection<String> wrappers) {
        Collection<Wrapper> wrappers1 = new ConcurrentLinkedQueue<>();
        for (String wrapper : wrappers) {
            if (this.wrappers.containsKey(wrapper)) {
                wrappers1.add(this.wrappers.get(wrapper));
            }
        }
        return wrappers1;
    }

    public Collection<ServiceId> getProxysServiceIdsAndWaitings(String group) {
        List<ServiceId> serviceIds = getProxys(group).stream()
                                                     .map(ProxyServer::getServiceId)
                                                     .collect(Collectors.toList());

        wrappers.values().stream()
                .flatMap(wrapper -> wrapper.getWaitingServices().values().stream())
                .filter(entry -> entry.getServiceId().getGroup().equals(group))
                .map(WaitingService::getServiceId)
                .forEach(serviceIds::add);

        return serviceIds;
    }

    public void startProxy(ProxyGroup proxyGroup, int memory) {
        startProxy(proxyGroup, memory, null, Collections.emptyList(), new Document());
    }

    public void startProxy(ProxyGroup proxyGroup, int memory, String url, Collection<ServerInstallablePlugin> plugins, Document document) {
        startProxy(proxyGroup, memory, EMPTY_STRING_ARRAY, url, plugins, document);
    }

    public void startProxy(ProxyGroup proxyGroup,
                           int memory,
                           String[] parameters,
                           String url,
                           Collection<ServerInstallablePlugin> plugins,
                           Document document) {
        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(proxyGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        List<Integer> ports = wrapper.getBoundPorts();
        int startport = proxyGroup.getStartPort();
        while (ports.contains(startport)) {
            startport++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper),
                                                                 memory,
                                                                 startport,
                                                                 parameters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxy(proxyProcessMeta);
    }

    public void startProxy(ProxyGroup proxyGroup, String urlTemplate) {
        startProxy(proxyGroup, proxyGroup.getMemory(), urlTemplate, Collections.emptyList(), new Document());
    }

    public void startProxy(ProxyGroup proxyGroup, String urlTemplate, Document document) {
        startProxy(proxyGroup, proxyGroup.getMemory(), urlTemplate, Collections.emptyList(), document);
    }

    public void startProxy(ProxyGroup proxyGroup, int memory, UUID uniqueId) {
        startProxy(proxyGroup, memory, EMPTY_STRING_ARRAY, null, Collections.emptyList(), new Document(), uniqueId);
    }

    public void startProxy(ProxyGroup proxyGroup,
                           int memory,
                           String[] parameters,
                           String url,
                           Collection<ServerInstallablePlugin> plugins,
                           Document document,
                           UUID uniqueId) {
        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(proxyGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        List<Integer> ports = wrapper.getBoundPorts();
        int startPort = proxyGroup.getStartPort();
        while (ports.contains(startPort)) {
            startPort++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper, uniqueId),
                                                                 memory,
                                                                 startPort,
                                                                 parameters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxy(proxyProcessMeta);
    }

    public ServiceId newServiceId(ProxyGroup proxyGroup, Wrapper wrapper, UUID uuid) {
        int id = 1;
        Collection<ServiceId> serviceIds = getProxysServiceIdsAndWaitings(proxyGroup.getName());
        List<Integer> serverIds = serviceIds.stream()
                                            .map(ServiceId::getId)
                                            .collect(Collectors.toList());
        while (serverIds.contains(id)) {
            id++;
        }

        return new ServiceId(proxyGroup.getName(),
                             id,
                             uuid,
                             wrapper.getNetworkInfo().getId(),
                             proxyGroup.getName() + config.getFormatSplitter() + id);
    }

    public void startProxy(ProxyGroup proxyGroup, Collection<ServerInstallablePlugin> plugins) {
        startProxy(proxyGroup, proxyGroup.getMemory(), null, plugins, new Document());
    }

    public void startProxy(ProxyGroup proxyGroup, int memory, int id, UUID uniqueId) {
        startProxy(proxyGroup, memory, EMPTY_STRING_ARRAY, null, Collections.emptyList(), new Document(), id, uniqueId);
    }

    public void startProxy(ProxyGroup proxyGroup,
                           int memory,
                           String[] parameters,
                           String url,
                           Collection<ServerInstallablePlugin> plugins,
                           Document document,
                           int id,
                           UUID uniqueId) {
        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(proxyGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        List<Integer> ports = wrapper.getBoundPorts();
        int startPort = proxyGroup.getStartPort();
        while (ports.contains(startPort)) {
            startPort++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper, id, uniqueId),
                                                                 memory,
                                                                 startPort,
                                                                 parameters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxy(proxyProcessMeta);
    }

    public void startProxy(ProxyGroup proxyGroup, int memory, String urlTemplate, int id, UUID uniqueId) {
        startProxy(proxyGroup, memory, EMPTY_STRING_ARRAY, urlTemplate, Collections.emptyList(), new Document(), id, uniqueId);
    }

    public void startProxy(ProxyGroup proxyGroup, String url, Collection<ServerInstallablePlugin> collection, int id, UUID uniqueId) {
        startProxy(proxyGroup, proxyGroup.getMemory(), EMPTY_STRING_ARRAY, url, collection, new Document(), id, uniqueId);
    }

    public void startGameServer(ServerGroup serverGroup, ServerConfig serverConfig, Properties serverProperties) {
        startGameServer(serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        false,
                        null,
                        EMPTY_STRING_ARRAY,
                        false,
                        Collections.emptyList(),
                        null,
                        serverProperties);
    }

    public void startCloudServer(String serverName, int memory, boolean priorityStop) {
        startCloudServer(serverName, new BasicServerConfig(), memory, priorityStop);
    }

    public void startCloudServer(String serverName, ServerConfig serverConfig, int memory, boolean priorityStop) {
        startCloudServer(serverName,
                         serverConfig,
                         memory,
                         priorityStop,
                         EMPTY_STRING_ARRAY,
                         new ArrayList<>(),
                         new Properties(),
                         ServerGroupType.BUKKIT);
    }

    public void startCloudServer(String serverName,
                                 ServerConfig serverConfig,
                                 int memory,
                                 boolean priorityStop,
                                 String[] processPreParameters,
                                 Collection<ServerInstallablePlugin> plugins,
                                 Properties properties,
                                 ServerGroupType serverGroupType) {
        Collection<Wrapper> wrappers = toWrapperInstances(config.getCloudServerWrapperList());
        if (wrappers.size() == 0) {
            return;
        }
        Wrapper wrapper = fetchPerformanceWrapper(memory, wrappers);
        if (wrapper == null) {
            return;
        }
        startCloudServer(wrapper,
                         serverName,
                         serverConfig,
                         memory,
                         priorityStop,
                         processPreParameters,
                         plugins,
                         properties,
                         serverGroupType);
    }

    public void startCloudServer(Wrapper wrapper,
                                 String serverName,
                                 ServerConfig serverConfig,
                                 int memory,
                                 boolean priorityStop,
                                 String[] processPreParameters,
                                 Collection<ServerInstallablePlugin> plugins,
                                 Properties properties,
                                 ServerGroupType serverGroupType) {
        List<Integer> ports = wrapper.getServers().values().stream()
                                     .map(MinecraftServer::getProcessMeta)
                                     .map(ServerProcessMeta::getPort)
                                     .collect(Collectors.toList());
        int startPort = getStartPort(wrapper);
        startCloudServer(wrapper,
                         serverName,
                         serverConfig,
                         memory,
                         priorityStop,
                         processPreParameters,
                         plugins,
                         properties,
                         serverGroupType,
                         startPort);
    }

    private static int getStartPort(final Wrapper wrapper) {
        List<Integer> ports = wrapper.getBoundPorts();
        int startport = wrapper.getWrapperInfo().getStartPort();
        do {
            startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        } while (ports.contains(startport));
        return startport;
    }

    public void startCloudServer(Wrapper wrapper,
                                 String serverName,
                                 ServerConfig serverConfig,
                                 int memory,
                                 boolean priorityStop,
                                 String[] processPreParameters,
                                 Collection<ServerInstallablePlugin> plugins,
                                 Properties properties,
                                 ServerGroupType serverGroupType,
                                 int port) {
        startCloudServer(wrapper,
                         serverName,
                         serverConfig,
                         memory,
                         priorityStop,
                         processPreParameters,
                         plugins,
                         properties,
                         serverGroupType,
                         port,
                         false);
    }

    public void startCloudServer(Wrapper wrapper,
                                 String serverName,
                                 ServerConfig serverConfig,
                                 int memory,
                                 boolean priorityStop,
                                 String[] processPreParameters,
                                 Collection<ServerInstallablePlugin> plugins,
                                 Properties properties,
                                 ServerGroupType serverGroupType,
                                 int port,
                                 boolean async) {
        CloudServerMeta cloudServerMeta = new CloudServerMeta(new ServiceId("_null_",
                                                                            -1,
                                                                            UUID.randomUUID(),
                                                                            wrapper.getServerId(),
                                                                            serverName),
                                                              memory,
                                                              priorityStop,
                                                              processPreParameters,
                                                              plugins,
                                                              serverConfig,
                                                              port,
                                                              //port
                                                              serverName,
                                                              properties,
                                                              serverGroupType);
        if (async) {
            wrapper.startCloudServer(cloudServerMeta);
        } else {
            wrapper.startCloudServerAsync(cloudServerMeta);
        }
    }

    public void startGameServer(ServerGroup serverGroup,
                                ServerConfig config,
                                int memory,
                                boolean priorityStop,
                                String url,
                                String[] processParameters,
                                boolean onlineMode,
                                Collection<ServerInstallablePlugin> plugins,
                                String customServerName,
                                Properties serverProperties) {
        if (serverGroup.getMaxOnlineServers() != -1 && serverGroup.getMaxOnlineServers() != 0 &&
            CloudNet.getInstance().getServersAndWaitings(serverGroup.getName()).size() >= serverGroup.getMaxOnlineServers()) {
            return;
        }

        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(serverGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        if (serverGroup.getTemplates().size() == 0) {
            return;
        }
        Map<Template, Integer> templateMap = getTemplateStatistics(wrapper, serverGroup);

        Optional<Template> entry = templateMap.entrySet().stream()
                                              .min(Map.Entry.comparingByValue())
                                              .map(Map.Entry::getKey);

        entry.ifPresent(template -> {
            int startPort = getStartPort(wrapper);

            ServerProcessMeta serverProcessMeta =
                new ServerProcessMeta(newServiceId(serverGroup, wrapper, customServerName),
                                      memory,
                                      priorityStop,
                                      url,
                                      processParameters,
                                      onlineMode,
                                      plugins,
                                      config,
                                      customServerName,
                                      startPort,
                                      serverProperties,
                                      template);
            wrapper.startGameServer(serverProcessMeta);
        });
    }

    public int calcMemory(int groupMemory, int groupDynamicMemory, int onlineFromGroup, int globalUse) {
        if (groupMemory < 0 || groupDynamicMemory < 0) {
            return groupMemory < 0 ? 512 : groupMemory;
        }
        if (groupDynamicMemory <= groupMemory) {
            return groupMemory;
        }
        if (onlineFromGroup > 9) {
            return groupMemory;
        }
        if (onlineFromGroup == 0) {
            return groupDynamicMemory;
        }
        return ((groupDynamicMemory - groupMemory) / 100) * (((10 - onlineFromGroup) * 10)) + groupMemory;
    }

    public void startGameServer(ServerGroup serverGroup) {
        startGameServer(serverGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()));
    }

    public void startGameServer(Wrapper wrapper, ServerGroup serverGroup) {
        startGameServer(wrapper, serverGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()));
    }

    public void startGameServer(Wrapper wrapper, ServerGroup serverGroup, Document properties) {
        startGameServer(wrapper, serverGroup, new ServerConfig(false, "extra", properties, System.currentTimeMillis()));
    }

    public void startGameServer(Wrapper wrapper, ServerGroup serverGroup, String extra, Document properties) {
        startGameServer(wrapper, serverGroup, new ServerConfig(false, extra, properties, System.currentTimeMillis()));
    }

    public void startGameServer(Wrapper wrapper, ServerGroup serverGroup, boolean hideServer, Document properties) {
        startGameServer(wrapper, serverGroup, new ServerConfig(hideServer, "extra", properties, System.currentTimeMillis()));
    }

    public void startGameServer(ServerGroup serverGroup, Document properties) {
        startGameServer(serverGroup, new ServerConfig(false, "extra", properties, System.currentTimeMillis()));
    }

    public void startGameServer(ServerGroup serverGroup, String extra, Document properties) {
        startGameServer(serverGroup, new ServerConfig(false, extra, properties, System.currentTimeMillis()));
    }

    public void startGameServer(ServerGroup serverGroup, boolean hideServer, Document properties) {
        startGameServer(serverGroup, new ServerConfig(hideServer, "extra", properties, System.currentTimeMillis()));
    }

    public long globalUsedMemory() {
        return Stream.concat(getServers().values().stream()
                                         .map(MinecraftServer::getProcessMeta)
                                         .map(ServerProcessMeta::getMemory),
                             getProxys().values().stream()
                                        .map(ProxyServer::getProcessMeta)
                                        .map(ProxyProcessMeta::getMemory))
                     .mapToLong(l -> l)
                     .sum();
    }

    private Map<Template, Integer> getTemplateStatistics(final Wrapper wrapper, final ServerGroup serverGroup) {
        Map<Template, Integer> templateMap = new HashMap<>();


        getServers(serverGroup.getName()).stream()
                                         .map(MinecraftServer::getProcessMeta)
                                         .map(ServerProcessMeta::getTemplate)
                                         .forEach(template -> templateMap.merge(template, 1, Integer::sum));

        wrapper.getWaitingServices().values().stream()
               .filter(quad -> quad.getServiceId().getGroup().equals(serverGroup.getName()))
               .map(WaitingService::getTemplate)
               .forEach(template -> templateMap.merge(template, 1, Integer::sum));

        serverGroup.getTemplates()
                   .forEach(template -> templateMap.merge(template, 1, Integer::sum));
        return templateMap;
    }

    public void startGameServer(Wrapper wrapper, ServerGroup serverGroup, ServerConfig serverConfig, Properties serverProperties) {
        startGameServer(wrapper,
                        serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        false,
                        null,
                        EMPTY_STRING_ARRAY,
                        false,
                        Collections.emptyList(),
                        null,
                        serverProperties);
    }

    public void startGameServer(Wrapper wrapper,
                                ServerGroup serverGroup,
                                ServerConfig config,
                                int memory,
                                boolean priorityStop,
                                String url,
                                String[] processParameters,
                                boolean onlineMode,
                                Collection<ServerInstallablePlugin> plugins,
                                String customServerName,
                                Properties serverProperties) {
        if (serverGroup.getMaxOnlineServers() != -1 &&
            serverGroup.getMaxOnlineServers() != 0 &&
            CloudNet.getInstance().getServersAndWaitings(serverGroup.getName()).size() >= serverGroup.getMaxOnlineServers()) {
            return;
        }

        if (wrapper == null) {
            return;
        }

        if (serverGroup.getTemplates().size() == 0) {
            return;
        }
        Map<Template, Integer> templateMap = getTemplateStatistics(wrapper, serverGroup);

        Optional<Template> entry = templateMap.entrySet().stream()
                                              .min(Map.Entry.comparingByValue())
                                              .map(Map.Entry::getKey);

        entry.ifPresent(template -> {

            int startPort = getStartPort(wrapper);

            ServerProcessMeta serverProcessMeta =
                new ServerProcessMeta(newServiceId(serverGroup, wrapper, customServerName),
                                      memory,
                                      priorityStop,
                                      url,
                                      processParameters,
                                      onlineMode,
                                      plugins,
                                      config,
                                      customServerName,
                                      startPort,
                                      serverProperties,
                                      template);

            wrapper.startGameServer(serverProcessMeta);
        });
    }

    public void startGameServer(ServerGroup serverGroup, Document properties, String[] processProperties, Properties serverProperties) {
        startGameServer(serverGroup,
                        new ServerConfig(false, "extra", properties, System.currentTimeMillis()),
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        false,
                        null,
                        processProperties,
                        false,
                        Collections.emptyList(),
                        null,
                        serverProperties);
    }

    public void startGameServer(Wrapper wrapper, ServerGroup serverGroup, Document properties, Properties serverProperties) {
        startGameServer(wrapper,
                        serverGroup,
                        new ServerConfig(false, "extra", properties, System.currentTimeMillis()),
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        false,
                        null,
                        EMPTY_STRING_ARRAY,
                        false,
                        Collections.emptyList(),
                        null,
                        serverProperties);
    }

    public void startGameServer(ServerGroup serverGroup,
                                boolean hideServer,
                                Document properties,
                                String[] processProperties,
                                Properties serverProperties) {
        startGameServer(serverGroup,
                        new ServerConfig(hideServer, "extra", properties, System.currentTimeMillis()),
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        false,
                        null,
                        processProperties,
                        false,
                        Collections.emptyList(),
                        null,
                        serverProperties);
    }

    public void startGameServer(Wrapper wrapper,
                                boolean hideServer,
                                ServerGroup serverGroup,
                                Document properties,
                                Properties serverProperties) {
        startGameServer(wrapper,
                        serverGroup,
                        new ServerConfig(hideServer, "extra", properties, System.currentTimeMillis()),
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        false,
                        null,
                        EMPTY_STRING_ARRAY,
                        false,
                        Collections.emptyList(),
                        null,
                        serverProperties);
    }

    public void startGameServer(ServerGroup serverGroup,
                                ServerConfig serverConfig,
                                String[] processProperties,
                                Properties serverProperties) {
        startGameServer(serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        false,
                        null,
                        processProperties,
                        false,
                        Collections.emptyList(),
                        null,
                        serverProperties);
    }

    public void startGameServer(Wrapper wrapper,
                                ServerGroup serverGroup,
                                ServerConfig serverConfig,
                                String[] processProperties,
                                Properties serverProperties) {
        startGameServer(wrapper,
                        serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        false,
                        null,
                        processProperties,
                        false,
                        Collections.emptyList(),
                        null,
                        serverProperties);
    }

    public void startGameServer(ServerGroup serverGroup, ServerConfig serverConfig, boolean priorityStop) {
        startGameServer(serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        priorityStop,
                        null,
                        EMPTY_STRING_ARRAY,
                        false,
                        Collections.emptyList(),
                        null,
                        new Properties());
    }

    public void startGameServer(Wrapper wrapper, ServerGroup serverGroup, ServerConfig serverConfig, boolean priorityStop) {
        startGameServer(wrapper,
                        serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        priorityStop,
                        null,
                        EMPTY_STRING_ARRAY,
                        false,
                        Collections.emptyList(),
                        null,
                        new Properties());
    }

    public void startGameServer(ServerGroup serverGroup, ServerConfig serverConfig, boolean priorityStop, String[] processProperties) {
        startGameServer(serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        priorityStop,
                        null,
                        processProperties,
                        false,
                        Collections.emptyList(),
                        null,
                        new Properties());
    }

    public void startGameServer(Wrapper wrapper,
                                ServerGroup serverGroup,
                                ServerConfig serverConfig,
                                boolean priorityStop,
                                String[] processProperties) {
        startGameServer(wrapper,
                        serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        priorityStop,
                        null,
                        processProperties,
                        false,
                        Collections.emptyList(),
                        null,
                        new Properties());
    }

    public void startGameServer(ServerGroup serverGroup,
                                ServerConfig serverConfig,
                                boolean priorityStop,
                                Collection<ServerInstallablePlugin> plugins,
                                String customServerName,
                                boolean onlinemode) {
        startGameServer(serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        priorityStop,
                        null,
                        EMPTY_STRING_ARRAY,
                        onlinemode,
                        plugins,
                        customServerName,
                        new Properties());
    }

    public void startGameServer(Wrapper wrapper,
                                ServerGroup serverGroup,
                                ServerConfig serverConfig,
                                boolean priorityStop,
                                Collection<ServerInstallablePlugin> plugins,
                                String customServerName,
                                boolean onlineMode) {
        startGameServer(wrapper,
                        serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        priorityStop,
                        null,
                        EMPTY_STRING_ARRAY,
                        onlineMode,
                        plugins,
                        customServerName,
                        new Properties());
    }

    public void startGameServer(ServerGroup serverGroup, ServerConfig serverConfig) {
        startGameServer(serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        false,
                        null,
                        EMPTY_STRING_ARRAY,
                        false,
                        Collections.emptyList(),
                        null,
                        new Properties());
    }

    public void startGameServer(Wrapper wrapper, ServerGroup serverGroup, ServerConfig serverConfig) {
        startGameServer(wrapper,
                        serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        false,
                        null,
                        EMPTY_STRING_ARRAY,
                        false,
                        Collections.emptyList(),
                        null,
                        new Properties());
    }

    public void startGameServer(ServerGroup serverGroup, Document document, boolean priorityStop) {
        startGameServer(serverGroup,
                        new ServerConfig(false, "extra", document, System.currentTimeMillis()),
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        priorityStop,
                        null,
                        EMPTY_STRING_ARRAY,
                        false,
                        Collections.emptyList(),
                        null,
                        new Properties());
    }

    public void startGameServer(ServerGroup serverGroup,
                                ServerConfig serverConfig,
                                String url,
                                Collection<ServerInstallablePlugin> plugins) {
        startGameServer(serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        false,
                        url,
                        EMPTY_STRING_ARRAY,
                        false,
                        plugins,
                        null,
                        new Properties());
    }

    public void startGameServer(Wrapper wrapper,
                                ServerGroup serverGroup,
                                ServerConfig serverConfig,
                                String url,
                                Collection<ServerInstallablePlugin> plugins) {
        startGameServer(wrapper,
                        serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        false,
                        url,
                        EMPTY_STRING_ARRAY,
                        false,
                        plugins,
                        null,
                        new Properties());
    }

    public void startGameServer(ServerGroup serverGroup,
                                String serverId,
                                ServerConfig config,
                                int memory,
                                boolean priorityStop,
                                String url,
                                String[] processParameters,
                                boolean onlineMode,
                                Collection<ServerInstallablePlugin> plugins,
                                String customServerName,
                                Properties serverProperties) {
        if (serverGroup.getMaxOnlineServers() != -1 && serverGroup.getMaxOnlineServers() != 0 && CloudNet.getInstance()
                                                                                                         .getServersAndWaitings(serverGroup.getName())
                                                                                                         .size() >= serverGroup.getMaxOnlineServers()) {
            return;
        }

        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(serverGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        if (serverGroup.getTemplates().size() == 0) {
            return;
        }
        Map<Template, Integer> templateMap = getTemplateStatistics(wrapper, serverGroup);
        Optional<Template> entry = templateMap.entrySet().stream()
                                              .min(Map.Entry.comparingByValue())
                                              .map(Map.Entry::getKey);

        entry.ifPresent(template -> {

            int startPort = getStartPort(wrapper);

            ServerProcessMeta serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper, serverId),
                                                                        memory,
                                                                        priorityStop,
                                                                        url,
                                                                        processParameters,
                                                                        onlineMode,
                                                                        plugins,
                                                                        config,
                                                                        customServerName,
                                                                        startPort,
                                                                        serverProperties,
                                                                        template);
            wrapper.startGameServer(serverProcessMeta);
        });
    }

    public void startGameServer(ServerGroup serverGroup,
                                ServerConfig config,
                                Template template,
                                int memory,
                                boolean priorityStop,
                                String url,
                                String[] processParameters,
                                boolean onlineMode,
                                Collection<ServerInstallablePlugin> plugins,
                                String customServerName,
                                Properties serverProperties) {
        if (serverGroup.getMaxOnlineServers() != -1 && serverGroup.getMaxOnlineServers() != 0 && CloudNet.getInstance()
                                                                                                         .getServersAndWaitings(serverGroup.getName())
                                                                                                         .size() >= serverGroup.getMaxOnlineServers()) {
            return;
        }

        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(serverGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        if (serverGroup.getTemplates().size() == 0) {
            return;
        }
        if (template == null) {
            return;
        }
        int startPort = getStartPort(wrapper);

        ServerProcessMeta serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper, customServerName),
                                                                    memory,
                                                                    priorityStop,
                                                                    url,
                                                                    processParameters,
                                                                    onlineMode,
                                                                    plugins,
                                                                    config,
                                                                    customServerName,
                                                                    startPort,
                                                                    serverProperties,
                                                                    template);

        wrapper.startGameServer(serverProcessMeta);
    }

    public void startGameServer(Wrapper wrapper,
                                ServerGroup serverGroup,
                                ServerConfig config,
                                Template template,
                                int memory,
                                boolean priorityStop,
                                String url,
                                String[] processParameters,
                                boolean onlineMode,
                                Collection<ServerInstallablePlugin> plugins,
                                String customServerName,
                                Properties serverProperties) {
        if (serverGroup.getMaxOnlineServers() != -1 &&
            serverGroup.getMaxOnlineServers() != 0 &&
            CloudNet.getInstance().getServersAndWaitings(serverGroup.getName()).size() >= serverGroup.getMaxOnlineServers()) {
            return;
        }

        if (wrapper == null) {
            return;
        }
        if (template == null) {
            return;
        }

        if (serverGroup.getTemplates().size() == 0) {
            return;
        }
        int startPort = getStartPort(wrapper);

        ServerProcessMeta serverProcessMeta;
        if (customServerName != null) {
            serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper, customServerName),
                                                      memory,
                                                      priorityStop,
                                                      url,
                                                      processParameters,
                                                      onlineMode,
                                                      plugins,
                                                      config,
                                                      customServerName,
                                                      startPort,
                                                      serverProperties,
                                                      template);
        } else {
            serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper),
                                                      memory,
                                                      priorityStop,
                                                      url,
                                                      processParameters,
                                                      onlineMode,
                                                      plugins,
                                                      config,
                                                      customServerName,
                                                      startPort,
                                                      serverProperties,
                                                      template);
        }

        wrapper.startGameServer(serverProcessMeta);
    }

    public void startGameServer(Wrapper wrapper,
                                String serverId,
                                ServerGroup serverGroup,
                                ServerConfig config,
                                int memory,
                                boolean priorityStop,
                                String url,
                                String[] processParameters,
                                boolean onlineMode,
                                Collection<ServerInstallablePlugin> plugins,
                                String customServerName,
                                Properties serverProperties) {
        if (serverGroup.getMaxOnlineServers() != -1 &&
            serverGroup.getMaxOnlineServers() != 0 &&
            CloudNet.getInstance().getServersAndWaitings(serverGroup.getName()).size() >= serverGroup.getMaxOnlineServers()) {
            return;
        }

        if (wrapper == null) {
            return;
        }

        if (serverGroup.getTemplates().size() == 0) {
            return;
        }
        Map<Template, Integer> templateMap = getTemplateStatistics(wrapper, serverGroup);

        Optional<Template> entry = templateMap.entrySet().stream()
                                              .min(Map.Entry.comparingByValue())
                                              .map(Map.Entry::getKey);

        entry.ifPresent(template -> {

            int startPort = getStartPort(wrapper);

            List<Template> templates = new ArrayList<>(serverGroup.getTemplates());
            if (templates.size() == 0) {
                return;
            }

            ServerProcessMeta serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper, serverId),
                                                                        memory,
                                                                        priorityStop,
                                                                        url,
                                                                        processParameters,
                                                                        onlineMode,
                                                                        plugins,
                                                                        config,
                                                                        customServerName,
                                                                        startPort,
                                                                        serverProperties,
                                                                        template);
            wrapper.startGameServer(serverProcessMeta);
        });
    }

    public void startProxyAsync(ProxyProcessMeta proxyProcessMeta, Wrapper wrapper) {
        wrapper.startProxyAsync(proxyProcessMeta);
    }

    public void startProxyAsync(ProxyGroup proxyGroup) {
        Wrapper wrapper = fetchPerformanceWrapper(proxyGroup.getMemory(), toWrapperInstances(proxyGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }
        List<Integer> ports = wrapper.getBoundPorts();
        int startPort = proxyGroup.getStartPort();
        while (ports.contains(startPort)) {
            startPort++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper),
                                                                 proxyGroup.getMemory(),
                                                                 startPort,
                                                                 EMPTY_STRING_ARRAY,
                                                                 null,
                                                                 Collections.emptyList(),
                                                                 new Document());
        wrapper.startProxyAsync(proxyProcessMeta);
    }

    public void startProxyAsync(ProxyGroup proxyGroup, int memory) {
        startProxyAsync(proxyGroup, memory, null, Collections.emptyList(), new Document());
    }

    public void startProxyAsync(ProxyGroup proxyGroup,
                                int memory,
                                String url,
                                Collection<ServerInstallablePlugin> plugins,
                                Document document) {
        startProxyAsync(proxyGroup, memory, EMPTY_STRING_ARRAY, url, plugins, document);
    }

    public void startProxyAsync(ProxyGroup proxyGroup,
                                int memory,
                                String[] parameters,
                                String url,
                                Collection<ServerInstallablePlugin> plugins,
                                Document document) {
        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(proxyGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        List<Integer> ports = wrapper.getBoundPorts();
        int startPort = proxyGroup.getStartPort();
        while (ports.contains(startPort)) {
            startPort++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper),
                                                                 memory,
                                                                 startPort,
                                                                 parameters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxyAsync(proxyProcessMeta);
    }

    public void startProxyAsync(ProxyGroup proxyGroup, Collection<ServerInstallablePlugin> plugins) {
        startProxyAsync(proxyGroup, proxyGroup.getMemory(), null, plugins, new Document());
    }

    public void startProxyAsync(ProxyGroup proxyGroup, String urlTemplate) {
        startProxyAsync(proxyGroup, proxyGroup.getMemory(), urlTemplate, Collections.emptyList(), new Document());
    }

    public void startProxyAsync(ProxyGroup proxyGroup, String urlTemplate, Document document) {
        startProxyAsync(proxyGroup, proxyGroup.getMemory(), urlTemplate, Collections.emptyList(), document);
    }

    public void startProxyAsync(ProxyGroup proxyGroup, int memory, UUID uniqueId) {
        startProxyAsync(proxyGroup, memory, EMPTY_STRING_ARRAY, null, Collections.emptyList(), new Document(), uniqueId);
    }

    public void startProxyAsync(ProxyGroup proxyGroup,
                                int memory,
                                String[] parameters,
                                String url,
                                Collection<ServerInstallablePlugin> plugins,
                                Document document,
                                UUID uniqueId) {
        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(proxyGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        List<Integer> ports = wrapper.getBoundPorts();
        int startPort = proxyGroup.getStartPort();
        while (ports.contains(startPort)) {
            startPort++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper, uniqueId),
                                                                 memory,
                                                                 startPort,
                                                                 parameters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxyAsync(proxyProcessMeta);
    }

    public void startProxyAsync(ProxyGroup proxyGroup, int memory, int id, UUID uniqueId) {
        startProxyAsync(proxyGroup, memory, EMPTY_STRING_ARRAY, null, Collections.emptyList(), new Document(), id, uniqueId);
    }

    public void startProxyAsync(ProxyGroup proxyGroup,
                                int memory,
                                String[] parameters,
                                String url,
                                Collection<ServerInstallablePlugin> plugins,
                                Document document,
                                int id,
                                UUID uniqueId) {
        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(proxyGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        List<Integer> ports = wrapper.getBoundPorts();
        int startPort = proxyGroup.getStartPort();
        while (ports.contains(startPort)) {
            startPort++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper, id, uniqueId),
                                                                 memory,
                                                                 startPort,
                                                                 parameters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxyAsync(proxyProcessMeta);
    }

    public void startProxyAsync(ProxyGroup proxyGroup, int memory, String urlTemplate, int id, UUID uniqueId) {
        startProxyAsync(proxyGroup, memory, EMPTY_STRING_ARRAY, urlTemplate, Collections.emptyList(), new Document(), id, uniqueId);
    }

    public void startProxyAsync(ProxyGroup proxyGroup, String url, Collection<ServerInstallablePlugin> collection, int id, UUID uniqueId) {
        startProxyAsync(proxyGroup, proxyGroup.getMemory(), EMPTY_STRING_ARRAY, url, collection, new Document(), id, uniqueId);
    }

    public void startGameServerAsync(ServerGroup serverGroup) {
        startGameServerAsync(serverGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()));
    }

    public void startGameServerAsync(Wrapper wrapper, ServerGroup serverGroup) {
        startGameServerAsync(wrapper, serverGroup, new ServerConfig(false, "extra", new Document(), System.currentTimeMillis()));
    }

    public void startGameServerAsync(Wrapper wrapper, ServerGroup serverGroup, Document properties) {
        startGameServerAsync(wrapper, serverGroup, new ServerConfig(false, "extra", properties, System.currentTimeMillis()));
    }

    public void startGameServerAsync(Wrapper wrapper, ServerGroup serverGroup, String extra, Document properties) {
        startGameServerAsync(wrapper, serverGroup, new ServerConfig(false, extra, properties, System.currentTimeMillis()));
    }

    public void startGameServerAsync(Wrapper wrapper, ServerGroup serverGroup, boolean hideServer, Document properties) {
        startGameServerAsync(wrapper, serverGroup, new ServerConfig(hideServer, "extra", properties, System.currentTimeMillis()));
    }

    public void startGameServerAsync(ServerGroup serverGroup, Document properties) {
        startGameServerAsync(serverGroup, new ServerConfig(false, "extra", properties, System.currentTimeMillis()));
    }

    public void startGameServerAsync(ServerGroup serverGroup, String extra, Document properties) {
        startGameServerAsync(serverGroup, new ServerConfig(false, extra, properties, System.currentTimeMillis()));
    }

    public void startGameServerAsync(ServerGroup serverGroup, boolean hideServer, Document properties) {
        startGameServerAsync(serverGroup, new ServerConfig(hideServer, "extra", properties, System.currentTimeMillis()));
    }

    public void startGameServerAsync(ServerGroup serverGroup, ServerConfig serverConfig, Properties serverProperties) {
        startGameServerAsync(serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             false,
                             null,
                             EMPTY_STRING_ARRAY,
                             false,
                             Collections.emptyList(),
                             null,
                             serverProperties);
    }

    public void startGameServerAsync(ServerGroup serverGroup,
                                     ServerConfig config,
                                     int memory,
                                     boolean priorityStop,
                                     String url,
                                     String[] processParameters,
                                     boolean onlineMode,
                                     Collection<ServerInstallablePlugin> plugins,
                                     String customServerName,
                                     Properties serverProperties) {
        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(serverGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        if (serverGroup.getTemplates().size() == 0) {
            return;
        }
        Map<Template, Integer> templateMap = getTemplateStatistics(wrapper, serverGroup);

        Optional<Template> entry = templateMap.entrySet().stream()
                                              .min(Map.Entry.comparingByValue())
                                              .map(Map.Entry::getKey);

        entry.ifPresent(template -> {
            int startPort = getStartPort(wrapper);

            ServerProcessMeta serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper),
                                                                        memory,
                                                                        priorityStop,
                                                                        url,
                                                                        processParameters,
                                                                        onlineMode,
                                                                        plugins,
                                                                        config,
                                                                        customServerName,
                                                                        startPort,
                                                                        serverProperties,
                                                                        template);
            wrapper.startGameServerAsync(serverProcessMeta);
        });
    }

    public void startGameServerAsync(Wrapper wrapper, ServerGroup serverGroup, ServerConfig serverConfig, Properties serverProperties) {
        startGameServerAsync(wrapper,
                             serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             false,
                             null,
                             EMPTY_STRING_ARRAY,
                             false,
                             Collections.emptyList(),
                             null,
                             serverProperties);
    }

    public void startGameServerAsync(Wrapper wrapper,
                                     ServerGroup serverGroup,
                                     ServerConfig config,
                                     int memory,
                                     boolean priorityStop,
                                     String url,
                                     String[] processParameters,
                                     boolean onlineMode,
                                     Collection<ServerInstallablePlugin> plugins,
                                     String customServerName,
                                     Properties serverProperties) {
        if (wrapper == null) {
            return;
        }

        if (serverGroup.getTemplates().size() == 0) {
            return;
        }
        Map<Template, Integer> templateMap = getTemplateStatistics(wrapper, serverGroup);

        Optional<Template> entry = templateMap.entrySet().stream()
                                              .min(Map.Entry.comparingByValue())
                                              .map(Map.Entry::getKey);

        entry.ifPresent(template -> {

            int startPort = getStartPort(wrapper);

            List<Template> templates = new ArrayList<>(serverGroup.getTemplates());
            if (templates.size() == 0) {
                return;
            }

            ServerProcessMeta serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper),
                                                                        memory,
                                                                        priorityStop,
                                                                        url,
                                                                        processParameters,
                                                                        onlineMode,
                                                                        plugins,
                                                                        config,
                                                                        customServerName,
                                                                        startPort,
                                                                        serverProperties,
                                                                        template);
            wrapper.startGameServerAsync(serverProcessMeta);
        });
    }

    public void startGameServerAsync(ServerGroup serverGroup,
                                     Document properties,
                                     String[] processProperties,
                                     Properties serverProperties) {
        startGameServerAsync(serverGroup,
                             new ServerConfig(false, "extra", properties, System.currentTimeMillis()),
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             false,
                             null,
                             processProperties,
                             false,
                             Collections.emptyList(),
                             null,
                             serverProperties);
    }

    public void startGameServerAsync(Wrapper wrapper, ServerGroup serverGroup, Document properties, Properties serverProperties) {
        startGameServerAsync(wrapper,
                             serverGroup,
                             new ServerConfig(false, "extra", properties, System.currentTimeMillis()),
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             false,
                             null,
                             EMPTY_STRING_ARRAY,
                             false,
                             Collections.emptyList(),
                             null,
                             serverProperties);
    }

    public void startGameServerAsync(ServerGroup serverGroup,
                                     boolean hideServer,
                                     Document properties,
                                     String[] processProperties,
                                     Properties serverProperties) {
        startGameServerAsync(serverGroup,
                             new ServerConfig(hideServer, "extra", properties, System.currentTimeMillis()),
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             false,
                             null,
                             processProperties,
                             false,
                             Collections.emptyList(),
                             null,
                             serverProperties);
    }

    public void startGameServerAsync(Wrapper wrapper,
                                     boolean hideServer,
                                     ServerGroup serverGroup,
                                     Document properties,
                                     Properties serverProperties) {
        startGameServerAsync(wrapper,
                             serverGroup,
                             new ServerConfig(hideServer, "extra", properties, System.currentTimeMillis()),
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             false,
                             null,
                             EMPTY_STRING_ARRAY,
                             false,
                             Collections.emptyList(),
                             null,
                             serverProperties);
    }

    public void startGameServerAsync(ServerGroup serverGroup,
                                     ServerConfig serverConfig,
                                     String[] processProperties,
                                     Properties serverProperties) {
        startGameServerAsync(serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             false,
                             null,
                             processProperties,
                             false,
                             Collections.emptyList(),
                             null,
                             serverProperties);
    }

    public void startGameServerAsync(Wrapper wrapper,
                                     ServerGroup serverGroup,
                                     ServerConfig serverConfig,
                                     String[] processProperties,
                                     Properties serverProperties) {
        startGameServerAsync(wrapper,
                             serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             false,
                             null,
                             processProperties,
                             false,
                             Collections.emptyList(),
                             null,
                             serverProperties);
    }

    public void startGameServerAsync(ServerGroup serverGroup, ServerConfig serverConfig, boolean priorityStop) {
        startGameServerAsync(serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             priorityStop,
                             null,
                             EMPTY_STRING_ARRAY,
                             false,
                             Collections.emptyList(),
                             null,
                             new Properties());
    }

    public void startGameServerAsync(Wrapper wrapper, ServerGroup serverGroup, ServerConfig serverConfig, boolean priorityStop) {
        startGameServerAsync(wrapper,
                             serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             priorityStop,
                             null,
                             EMPTY_STRING_ARRAY,
                             false,
                             Collections.emptyList(),
                             null,
                             new Properties());
    }

    public void startGameServerAsync(ServerGroup serverGroup, ServerConfig serverConfig, boolean priorityStop, String[] processProperties) {
        startGameServerAsync(serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             priorityStop,
                             null,
                             processProperties,
                             false,
                             Collections.emptyList(),
                             null,
                             new Properties());
    }

    public void startGameServerAsync(Wrapper wrapper,
                                     ServerGroup serverGroup,
                                     ServerConfig serverConfig,
                                     boolean priorityStop,
                                     String[] processProperties) {
        startGameServerAsync(wrapper,
                             serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             priorityStop,
                             null,
                             processProperties,
                             false,
                             Collections.emptyList(),
                             null,
                             new Properties());
    }

    public void startGameServerAsync(ServerGroup serverGroup,
                                     ServerConfig serverConfig,
                                     boolean priorityStop,
                                     Collection<ServerInstallablePlugin> plugins,
                                     String customServerName,
                                     boolean onlinemode) {
        startGameServerAsync(serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             priorityStop,
                             null,
                             EMPTY_STRING_ARRAY,
                             onlinemode,
                             plugins,
                             customServerName,
                             new Properties());
    }

    public void startGameServerAsync(Wrapper wrapper,
                                     ServerGroup serverGroup,
                                     ServerConfig serverConfig,
                                     boolean priorityStop,
                                     Collection<ServerInstallablePlugin> plugins,
                                     String customServerName,
                                     boolean onlineMode) {
        startGameServerAsync(wrapper,
                             serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             priorityStop,
                             null,
                             EMPTY_STRING_ARRAY,
                             onlineMode,
                             plugins,
                             customServerName,
                             new Properties());
    }

    public void startGameServerAsync(ServerGroup serverGroup, ServerConfig serverConfig) {
        startGameServerAsync(serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             false,
                             null,
                             EMPTY_STRING_ARRAY,
                             false,
                             Collections.emptyList(),
                             null,
                             new Properties());
    }

    public void startGameServerAsync(Wrapper wrapper, ServerGroup serverGroup, ServerConfig serverConfig) {
        startGameServerAsync(wrapper,
                             serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             false,
                             null,
                             EMPTY_STRING_ARRAY,
                             false,
                             Collections.emptyList(),
                             null,
                             new Properties());
    }

    public void startGameServerAsync(ServerGroup serverGroup,
                                     ServerConfig serverConfig,
                                     String url,
                                     Collection<ServerInstallablePlugin> plugins) {
        startGameServerAsync(serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             false,
                             url,
                             EMPTY_STRING_ARRAY,
                             false,
                             plugins,
                             null,
                             new Properties());
    }

    public void startGameServerAsync(Wrapper wrapper,
                                     ServerGroup serverGroup,
                                     ServerConfig serverConfig,
                                     String url,
                                     Collection<ServerInstallablePlugin> plugins) {
        startGameServerAsync(wrapper,
                             serverGroup,
                             serverConfig,
                             calcMemory(serverGroup.getMemory(),
                                        serverGroup.getDynamicMemory(),
                                        getServers(serverGroup.getName()).size(),
                                        (int) globalUsedMemory()),
                             false,
                             url,
                             EMPTY_STRING_ARRAY,
                             false,
                             plugins,
                             null,
                             new Properties());
    }

    public void startGameServerAsync(Wrapper wrapper,
                                     String serverId,
                                     ServerGroup serverGroup,
                                     ServerConfig config,
                                     int memory,
                                     boolean priorityStop,
                                     String url,
                                     String[] processParameters,
                                     boolean onlineMode,
                                     Collection<ServerInstallablePlugin> plugins,
                                     String customServerName,
                                     Properties serverProperties) {
        if (wrapper == null) {
            return;
        }

        if (serverGroup.getTemplates().size() == 0) {
            return;
        }
        Map<Template, Integer> templateMap = getTemplateStatistics(wrapper, serverGroup);

        Optional<Template> entry = templateMap.entrySet().stream()
                                              .min(Map.Entry.comparingByValue())
                                              .map(Map.Entry::getKey);

        entry.ifPresent(template -> {

            int startPort = getStartPort(wrapper);

            ServerProcessMeta serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper, serverId),
                                                                        memory,
                                                                        priorityStop,
                                                                        url,
                                                                        processParameters,
                                                                        onlineMode,
                                                                        plugins,
                                                                        config,
                                                                        customServerName,
                                                                        startPort,
                                                                        serverProperties,
                                                                        template);
            wrapper.startGameServerAsync(serverProcessMeta);
        });
    }

    public ServiceId newServiceId(ServerGroup serverGroup, Wrapper wrapper, String serverId) {
        int id = 1;
        Collection<ServiceId> serviceIds = getServerServiceIdsAndWaitings(serverGroup.getName());
        List<Integer> serverIds = serviceIds.stream()
                                            .map(ServiceId::getId)
                                            .collect(Collectors.toList());
        while (serverIds.contains(id)) {
            id++;
        }
        if (serverId == null) {
            serverId = serverGroup.getName() + this.config.getFormatSplitter() + id;
        }

        return new ServiceId(serverGroup.getName(), id, UUID.randomUUID(), wrapper.getNetworkInfo().getId(), serverId);
    }
}
