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
import de.dytanic.cloudnet.lib.scheduler.TaskScheduler;
import de.dytanic.cloudnet.lib.server.*;
import de.dytanic.cloudnet.lib.server.defaults.BasicServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.user.User;
import de.dytanic.cloudnet.lib.utility.*;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import de.dytanic.cloudnet.lib.utility.threading.Scheduler;
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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public final class CloudNet implements Executable, Runnable, Reloadable {

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
    private final Scheduler scheduler = new Scheduler(50);
    private final java.util.Map<String, Wrapper> wrappers = NetworkUtils.newConcurrentHashMap();
    private final java.util.Map<String, ServerGroup> serverGroups = NetworkUtils.newConcurrentHashMap();
    private final java.util.Map<String, ProxyGroup> proxyGroups = NetworkUtils.newConcurrentHashMap();
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
    private List<String> preConsoleOutput;
    private Collection<User> users;
    private long startupTime = System.currentTimeMillis();
    private boolean downTown = true;

    public CloudNet(CloudConfig config, CloudLogger cloudNetLogging, OptionSet optionSet, List<String> objective, List<String> args) throws
        Exception {
        if (instance == null) {
            instance = this;
        }

        this.config = config;
        this.logger = cloudNetLogging;
        this.preConsoleOutput = objective;
        this.optionSet = optionSet;
        this.arguments = args;
        this.defaultModuleManager = new DefaultModuleManager();

        this.logger.getReader().addCompleter(commandManager);
    }

    public static boolean isRUNNING() {
        return RUNNING;
    }

    public static CloudLogger getLogger() {
        return getInstance().logger;
    }

    public static CloudNet getInstance() {
        return instance;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public Scheduler getScheduler() {
        return scheduler;
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

    public List<String> getPreConsoleOutput() {
        return preConsoleOutput;
    }

    public LocalCloudWrapper getLocalCloudWrapper() {
        return localCloudWrapper;
    }

    public long getStartupTime() {
        return startupTime;
    }

    public boolean isDownTown() {
        return downTown;
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

        NetworkUtils.addAll(this.serverGroups, config.getServerGroups(), new Acceptable<ServerGroup>() {
            @Override
            public boolean isAccepted(ServerGroup value) {
                System.out.println("Loading ServerGroup: " + value.getName());
                setupGroup(value);
                return true;
            }
        });

        NetworkUtils.addAll(this.proxyGroups, config.getProxyGroups(), new Acceptable<ProxyGroup>() {
            @Override
            public boolean isAccepted(ProxyGroup value) {
                System.out.println("Loading ProxyGroup: " + value.getName());
                setupProxy(value);
                return true;
            }
        });

        webServer = new WebServer(optionSet.has("ssl"), config.getWebServerConfig().getAddress(), config.getWebServerConfig().getPort());

        this.initialCommands();
        this.initWebHandlers();
        this.initPacketHandlers();

        {
            Thread thread = new Thread(scheduler);
            thread.setDaemon(true);
            thread.start();
        }

        for (ConnectableAddress connectableAddress : config.getAddresses()) {
            new CloudNetServer(optionSet, connectableAddress);
        }

        webServer.bind();

        RUNNING = true;
        Runtime.getRuntime().addShutdownHook(new Thread(this));

        {
            if (!optionSet.has("onlyConsole")) {
                CloudStartupHandler cloudStartupHandler = new CloudStartupHandler();
                CloudPriorityStartupHandler cloudPriorityStartupHandler = new CloudPriorityStartupHandler();
                CloudPriorityGroupStartupHandler cloudPriorityGroupStartupHandler = new CloudPriorityGroupStartupHandler();
                CloudPlayerRemoverHandler cloudPlayerRemoverHandler = new CloudPlayerRemoverHandler();

                scheduler.runTaskRepeatSync(cloudStartupHandler, 0, cloudStartupHandler.getTicks());
                scheduler.runTaskRepeatSync(cloudPriorityGroupStartupHandler, 0, cloudPriorityGroupStartupHandler.getTicks());
                scheduler.runTaskRepeatSync(cloudPriorityStartupHandler, 0, cloudPriorityStartupHandler.getTicks());
                scheduler.runTaskRepeatSync(cloudPlayerRemoverHandler, 0, cloudPlayerRemoverHandler.getTicks());
            }

            CloudStopCheckHandler cloudStopCheck = new CloudStopCheckHandler();

            scheduler.runTaskRepeatSync(cloudStopCheck, 0, cloudStopCheck.getTicks());
            scheduler.runTaskRepeatSync(serverLogManager, 0, 2000);

            scheduler.runTaskRepeatSync(new Runnable() {
                @Override
                public void run() {
                    for (CloudPlayer cloudPlayer : networkManager.getWaitingPlayers().values()) {
                        if ((cloudPlayer.getLoginTimeStamp().getTime() + 10000L) < System.currentTimeMillis()) {
                            networkManager.getWaitingPlayers().remove(cloudPlayer.getUniqueId());
                        }
                    }
                }
            }, 0, 100);
        }

        if (!optionSet.has("disable-modules")) {
            System.out.println("Enabling Modules...");
            moduleManager.enableModules();
        }

        eventManager.callEvent(new CloudInitEvent());
        this.localCloudWrapper.run(optionSet);

        return true;
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
    @Override
    public boolean shutdown() {
        if (!RUNNING) {
            return false;
        }
        TaskScheduler.runtimeScheduler().shutdown();

        this.scheduler.cancelAllTasks();

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
        if (downTown) {
            System.exit(0);
        }
        return true;
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

        NetworkUtils.addAll(this.serverGroups, config.getServerGroups(), new Acceptable<ServerGroup>() {
            public boolean isAccepted(ServerGroup value) {
                System.out.println("Loading server group: " + value.getName());
                setupGroup(value);
                return true;
            }
        });

        NetworkUtils.addAll(this.proxyGroups, config.getProxyGroups(), new Acceptable<ProxyGroup>() {
            public boolean isAccepted(ProxyGroup value) {
                System.out.println("Loading proxy group: " + value.getName());
                setupProxy(value);
                return true;
            }
        });

        this.initialCommands();
        this.initWebHandlers();
        this.initPacketHandlers();

        if (!optionSet.has("disable-modules")) {
            this.moduleManager.loadModules().enableModules();
        }

        System.out.println("Updating wrappers...");
        for (Wrapper wrapper : wrappers.values()) {
            wrapper.updateWrapper();
        }

        networkManager.reload();
        networkManager.updateAll();
    }

    @Deprecated
    @Override
    public void run() {
        downTown = false;
        shutdown();
    }

    public boolean authorization(String name, String token) {
        User user = CollectionWrapper.filter(users, new Acceptable<User>() {
            @Override
            public boolean isAccepted(User value) {
                return value.getName().equalsIgnoreCase(name);
            }
        });
        if (user != null) {
            if (user.getApiToken().equals(token)) {
                return true;
            }
        }
        return false;
    }

    public boolean authorizationPassword(String name, String password) {
        User user = CollectionWrapper.filter(users, new Acceptable<User>() {
            @Override
            public boolean isAccepted(User value) {
                return value.getName().equalsIgnoreCase(name);
            }
        });
        if (user != null) {
            if (user.getHashedPassword().equals(DyHash.hashString(password))) {
                return true;
            }
        }
        return false;
    }

    public ServerGroup getServerGroup(String group) {
        return serverGroups.get(group);
    }

    public ProxyGroup getProxyGroup(String group) {
        return proxyGroups.get(group);
    }

    public User getUser(String name) {
        return CollectionWrapper.filter(users, new Acceptable<User>() {
            @Override
            public boolean isAccepted(User value) {
                return name.toLowerCase().equals(value.getName().toLowerCase());
            }
        });
    }

    public int getGlobalUsedMemoryAndWaitings() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        CollectionWrapper.iterator(CloudNet.getInstance().getWrappers().values(), new Runnabled<Wrapper>() {
            @Override
            public void run(Wrapper obj) {
                atomicInteger.addAndGet(obj.getUsedMemory());

                for (Quad<Integer, Integer, ServiceId, Template> serviceIdTrio : obj.getWaitingServices().values()) {
                    atomicInteger.addAndGet(serviceIdTrio.getSecond());
                }
            }
        });
        return atomicInteger.get();
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
        Collection<Integer> collection = CollectionWrapper.transform(serviceIds, new Catcher<Integer, ServiceId>() {
            @Override
            public Integer doCatch(ServiceId key) {
                return key.getId();
            }
        });
        while (collection.contains(id)) {
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
        Collection<Integer> collection = CollectionWrapper.transform(serviceIds, new Catcher<Integer, ServiceId>() {
            @Override
            public Integer doCatch(ServiceId key) {
                return key.getId();
            }
        });
        while (collection.contains(id)) {
            id++;
        }
        return new ServiceId(serverGroup.getName(),
                             id,
                             uniqueId,
                             wrapper.getNetworkInfo().getId(),
                             serverGroup.getName() + config.getFormatSplitter() + id);
    }

    public Collection<ServiceId> getServerServiceIdsAndWaitings(String group) {
        Collection<ServiceId> strings = CollectionWrapper.transform(getServers(group), new Catcher<ServiceId, MinecraftServer>() {
            @Override
            public ServiceId doCatch(MinecraftServer key) {
                return key.getServiceId();
            }
        });

        for (Wrapper wrapper : wrappers.values()) {
            for (Map.Entry<String, Quad<Integer, Integer, ServiceId, Template>> serviceId : wrapper.getWaitingServices().entrySet()) {
                if (serviceId.getValue().getThird().getGroup().equalsIgnoreCase(group)) {
                    strings.add(serviceId.getValue().getThird());
                }
            }
        }
        return strings;
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

    public java.util.Map<String, MinecraftServer> getServers() {
        java.util.Map<String, MinecraftServer> minecraftServerMap = new HashMap<>();

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
        int id = 0;
        Collection<ServiceId> serviceIds = getServerServiceIdsAndWaitings(serverGroup.getName());
        Collection<Integer> collection = CollectionWrapper.transform(serviceIds, new Catcher<Integer, ServiceId>() {
            @Override
            public Integer doCatch(ServiceId key) {
                return key.getId();
            }
        });
        while (collection.contains(id)) {
            id++;
        }
        return new ServiceId(serverGroup.getName(), id, uniqueId, wrapper.getNetworkInfo().getId(), serverId);
    }

    public java.util.Map<String, MinecraftServer> getGameServers() {
        java.util.Map<String, MinecraftServer> minecraftServerMap = new HashMap<>();

        return minecraftServerMap;
    }

    public long globalMaxMemory() {
        AtomicInteger atomicInteger = new AtomicInteger();
        CollectionWrapper.iterator(getWrappers().values(), new Runnabled<Wrapper>() {
            @Override
            public void run(Wrapper obj) {
                atomicInteger.addAndGet(obj.getMaxMemory());
            }
        });
        return atomicInteger.get();
    }

    public long globalUsedMemory() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        CollectionWrapper.iterator(getServers().values(), new Runnabled<MinecraftServer>() {
            @Override
            public void run(MinecraftServer obj) {
                atomicInteger.addAndGet(obj.getProcessMeta().getMemory());
            }
        });
        CollectionWrapper.iterator(getProxys().values(), new Runnabled<ProxyServer>() {
            @Override
            public void run(ProxyServer obj) {
                atomicInteger.addAndGet(obj.getProcessMeta().getMemory());
            }
        });
        return atomicInteger.get();
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

    public Map<String, CloudServer> getCloudGameServers() {
        Map<String, CloudServer> cloudServerMap = new HashMap<>();

        for (Wrapper wrapper : wrappers.values()) {
            NetworkUtils.addAll(cloudServerMap, wrapper.getCloudServers());
        }

        return cloudServerMap;
    }

    public Collection<String> getServersAndWaitings(String group) {
        Collection<String> strings = CollectionWrapper.transform(getServers(group), new Catcher<String, MinecraftServer>() {
            @Override
            public String doCatch(MinecraftServer key) {
                return key.getServerId();
            }
        });

        for (Wrapper wrapper : wrappers.values()) {
            for (Map.Entry<String, Quad<Integer, Integer, ServiceId, Template>> serviceId : wrapper.getWaitingServices().entrySet()) {
                if (serviceId.getValue().getThird().getGroup().equalsIgnoreCase(group)) {
                    strings.add(serviceId.getKey());
                }
            }
        }
        return strings;
    }

    public Collection<Trio<String, Integer, Integer>> getServersAndWaitingData(String group) {
        Collection<Trio<String, Integer, Integer>> strings = CollectionWrapper.transform(getServers(group),
                                                                                         new Catcher<Trio<String, Integer, Integer>, MinecraftServer>() {
                                                                                             @Override
                                                                                             public Trio<String, Integer, Integer> doCatch(
                                                                                                 MinecraftServer key) {
                                                                                                 return new Trio<>(key.getServerId(),
                                                                                                                   key.getServerInfo()
                                                                                                                      .getOnlineCount(),
                                                                                                                   key.getServerInfo()
                                                                                                                      .getMaxPlayers());
                                                                                             }
                                                                                         });

        for (Wrapper wrapper : wrappers.values()) {
            for (Map.Entry<String, Quad<Integer, Integer, ServiceId, Template>> serviceId : wrapper.getWaitingServices().entrySet()) {
                if (serviceId.getValue().getThird().getGroup().equalsIgnoreCase(group)) {
                    strings.add(new Trio<>(serviceId.getKey(), 0, 0));
                }
            }
        }
        return strings;
    }

    public Collection<String> getServersAndWaitings() {
        Collection<String> strings = CollectionWrapper.transform(getServers().values(), new Catcher<String, MinecraftServer>() {
            @Override
            public String doCatch(MinecraftServer key) {
                return key.getServerId();
            }
        });

        for (Wrapper wrapper : wrappers.values()) {
            for (Map.Entry<String, Quad<Integer, Integer, ServiceId, Template>> serviceId : wrapper.getWaitingServices().entrySet()) {
                strings.add(serviceId.getKey());
            }
        }
        return strings;
    }

    public Collection<String> getProxysAndWaitings(String group) {
        Collection<String> strings = CollectionWrapper.transform(getProxys(group), new Catcher<String, ProxyServer>() {
            @Override
            public String doCatch(ProxyServer key) {
                return key.getServerId();
            }
        });

        for (Wrapper wrapper : wrappers.values()) {
            for (Quad<Integer, Integer, ServiceId, Template> serviceId : wrapper.getWaitingServices().values()) {
                if (serviceId.getThird().getGroup().equalsIgnoreCase(group)) {
                    strings.add(serviceId.getThird().getServerId());
                }
            }
        }
        return strings;
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

    public java.util.Map<String, ProxyServer> getProxys() {
        java.util.Map<String, ProxyServer> minecraftServerMap = new HashMap<>();

        for (Wrapper wrapper : wrappers.values()) {
            for (ProxyServer minecraftServer : wrapper.getProxys().values()) {
                minecraftServerMap.put(minecraftServer.getServerId(), minecraftServer);
            }
        }

        return minecraftServerMap;
    }

    public CloudServer getCloudGameServer(String serverId) {
        return CollectionWrapper.filter(getCloudGameServers().values(), new Acceptable<CloudServer>() {
            @Override
            public boolean isAccepted(CloudServer cloudServer) {
                return cloudServer.getServerId().equalsIgnoreCase(serverId);
            }
        });
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

    public int calcMemory(int groupMemory, int groupDynmamicMemory, int onlineFromGroup, int globaluse) {
        if (groupMemory < 0 || groupDynmamicMemory < 0) {
            return groupMemory < 0 ? 512 : groupMemory;
        }
        if (groupDynmamicMemory <= groupMemory) {
            return groupMemory;
        }
        if (onlineFromGroup > 9) {
            return groupMemory;
        }
        if (onlineFromGroup == 0) {
            return groupDynmamicMemory;
        }
        return ((groupDynmamicMemory - groupMemory) / 100) * (((10 - onlineFromGroup) * 10)) + groupMemory;
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

    public void startProxy(Wrapper wrapper, ProxyGroup proxyGroup) {
        Collection<Integer> collection = CollectionWrapper.getCollection(getProxys(), new Catcher<Integer, ProxyServer>() {
            @Override
            public Integer doCatch(ProxyServer key) {
                return key.getProxyInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());

        int startport = proxyGroup.getStartPort();
        while (collection.contains(startport)) {
            startport++;
        }

        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper),
                                                                 proxyGroup.getMemory(),
                                                                 startport,
                                                                 new String[] {},
                                                                 null,
                                                                 Arrays.asList(),
                                                                 new Document());
        wrapper.startProxy(proxyProcessMeta);
    }

    public ServiceId newServiceId(ProxyGroup proxyGroup, Wrapper wrapper) {
        int id = 1;
        Collection<ServiceId> serviceIds = getProxysServiceIdsAndWaitings(proxyGroup.getName());
        Collection<Integer> collection = CollectionWrapper.transform(serviceIds, new Catcher<Integer, ServiceId>() {
            @Override
            public Integer doCatch(ServiceId key) {
                return key.getId();
            }
        });
        while (collection.contains(id)) {
            id++;
        }

        return new ServiceId(proxyGroup.getName(),
                             id,
                             UUID.randomUUID(),
                             wrapper.getNetworkInfo().getId(),
                             proxyGroup.getName() + config.getFormatSplitter() + id);
    }

    public Collection<ServiceId> getProxysServiceIdsAndWaitings(String group) {
        Collection<ServiceId> strings = CollectionWrapper.transform(getProxys(group), new Catcher<ServiceId, ProxyServer>() {
            @Override
            public ServiceId doCatch(ProxyServer key) {
                return key.getServiceId();
            }
        });

        for (Wrapper wrapper : wrappers.values()) {
            for (Quad<Integer, Integer, ServiceId, Template> serviceId : wrapper.getWaitingServices().values()) {
                if (serviceId.getThird().getGroup().equalsIgnoreCase(group)) {
                    strings.add(serviceId.getThird());
                }
            }
        }
        return strings;
    }

    public void startProxy(Wrapper wrapper,
                           ProxyGroup proxyGroup,
                           int memory,
                           String[] paramters,
                           String url,
                           Collection<ServerInstallablePlugin> plugins,
                           Document document) {
        if (wrapper == null) {
            return;
        }

        Collection<Integer> collection = CollectionWrapper.getCollection(getProxys(), new Catcher<Integer, ProxyServer>() {
            @Override
            public Integer doCatch(ProxyServer key) {
                return key.getProxyInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        int startport = proxyGroup.getStartPort();
        while (collection.contains(startport)) {
            startport++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper),
                                                                 memory,
                                                                 startport,
                                                                 paramters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxy(proxyProcessMeta);
    }

    public void startProxy(Wrapper wrapper,
                           ProxyGroup proxyGroup,
                           int memory,
                           String[] paramters,
                           String url,
                           Collection<ServerInstallablePlugin> plugins,
                           Document document,
                           int id,
                           UUID uniqueId) {
        if (wrapper == null) {
            return;
        }

        Collection<Integer> collection = CollectionWrapper.getCollection(getProxys(), new Catcher<Integer, ProxyServer>() {
            @Override
            public Integer doCatch(ProxyServer key) {
                return key.getProxyInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        int startport = proxyGroup.getStartPort();
        while (collection.contains(startport)) {
            startport++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper, id, uniqueId),
                                                                 memory,
                                                                 startport,
                                                                 paramters,
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

    public void startProxy(ProxyGroup proxyGroup, int memory) {
        startProxy(proxyGroup, memory, null, Arrays.asList(), new Document());
    }

    public void startProxy(ProxyGroup proxyGroup, int memory, String url, Collection<ServerInstallablePlugin> plugins, Document document) {
        startProxy(proxyGroup, memory, new String[] {}, url, plugins, document);
    }

    public void startProxy(ProxyGroup proxyGroup,
                           int memory,
                           String[] paramters,
                           String url,
                           Collection<ServerInstallablePlugin> plugins,
                           Document document) {
        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(proxyGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        Collection<Integer> collection = CollectionWrapper.getCollection(getProxys(), new Catcher<Integer, ProxyServer>() {
            @Override
            public Integer doCatch(ProxyServer key) {
                return key.getProxyInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        int startport = proxyGroup.getStartPort();
        while (collection.contains(startport)) {
            startport++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper),
                                                                 memory,
                                                                 startport,
                                                                 paramters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxy(proxyProcessMeta);
    }

    public void startProxy(ProxyGroup proxyGroup, Collection<ServerInstallablePlugin> plugins) {
        startProxy(proxyGroup, proxyGroup.getMemory(), null, plugins, new Document());
    }

    public void startProxy(ProxyGroup proxyGroup, int memory, String urlTemplate, Collection<ServerInstallablePlugin> plugins) {
        startProxy(proxyGroup, memory, urlTemplate, plugins);
    }

    public void startProxy(ProxyGroup proxyGroup, String urlTemplate) {
        startProxy(proxyGroup, proxyGroup.getMemory(), urlTemplate, Arrays.asList(), new Document());
    }

    public void startProxy(ProxyGroup proxyGroup, String urlTemplate, Document document) {
        startProxy(proxyGroup, proxyGroup.getMemory(), urlTemplate, Arrays.asList(), document);
    }

    public void startProxy(ProxyGroup proxyGroup, int memory, UUID uniqueId) {
        startProxy(proxyGroup, memory, new String[] {}, null, Arrays.asList(), new Document(), uniqueId);
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

        Collection<Integer> collection = CollectionWrapper.getCollection(getProxys(), new Catcher<Integer, ProxyServer>() {
            @Override
            public Integer doCatch(ProxyServer key) {
                return key.getProxyInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        int startport = proxyGroup.getStartPort();
        while (collection.contains(startport)) {
            startport++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper, uniqueId),
                                                                 memory,
                                                                 startport,
                                                                 parameters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxy(proxyProcessMeta);
    }

    public ServiceId newServiceId(ProxyGroup proxyGroup, Wrapper wrapper, UUID uuid) {
        int id = 1;
        Collection<ServiceId> serviceIds = getProxysServiceIdsAndWaitings(proxyGroup.getName());
        Collection<Integer> collection = CollectionWrapper.transform(serviceIds, new Catcher<Integer, ServiceId>() {
            @Override
            public Integer doCatch(ServiceId key) {
                return key.getId();
            }
        });
        while (collection.contains(id)) {
            id++;
        }

        return new ServiceId(proxyGroup.getName(),
                             id,
                             uuid,
                             wrapper.getNetworkInfo().getId(),
                             proxyGroup.getName() + config.getFormatSplitter() + id);
    }

    public void startProxy(ProxyGroup proxyGroup, int memory, int id, UUID uniqueId) {
        startProxy(proxyGroup, memory, new String[] {}, null, Arrays.asList(), new Document(), id, uniqueId);
    }

    public void startProxy(ProxyGroup proxyGroup,
                           int memory,
                           String[] paramters,
                           String url,
                           Collection<ServerInstallablePlugin> plugins,
                           Document document,
                           int id,
                           UUID uniqueId) {
        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(proxyGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        Collection<Integer> collection = CollectionWrapper.getCollection(getProxys(), new Catcher<Integer, ProxyServer>() {
            @Override
            public Integer doCatch(ProxyServer key) {
                return key.getProxyInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        int startport = proxyGroup.getStartPort();
        while (collection.contains(startport)) {
            startport++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper, id, uniqueId),
                                                                 memory,
                                                                 startport,
                                                                 paramters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxy(proxyProcessMeta);
    }

    public void startProxy(ProxyGroup proxyGroup, int memory, String urlTemplate, int id, UUID uniqueId) {
        startProxy(proxyGroup, memory, new String[] {}, urlTemplate, Arrays.asList(), new Document(), id, uniqueId);
    }

    public void startProxy(ProxyGroup proxyGroup, String url, Collection<ServerInstallablePlugin> collection, int id, UUID uniqueId) {
        startProxy(proxyGroup, proxyGroup.getMemory(), new String[] {}, url, collection, new Document(), id, uniqueId);
    }

    public void startCloudServer(String serverName, int memory, boolean priorityStop) {
        startCloudServer(serverName, new BasicServerConfig(), memory, priorityStop);
    }

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
        Collection<Integer> collection = CollectionWrapper.getCollection(wrapper.getServers(), new Catcher<Integer, MinecraftServer>() {
            @Override
            public Integer doCatch(MinecraftServer key) {
                return key.getServerInfo().getPort();
            }
        });
        int startport = wrapper.getWrapperInfo().getStartPort();
        startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        while (collection.contains(startport)) {
            startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        }
        startCloudServer(wrapper,
                         serverName,
                         serverConfig,
                         memory,
                         priorityStop,
                         processPreParameters,
                         plugins,
                         properties,
                         serverGroupType,
                         startport);
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

    public void startGameServer(ServerGroup serverGroup, ServerConfig serverConfig, Properties serverProperties) {
        startGameServer(serverGroup,
                        serverConfig,
                        calcMemory(serverGroup.getMemory(),
                                   serverGroup.getDynamicMemory(),
                                   getServers(serverGroup.getName()).size(),
                                   (int) globalUsedMemory()),
                        false,
                        null,
                        new String[] {},
                        false,
                        Arrays.asList(),
                        null,
                        serverProperties);
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
                        new String[] {},
                        false,
                        Arrays.asList(),
                        null,
                        serverProperties);
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
                        Arrays.asList(),
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
                        new String[] {},
                        false,
                        Arrays.asList(),
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
                        Arrays.asList(),
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
                        new String[] {},
                        false,
                        Arrays.asList(),
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
                        Arrays.asList(),
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
                        Arrays.asList(),
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
                        new String[] {},
                        false,
                        Arrays.asList(),
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
                        new String[] {},
                        false,
                        Arrays.asList(),
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
                        Arrays.asList(),
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
                        Arrays.asList(),
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
                        new String[] {},
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
                        new String[] {},
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
                        new String[] {},
                        false,
                        Arrays.asList(),
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
                        new String[] {},
                        false,
                        Arrays.asList(),
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
                        new String[] {},
                        false,
                        Arrays.asList(),
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
                        new String[] {},
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
                        new String[] {},
                        false,
                        plugins,
                        null,
                        new Properties());
    }

    public void startGameServer(ServerGroup serverGroup,
                                ServerConfig config,
                                int memory,
                                boolean prioritystop,
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
        Map<String, Integer> templateMap = new WeakHashMap<>();
        Collection<Integer> collection = CollectionWrapper.getCollection(wrapper.getServers(), new Catcher<Integer, MinecraftServer>() {
            @Override
            public Integer doCatch(MinecraftServer key) {
                return key.getServerInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        CollectionWrapper.iterator(getServers(serverGroup.getName()), new Runnabled<MinecraftServer>() {
            @Override
            public void run(MinecraftServer obj) {
                Template template = obj.getProcessMeta().getTemplate();
                if (!templateMap.containsKey(template.getName())) {
                    templateMap.put(template.getName(), 1);
                } else {
                    templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                }
            }
        });

        CollectionWrapper.iterator(wrapper.getWaitingServices().values(), new Runnabled<Quad<Integer, Integer, ServiceId, Template>>() {
            @Override
            public void run(Quad<Integer, Integer, ServiceId, Template> obj) {
                Template template = obj.getFourth();
                if (template != null) {
                    if (!templateMap.containsKey(template.getName())) {
                        templateMap.put(template.getName(), 1);
                    } else {
                        templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                    }
                }
            }
        });

        for (Template template : serverGroup.getTemplates()) {
            if (!templateMap.containsKey(template.getName())) {
                templateMap.put(template.getName(), 1);
            } else {
                templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
            }
        }

        Map.Entry<String, Integer> entry = null;
        for (Map.Entry<String, Integer> values : templateMap.entrySet()) {
            if (entry == null) {
                entry = values;
            } else {
                if (entry.getValue() >= values.getValue()) {
                    entry = values;
                }
            }
        }

        Template template = null;
        for (Template t : serverGroup.getTemplates()) {
            if (entry.getKey().equalsIgnoreCase(t.getName())) {
                template = t;
                break;
            }
        }

        if (template == null) {
            return;
        }
        int startport = wrapper.getWrapperInfo().getStartPort();
        startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        while (collection.contains(startport)) {
            startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        }

        ServerProcessMeta serverProcessMeta;
        if (customServerName != null) {
            serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper, customServerName),
                                                      memory,
                                                      prioritystop,
                                                      url,
                                                      processParameters,
                                                      onlineMode,
                                                      plugins,
                                                      config,
                                                      customServerName,
                                                      startport,
                                                      serverProperties,
                                                      template);
        } else {
            serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper),
                                                      memory,
                                                      prioritystop,
                                                      url,
                                                      processParameters,
                                                      onlineMode,
                                                      plugins,
                                                      config,
                                                      customServerName,
                                                      startport,
                                                      serverProperties,
                                                      template);
        }
        wrapper.startGameServer(serverProcessMeta);
    }

    public void startGameServer(ServerGroup serverGroup,
                                String serverId,
                                ServerConfig config,
                                int memory,
                                boolean prioritystop,
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
        Map<String, Integer> templateMap = new WeakHashMap<>();
        Collection<Integer> collection = CollectionWrapper.getCollection(wrapper.getServers(), new Catcher<Integer, MinecraftServer>() {
            @Override
            public Integer doCatch(MinecraftServer key) {
                return key.getServerInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        CollectionWrapper.iterator(getServers(serverGroup.getName()), new Runnabled<MinecraftServer>() {
            @Override
            public void run(MinecraftServer obj) {
                Template template = obj.getProcessMeta().getTemplate();
                if (!templateMap.containsKey(template.getName())) {
                    templateMap.put(template.getName(), 1);
                } else {
                    templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                }
            }
        });

        CollectionWrapper.iterator(wrapper.getWaitingServices().values(), new Runnabled<Quad<Integer, Integer, ServiceId, Template>>() {
            @Override
            public void run(Quad<Integer, Integer, ServiceId, Template> obj) {
                Template template = obj.getFourth();
                if (template != null) {
                    if (!templateMap.containsKey(template.getName())) {
                        templateMap.put(template.getName(), 1);
                    } else {
                        templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                    }
                }
            }
        });

        for (Template template : serverGroup.getTemplates()) {
            if (!templateMap.containsKey(template.getName())) {
                templateMap.put(template.getName(), 1);
            } else {
                templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
            }
        }

        Map.Entry<String, Integer> entry = null;
        for (Map.Entry<String, Integer> values : templateMap.entrySet()) {
            if (entry == null) {
                entry = values;
            } else {
                if (entry.getValue() >= values.getValue()) {
                    entry = values;
                }
            }
        }

        Template template = null;
        for (Template t : serverGroup.getTemplates()) {
            if (entry.getKey().equalsIgnoreCase(t.getName())) {
                template = t;
                break;
            }
        }

        if (template == null) {
            return;
        }
        int startport = wrapper.getWrapperInfo().getStartPort();
        startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        while (collection.contains(startport)) {
            startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        }

        ServerProcessMeta serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper, serverId),
                                                                    memory,
                                                                    prioritystop,
                                                                    url,
                                                                    processParameters,
                                                                    onlineMode,
                                                                    plugins,
                                                                    config,
                                                                    customServerName,
                                                                    startport,
                                                                    serverProperties,
                                                                    template);
        wrapper.startGameServer(serverProcessMeta);
    }

    public void startGameServer(ServerGroup serverGroup,
                                ServerConfig config,
                                Template template,
                                int memory,
                                boolean prioritystop,
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
        Collection<Integer> collection = CollectionWrapper.getCollection(wrapper.getServers(), new Catcher<Integer, MinecraftServer>() {
            @Override
            public Integer doCatch(MinecraftServer key) {
                return key.getServerInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        if (template == null) {
            return;
        }
        int startport = wrapper.getWrapperInfo().getStartPort();
        startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        while (collection.contains(startport)) {
            startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        }

        ServerProcessMeta serverProcessMeta;
        if (customServerName != null) {
            serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper, customServerName),
                                                      memory,
                                                      prioritystop,
                                                      url,
                                                      processParameters,
                                                      onlineMode,
                                                      plugins,
                                                      config,
                                                      customServerName,
                                                      startport,
                                                      serverProperties,
                                                      template);
        } else {
            serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper),
                                                      memory,
                                                      prioritystop,
                                                      url,
                                                      processParameters,
                                                      onlineMode,
                                                      plugins,
                                                      config,
                                                      customServerName,
                                                      startport,
                                                      serverProperties,
                                                      template);
        }

        wrapper.startGameServer(serverProcessMeta);
    }

    public void startGameServer(Wrapper wrapper,
                                ServerGroup serverGroup,
                                ServerConfig config,
                                Template template,
                                int memory,
                                boolean prioritystop,
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

        if (wrapper == null) {
            return;
        }

        if (serverGroup.getTemplates().size() == 0) {
            return;
        }
        Collection<Integer> collection = CollectionWrapper.getCollection(wrapper.getServers(), new Catcher<Integer, MinecraftServer>() {
            @Override
            public Integer doCatch(MinecraftServer key) {
                return key.getServerInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        if (template == null) {
            return;
        }
        int startport = wrapper.getWrapperInfo().getStartPort();
        startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        while (collection.contains(startport)) {
            startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        }

        ServerProcessMeta serverProcessMeta;
        if (customServerName != null) {
            serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper, customServerName),
                                                      memory,
                                                      prioritystop,
                                                      url,
                                                      processParameters,
                                                      onlineMode,
                                                      plugins,
                                                      config,
                                                      customServerName,
                                                      startport,
                                                      serverProperties,
                                                      template);
        } else {
            serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper),
                                                      memory,
                                                      prioritystop,
                                                      url,
                                                      processParameters,
                                                      onlineMode,
                                                      plugins,
                                                      config,
                                                      customServerName,
                                                      startport,
                                                      serverProperties,
                                                      template);
        }

        wrapper.startGameServer(serverProcessMeta);
    }

    public void startGameServer(Wrapper wrapper,
                                ServerGroup serverGroup,
                                ServerConfig config,
                                int memory,
                                boolean prioritystop,
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

        if (wrapper == null) {
            return;
        }

        if (serverGroup.getTemplates().size() == 0) {
            return;
        }
        Map<String, Integer> templateMap = new WeakHashMap<>();
        Collection<Integer> collection = CollectionWrapper.getCollection(wrapper.getServers(), new Catcher<Integer, MinecraftServer>() {
            @Override
            public Integer doCatch(MinecraftServer key) {
                return key.getServerInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        CollectionWrapper.iterator(getServers(serverGroup.getName()), new Runnabled<MinecraftServer>() {
            @Override
            public void run(MinecraftServer obj) {
                Template template = obj.getProcessMeta().getTemplate();
                if (!templateMap.containsKey(template.getName())) {
                    templateMap.put(template.getName(), 1);
                } else {
                    templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                }
            }
        });

        CollectionWrapper.iterator(wrapper.getWaitingServices().values(), new Runnabled<Quad<Integer, Integer, ServiceId, Template>>() {
            @Override
            public void run(Quad<Integer, Integer, ServiceId, Template> obj) {
                Template template = obj.getFourth();
                if (template != null) {
                    if (!templateMap.containsKey(template.getName())) {
                        templateMap.put(template.getName(), 1);
                    } else {
                        templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                    }
                }
            }
        });

        for (Template template : serverGroup.getTemplates()) {
            if (!templateMap.containsKey(template.getName())) {
                templateMap.put(template.getName(), 1);
            } else {
                templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
            }
        }

        Map.Entry<String, Integer> entry = null;
        for (Map.Entry<String, Integer> values : templateMap.entrySet()) {
            if (entry == null) {
                entry = values;
            } else {
                if (entry.getValue() >= values.getValue()) {
                    entry = values;
                }
            }
        }

        Template template = null;
        for (Template t : serverGroup.getTemplates()) {
            if (entry.getKey().equalsIgnoreCase(t.getName())) {
                template = t;
                break;
            }
        }

        if (template == null) {
            return;
        }
        int startport = wrapper.getWrapperInfo().getStartPort();
        startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        while (collection.contains(startport)) {
            startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        }

        ServerProcessMeta serverProcessMeta;
        if (customServerName != null) {
            serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper, customServerName),
                                                      memory,
                                                      prioritystop,
                                                      url,
                                                      processParameters,
                                                      onlineMode,
                                                      plugins,
                                                      config,
                                                      customServerName,
                                                      startport,
                                                      serverProperties,
                                                      template);
        } else {
            serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper),
                                                      memory,
                                                      prioritystop,
                                                      url,
                                                      processParameters,
                                                      onlineMode,
                                                      plugins,
                                                      config,
                                                      customServerName,
                                                      startport,
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
                                boolean prioritystop,
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

        if (wrapper == null) {
            return;
        }

        if (serverGroup.getTemplates().size() == 0) {
            return;
        }
        Map<String, Integer> templateMap = new WeakHashMap<>();
        Collection<Integer> collection = CollectionWrapper.getCollection(wrapper.getServers(), new Catcher<Integer, MinecraftServer>() {
            @Override
            public Integer doCatch(MinecraftServer key) {
                return key.getServerInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());

        CollectionWrapper.iterator(getServers(serverGroup.getName()), new Runnabled<MinecraftServer>() {
            @Override
            public void run(MinecraftServer obj) {
                Template template = obj.getProcessMeta().getTemplate();
                if (!templateMap.containsKey(template.getName())) {
                    templateMap.put(template.getName(), 1);
                } else {
                    templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                }
            }
        });

        CollectionWrapper.iterator(wrapper.getWaitingServices().values(), new Runnabled<Quad<Integer, Integer, ServiceId, Template>>() {
            @Override
            public void run(Quad<Integer, Integer, ServiceId, Template> obj) {
                Template template = obj.getFourth();
                if (template != null) {
                    if (!templateMap.containsKey(template.getName())) {
                        templateMap.put(template.getName(), 1);
                    } else {
                        templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                    }
                }
            }
        });

        for (Template template : serverGroup.getTemplates()) {
            if (!templateMap.containsKey(template.getName())) {
                templateMap.put(template.getName(), 1);
            } else {
                templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
            }
        }

        Map.Entry<String, Integer> entry = null;
        for (Map.Entry<String, Integer> values : templateMap.entrySet()) {
            if (entry == null) {
                entry = values;
            } else {
                if (entry.getValue() >= values.getValue()) {
                    entry = values;
                }
            }
        }

        Template template = null;
        for (Template t : serverGroup.getTemplates()) {
            if (entry.getKey().equalsIgnoreCase(t.getName())) {
                template = t;
                break;
            }
        }

        if (template == null) {
            return;
        }

        int startport = wrapper.getWrapperInfo().getStartPort();
        startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        while (collection.contains(startport)) {
            startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        }

        List<Template> templates = CollectionWrapper.transform(serverGroup.getTemplates());
        if (templates.size() == 0) {
            return;
        }

        ServerProcessMeta serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper, serverId),
                                                                    memory,
                                                                    prioritystop,
                                                                    url,
                                                                    processParameters,
                                                                    onlineMode,
                                                                    plugins,
                                                                    config,
                                                                    customServerName,
                                                                    startport,
                                                                    serverProperties,
                                                                    template);
        wrapper.startGameServer(serverProcessMeta);
    }

    public void startProxyAsync(ProxyProcessMeta proxyProcessMeta, Wrapper wrapper) {
        wrapper.startProxyAsync(proxyProcessMeta);
    }

    public void startProxyAsync(ProxyGroup proxyGroup) {
        Wrapper wrapper = fetchPerformanceWrapper(proxyGroup.getMemory(), toWrapperInstances(proxyGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        Collection<Integer> collection = CollectionWrapper.getCollection(getProxys(), new Catcher<Integer, ProxyServer>() {
            @Override
            public Integer doCatch(ProxyServer key) {
                return key.getProxyInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        int startport = proxyGroup.getStartPort();
        while (collection.contains(startport)) {
            startport++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper),
                                                                 proxyGroup.getMemory(),
                                                                 startport,
                                                                 new String[] {},
                                                                 null,
                                                                 Arrays.asList(),
                                                                 new Document());
        wrapper.startProxyAsync(proxyProcessMeta);
    }

    public void startProxyAsync(ProxyGroup proxyGroup, int memory) {
        startProxyAsync(proxyGroup, memory, null, Arrays.asList(), new Document());
    }

    public void startProxyAsync(ProxyGroup proxyGroup,
                                int memory,
                                String url,
                                Collection<ServerInstallablePlugin> plugins,
                                Document document) {
        startProxyAsync(proxyGroup, memory, new String[] {}, url, plugins, document);
    }

    public void startProxyAsync(ProxyGroup proxyGroup,
                                int memory,
                                String[] paramters,
                                String url,
                                Collection<ServerInstallablePlugin> plugins,
                                Document document) {
        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(proxyGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        Collection<Integer> collection = CollectionWrapper.getCollection(getProxys(), new Catcher<Integer, ProxyServer>() {
            @Override
            public Integer doCatch(ProxyServer key) {
                return key.getProxyInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        int startport = proxyGroup.getStartPort();
        while (collection.contains(startport)) {
            startport++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper),
                                                                 memory,
                                                                 startport,
                                                                 paramters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxyAsync(proxyProcessMeta);
    }

    public void startProxyAsync(ProxyGroup proxyGroup, Collection<ServerInstallablePlugin> plugins) {
        startProxyAsync(proxyGroup, proxyGroup.getMemory(), null, plugins, new Document());
    }

    public void startProxyAsync(ProxyGroup proxyGroup, String urlTemplate) {
        startProxyAsync(proxyGroup, proxyGroup.getMemory(), urlTemplate, Arrays.asList(), new Document());
    }

    public void startProxyAsync(ProxyGroup proxyGroup, String urlTemplate, Document document) {
        startProxyAsync(proxyGroup, proxyGroup.getMemory(), urlTemplate, Arrays.asList(), document);
    }

    public void startProxyAsync(ProxyGroup proxyGroup, int memory, UUID uniqueId) {
        startProxyAsync(proxyGroup, memory, new String[] {}, null, Arrays.asList(), new Document(), uniqueId);
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

        Collection<Integer> collection = CollectionWrapper.getCollection(getProxys(), new Catcher<Integer, ProxyServer>() {
            @Override
            public Integer doCatch(ProxyServer key) {
                return key.getProxyInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        int startport = proxyGroup.getStartPort();
        while (collection.contains(startport)) {
            startport++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper, uniqueId),
                                                                 memory,
                                                                 startport,
                                                                 parameters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxyAsync(proxyProcessMeta);
    }

    public void startProxyAsync(ProxyGroup proxyGroup, int memory, int id, UUID uniqueId) {
        startProxyAsync(proxyGroup, memory, new String[] {}, null, Arrays.asList(), new Document(), id, uniqueId);
    }

    public void startProxyAsync(ProxyGroup proxyGroup,
                                int memory,
                                String[] paramters,
                                String url,
                                Collection<ServerInstallablePlugin> plugins,
                                Document document,
                                int id,
                                UUID uniqueId) {
        Wrapper wrapper = fetchPerformanceWrapper(memory, toWrapperInstances(proxyGroup.getWrapper()));
        if (wrapper == null) {
            return;
        }

        Collection<Integer> collection = CollectionWrapper.getCollection(getProxys(), new Catcher<Integer, ProxyServer>() {
            @Override
            public Integer doCatch(ProxyServer key) {
                return key.getProxyInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        int startport = proxyGroup.getStartPort();
        while (collection.contains(startport)) {
            startport++;
        }
        ProxyProcessMeta proxyProcessMeta = new ProxyProcessMeta(newServiceId(proxyGroup, wrapper, id, uniqueId),
                                                                 memory,
                                                                 startport,
                                                                 paramters,
                                                                 url,
                                                                 plugins,
                                                                 document);
        wrapper.startProxyAsync(proxyProcessMeta);
    }

    public void startProxyAsync(ProxyGroup proxyGroup, int memory, String urlTemplate, int id, UUID uniqueId) {
        startProxyAsync(proxyGroup, memory, new String[] {}, urlTemplate, Arrays.asList(), new Document(), id, uniqueId);
    }

    public void startProxyAsync(ProxyGroup proxyGroup, String url, Collection<ServerInstallablePlugin> collection, int id, UUID uniqueId) {
        startProxyAsync(proxyGroup, proxyGroup.getMemory(), new String[] {}, url, collection, new Document(), id, uniqueId);
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
                             new String[] {},
                             false,
                             Arrays.asList(),
                             null,
                             serverProperties);
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
                             new String[] {},
                             false,
                             Arrays.asList(),
                             null,
                             serverProperties);
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
                             Arrays.asList(),
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
                             new String[] {},
                             false,
                             Arrays.asList(),
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
                             Arrays.asList(),
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
                             new String[] {},
                             false,
                             Arrays.asList(),
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
                             Arrays.asList(),
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
                             Arrays.asList(),
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
                             new String[] {},
                             false,
                             Arrays.asList(),
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
                             new String[] {},
                             false,
                             Arrays.asList(),
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
                             Arrays.asList(),
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
                             Arrays.asList(),
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
                             new String[] {},
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
                             new String[] {},
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
                             new String[] {},
                             false,
                             Arrays.asList(),
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
                             new String[] {},
                             false,
                             Arrays.asList(),
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
                             new String[] {},
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
                             new String[] {},
                             false,
                             plugins,
                             null,
                             new Properties());
    }

    public void startGameServerAsync(ServerGroup serverGroup,
                                     ServerConfig config,
                                     int memory,
                                     boolean prioritystop,
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
        Map<String, Integer> templateMap = new WeakHashMap<>();
        Collection<Integer> collection = CollectionWrapper.getCollection(wrapper.getServers(), new Catcher<Integer, MinecraftServer>() {
            @Override
            public Integer doCatch(MinecraftServer key) {
                return key.getServerInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        CollectionWrapper.iterator(getServers(serverGroup.getName()), new Runnabled<MinecraftServer>() {
            @Override
            public void run(MinecraftServer obj) {
                Template template = obj.getProcessMeta().getTemplate();
                if (!templateMap.containsKey(template.getName())) {
                    templateMap.put(template.getName(), 1);
                } else {
                    templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                }
            }
        });

        CollectionWrapper.iterator(wrapper.getWaitingServices().values(), new Runnabled<Quad<Integer, Integer, ServiceId, Template>>() {
            @Override
            public void run(Quad<Integer, Integer, ServiceId, Template> obj) {
                Template template = obj.getFourth();
                if (template != null) {
                    if (!templateMap.containsKey(template.getName())) {
                        templateMap.put(template.getName(), 1);
                    } else {
                        templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                    }
                }
            }
        });

        for (Template template : serverGroup.getTemplates()) {
            if (!templateMap.containsKey(template.getName())) {
                templateMap.put(template.getName(), 1);
            } else {
                templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
            }
        }

        Map.Entry<String, Integer> entry = null;
        for (Map.Entry<String, Integer> values : templateMap.entrySet()) {
            if (entry == null) {
                entry = values;
            } else {
                if (entry.getValue() >= values.getValue()) {
                    entry = values;
                }
            }
        }

        Template template = null;
        for (Template t : serverGroup.getTemplates()) {
            if (entry.getKey().equalsIgnoreCase(t.getName())) {
                template = t;
                break;
            }
        }

        if (template == null) {
            return;
        }
        int startport = wrapper.getWrapperInfo().getStartPort();
        startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        while (collection.contains(startport)) {
            startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        }

        ServerProcessMeta serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper),
                                                                    memory,
                                                                    prioritystop,
                                                                    url,
                                                                    processParameters,
                                                                    onlineMode,
                                                                    plugins,
                                                                    config,
                                                                    customServerName,
                                                                    startport,
                                                                    serverProperties,
                                                                    template);
        wrapper.startGameServerAsync(serverProcessMeta);
    }

    public void startGameServerAsync(Wrapper wrapper,
                                     ServerGroup serverGroup,
                                     ServerConfig config,
                                     int memory,
                                     boolean prioritystop,
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
        Map<String, Integer> templateMap = new WeakHashMap<>();
        Collection<Integer> collection = CollectionWrapper.getCollection(wrapper.getServers(), new Catcher<Integer, MinecraftServer>() {
            @Override
            public Integer doCatch(MinecraftServer key) {
                return key.getServerInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        CollectionWrapper.iterator(getServers(serverGroup.getName()), new Runnabled<MinecraftServer>() {
            @Override
            public void run(MinecraftServer obj) {
                Template template = obj.getProcessMeta().getTemplate();
                if (!templateMap.containsKey(template.getName())) {
                    templateMap.put(template.getName(), 1);
                } else {
                    templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                }
            }
        });

        CollectionWrapper.iterator(wrapper.getWaitingServices().values(), new Runnabled<Quad<Integer, Integer, ServiceId, Template>>() {
            @Override
            public void run(Quad<Integer, Integer, ServiceId, Template> obj) {
                Template template = obj.getFourth();
                if (template != null) {
                    if (!templateMap.containsKey(template.getName())) {
                        templateMap.put(template.getName(), 1);
                    } else {
                        templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                    }
                }
            }
        });

        for (Template template : serverGroup.getTemplates()) {
            if (!templateMap.containsKey(template.getName())) {
                templateMap.put(template.getName(), 1);
            } else {
                templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
            }
        }

        Map.Entry<String, Integer> entry = null;
        for (Map.Entry<String, Integer> values : templateMap.entrySet()) {
            if (entry == null) {
                entry = values;
            } else {
                if (entry.getValue() >= values.getValue()) {
                    entry = values;
                }
            }
        }

        Template template = null;
        for (Template t : serverGroup.getTemplates()) {
            if (entry.getKey().equalsIgnoreCase(t.getName())) {
                template = t;
                break;
            }
        }

        if (template == null) {
            return;
        }

        int startport = wrapper.getWrapperInfo().getStartPort();
        startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        while (collection.contains(startport)) {
            startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        }

        List<Template> templates = CollectionWrapper.transform(serverGroup.getTemplates());
        if (templates.size() == 0) {
            return;
        }

        ServerProcessMeta serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper),
                                                                    memory,
                                                                    prioritystop,
                                                                    url,
                                                                    processParameters,
                                                                    onlineMode,
                                                                    plugins,
                                                                    config,
                                                                    customServerName,
                                                                    startport,
                                                                    serverProperties,
                                                                    template);
        wrapper.startGameServerAsync(serverProcessMeta);
    }

    public void startGameServerAsync(Wrapper wrapper,
                                     String serverId,
                                     ServerGroup serverGroup,
                                     ServerConfig config,
                                     int memory,
                                     boolean prioritystop,
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
        Map<String, Integer> templateMap = new WeakHashMap<>();
        Collection<Integer> collection = CollectionWrapper.getCollection(wrapper.getServers(), new Catcher<Integer, MinecraftServer>() {
            @Override
            public Integer doCatch(MinecraftServer key) {
                return key.getServerInfo().getPort();
            }
        });
        collection.addAll(wrapper.getBinndedPorts());
        CollectionWrapper.iterator(getServers(serverGroup.getName()), new Runnabled<MinecraftServer>() {
            @Override
            public void run(MinecraftServer obj) {
                Template template = obj.getProcessMeta().getTemplate();
                if (!templateMap.containsKey(template.getName())) {
                    templateMap.put(template.getName(), 1);
                } else {
                    templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                }
            }
        });

        CollectionWrapper.iterator(wrapper.getWaitingServices().values(), new Runnabled<Quad<Integer, Integer, ServiceId, Template>>() {
            @Override
            public void run(Quad<Integer, Integer, ServiceId, Template> obj) {
                Template template = obj.getFourth();
                if (template != null) {
                    if (!templateMap.containsKey(template.getName())) {
                        templateMap.put(template.getName(), 1);
                    } else {
                        templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
                    }
                }
            }
        });

        for (Template template : serverGroup.getTemplates()) {
            if (!templateMap.containsKey(template.getName())) {
                templateMap.put(template.getName(), 1);
            } else {
                templateMap.put(template.getName(), templateMap.get(template.getName()) + 1);
            }
        }

        Map.Entry<String, Integer> entry = null;
        for (Map.Entry<String, Integer> values : templateMap.entrySet()) {
            if (entry == null) {
                entry = values;
            } else {
                if (entry.getValue() >= values.getValue()) {
                    entry = values;
                }
            }
        }

        Template template = null;
        for (Template t : serverGroup.getTemplates()) {
            if (entry.getKey().equalsIgnoreCase(t.getName())) {
                template = t;
                break;
            }
        }

        if (template == null) {
            return;
        }

        int startport = wrapper.getWrapperInfo().getStartPort();
        startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        while (collection.contains(startport)) {
            startport = (startport + NetworkUtils.RANDOM.nextInt(20) + 1);
        }

        List<Template> templates = CollectionWrapper.transform(serverGroup.getTemplates());
        if (templates.size() == 0) {
            return;
        }

        ServerProcessMeta serverProcessMeta = new ServerProcessMeta(newServiceId(serverGroup, wrapper, serverId),
                                                                    memory,
                                                                    prioritystop,
                                                                    url,
                                                                    processParameters,
                                                                    onlineMode,
                                                                    plugins,
                                                                    config,
                                                                    customServerName,
                                                                    startport,
                                                                    serverProperties,
                                                                    template);
        wrapper.startGameServerAsync(serverProcessMeta);
    }

    public ServiceId newServiceId(ServerGroup serverGroup, Wrapper wrapper, String serverId) {
        int id = 1;
        Collection<ServiceId> serviceIds = getServerServiceIdsAndWaitings(serverGroup.getName());
        Collection<Integer> collection = CollectionWrapper.transform(serviceIds, new Catcher<Integer, ServiceId>() {
            @Override
            public Integer doCatch(ServiceId key) {
                return key.getId();
            }
        });
        while (collection.contains(id)) {
            id++;
        }

        return new ServiceId(serverGroup.getName(), id, UUID.randomUUID(), wrapper.getNetworkInfo().getId(), serverId);
    }
}
