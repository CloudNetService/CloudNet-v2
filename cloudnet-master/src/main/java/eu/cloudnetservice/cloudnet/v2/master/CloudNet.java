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

package eu.cloudnetservice.cloudnet.v2.master;

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.database.DatabaseManager;
import eu.cloudnetservice.cloudnet.v2.event.EventKey;
import eu.cloudnetservice.cloudnet.v2.event.EventManager;
import eu.cloudnetservice.cloudnet.v2.lib.CloudNetwork;
import eu.cloudnetservice.cloudnet.v2.lib.ConnectableAddress;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.hash.DyHash;
import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Executable;
import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Reloadable;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketManager;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyProcessMeta;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerProcessMeta;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.service.ServiceId;
import eu.cloudnetservice.cloudnet.v2.lib.user.User;
import eu.cloudnetservice.cloudnet.v2.logging.CloudLogger;
import eu.cloudnetservice.cloudnet.v2.master.api.event.network.CloudInitEvent;
import eu.cloudnetservice.cloudnet.v2.master.command.*;
import eu.cloudnetservice.cloudnet.v2.master.database.DatabaseBasicHandlers;
import eu.cloudnetservice.cloudnet.v2.master.handler.*;
import eu.cloudnetservice.cloudnet.v2.master.module.CloudModuleManager;
import eu.cloudnetservice.cloudnet.v2.master.network.CloudNetServer;
import eu.cloudnetservice.cloudnet.v2.master.network.NetworkManager;
import eu.cloudnetservice.cloudnet.v2.master.network.components.*;
import eu.cloudnetservice.cloudnet.v2.master.network.components.screen.ScreenProvider;
import eu.cloudnetservice.cloudnet.v2.master.network.packet.api.*;
import eu.cloudnetservice.cloudnet.v2.master.network.packet.api.sync.*;
import eu.cloudnetservice.cloudnet.v2.master.network.packet.dbsync.*;
import eu.cloudnetservice.cloudnet.v2.master.network.packet.in.*;
import eu.cloudnetservice.cloudnet.v2.master.network.packet.out.PacketOutCloudNetwork;
import eu.cloudnetservice.cloudnet.v2.master.process.ProcessStartListener;
import eu.cloudnetservice.cloudnet.v2.master.serverlog.ServerLogManager;
import eu.cloudnetservice.cloudnet.v2.master.util.FileCopy;
import eu.cloudnetservice.cloudnet.v2.master.web.api.v1.*;
import eu.cloudnetservice.cloudnet.v2.master.web.log.WebsiteLog;
import eu.cloudnetservice.cloudnet.v2.master.wrapper.local.LocalCloudWrapper;
import eu.cloudnetservice.cloudnet.v2.web.client.WebClient;
import eu.cloudnetservice.cloudnet.v2.web.server.WebServer;
import joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Because this is an API class, we can and should suppress warnings about
// unused methods and weaker access.
@SuppressWarnings({"unused", "WeakerAccess"})
public final class CloudNet extends EventKey implements Executable, Reloadable {

    public static volatile boolean RUNNING = false;

    private static CloudNet instance;

    private final CommandManager commandManager = new CommandManager();
    private final DatabaseManager databaseManager = new DatabaseManager();
    private final PacketManager packetManager = new PacketManager();
    private final EventManager eventManager = new EventManager();
    private final ScreenProvider screenProvider = new ScreenProvider();
    private final ServerLogManager serverLogManager = new ServerLogManager();
    private final ProcessStartListener processStartListener = new ProcessStartListener();
    private final NetworkManager networkManager = new NetworkManager();
    private final Map<String, Wrapper> wrappers = new ConcurrentHashMap<>();
    private final Map<String, ServerGroup> serverGroups = new ConcurrentHashMap<>();
    private final Map<String, ProxyGroup> proxyGroups = new ConcurrentHashMap<>();
    private final LocalCloudWrapper localCloudWrapper = new LocalCloudWrapper();
    private final Collection<CloudNetServer> cloudServers = new CopyOnWriteArrayList<>();
    private final WebClient webClient = new WebClient();
    private final CloudConfig config;
    private final CloudLogger logger;
    private final OptionSet optionSet;
    private final List<String> arguments;
    private final long startupTime = System.currentTimeMillis();
    private final CloudModuleManager moduleManager;
    private WebServer webServer;
    private DatabaseBasicHandlers dbHandlers;
    private Collection<User> users;

    public CloudNet(CloudConfig config, CloudLogger cloudNetLogging, OptionSet optionSet, List<String> args) {
        if (instance != null) {
            throw new IllegalStateException("CloudNet already initialized!");
        }
        instance = this;

        this.config = config;
        this.logger = cloudNetLogging;
        this.optionSet = optionSet;
        this.arguments = args;

        // We need the reader to stay open
        this.logger.getReader().addCompleter(commandManager);
        this.moduleManager = new CloudModuleManager();
    }

    public static CloudLogger getLogger() {
        return instance.logger;
    }

    public static CloudNet getInstance() {
        return instance;
    }

    public ProcessStartListener getProcessStartListener() {
        return processStartListener;
    }

    @Override
    public boolean bootstrap() {
        if (!optionSet.has("disable-autoupdate")) {
            checkForUpdates();
        }

        dbHandlers = new DatabaseBasicHandlers(databaseManager);
        dbHandlers.getStatisticManager().addStartup();

        this.eventManager.registerListener(this, processStartListener);

        if (!optionSet.has("disable-modules")) {
            System.out.println("Loading Modules...");
            this.moduleManager.detectModules();
        }

        for (WrapperMeta wrapperMeta : config.getWrappers()) {
            System.out.println("Loading Wrapper " + wrapperMeta.getId() + " @ " + wrapperMeta.getHostName());
            this.wrappers.put(wrapperMeta.getId(), new Wrapper(wrapperMeta));
        }

        this.users = config.getUsers();

        this.loadGroups();

        webServer = new WebServer(config.getWebServerConfig().getAddress(), config.getWebServerConfig().getPort());

        this.initialCommands();
        this.initWebHandlers();
        this.initPacketHandlers();

        for (ConnectableAddress connectableAddress : config.getAddresses()) {
            cloudServers.add(new CloudNetServer(connectableAddress));
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
                getExecutor().scheduleWithFixedDelay(cloudPlayerRemoverHandler, 0, 200, TimeUnit.MILLISECONDS);
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
            this.moduleManager.getModules().values().forEach(this.moduleManager::enableModule);
        }

        eventManager.callEvent(new CloudInitEvent());
        this.localCloudWrapper.accept(optionSet);

        return true;
    }

    @Override
    public boolean shutdown() {
        if (!RUNNING) {
            return false;
        }

        getExecutor().shutdownNow();

        for (Wrapper wrapper : wrappers.values()) {
            System.out.println("Disconnecting wrapper " + wrapper.getServerId());
            wrapper.disconnect();
        }

        if (!optionSet.has("disable-modules")) {
            System.out.println("Disabling Modules...");
            this.moduleManager.getModules().values().forEach(this.moduleManager::disableModule);
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
            boolean terminated = getExecutor().awaitTermination(10, TimeUnit.SECONDS);
            if (!terminated) {
                System.err.println("Executor service couldn't be terminated! At least one task seems to still run!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
        return true;
    }

    public void checkForUpdates() {
        if (!config.isAutoUpdate()) {
            return;
        }

        String version = webClient.getLatestVersion();

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

    private void loadGroups() {
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

        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 1, PacketInUpdateServerInfo.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 2, PacketInUpdateProxyInfo.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 3, PacketInCustomChannelMessage.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 4, PacketInStartServer.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 5, PacketInStopServer.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 6, PacketInStartProxy.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 7, PacketInStopProxy.class);
        packetManager.registerHandler(PacketRC.SERVER_HANDLE + 8, PacketInCustomSubChannelMessage.class);
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

    public static ScheduledExecutorService getExecutor() {
        return NetworkUtils.getExecutor();
    }

    public void setupGroup(ServerGroup serverGroup) {
        Path path;
        for (Template template : serverGroup.getTemplates()) {
            path = Paths.get("local", "templates", serverGroup.getName(), template.getName());
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                    Files.createDirectories(path.resolve("plugins"));
                    FileCopy.insertData("files/server.properties", path.resolve("server.properties"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        path = Paths.get("local", "templates", serverGroup.getName(), "globaltemplate");
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                Files.createDirectories(path.resolve("plugins"));
                FileCopy.insertData("files/server.properties", path.resolve("server.properties"));
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
                Files.createDirectories(Paths.get("local", "templates", proxyGroup.getName(), "plugins"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public List<String> getArguments() {
        return arguments;
    }

    public LocalCloudWrapper getLocalCloudWrapper() {
        return localCloudWrapper;
    }

    public Collection<ServiceId> getProxyServiceIdsAndWaitingServices(String group) {
        List<ServiceId> serviceIds = getProxys(group).stream()
                                                     .map(ProxyServer::getServiceId)
                                                     .collect(Collectors.toList());

        wrappers.values().stream()
                .flatMap(wrapper -> wrapper.getWaitingServices().values().stream())
                .map(WaitingService::getServiceId)
                .filter(serviceId -> serviceId.getGroup().equals(group))
                .forEach(serviceIds::add);

        return serviceIds;
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
            for (ProxyServer proxyServer : wrapper.getProxies().values()) {
                minecraftServerMap.put(proxyServer.getServerId(), proxyServer);
            }
        }

        return minecraftServerMap;
    }

    public long getStartupTime() {
        return startupTime;
    }

    @Override
    public void reload() {

        if (!optionSet.has("disable-modules")) {
            System.out.println("Disabling modules...");
            this.moduleManager.getModules().values().forEach(this.moduleManager::disableModule);
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

        this.loadGroups();

        this.initialCommands();
        this.initWebHandlers();
        this.initPacketHandlers();

        if (!optionSet.has("disable-modules")) {
            System.out.println("Load Modules...");
            this.moduleManager.detectModules();
        }

        if (!optionSet.has("disable-modules")) {
            System.out.println("Enabling Modules...");
            this.moduleManager.getModules().values().forEach(this.moduleManager::enableModule);
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

    public Collection<ServiceId> getServerServiceIdsAndWaitings(String group) {
        List<ServiceId> serviceIds = getServers(group).stream()
                                                      .map(MinecraftServer::getServiceId)
                                                      .collect(Collectors.toList());

        wrappers.values().stream()
                .flatMap(wrapper -> wrapper.getWaitingServices().values().stream())
                .map(WaitingService::getServiceId)
                .filter(serviceId -> serviceId.getGroup().equals(group))
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

    public long globalMaxMemory() {
        return wrappers.values().stream()
                       .mapToLong(Wrapper::getMaxMemory)
                       .sum();
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
            for (ProxyServer minecraftServer : wrapper.getProxies().values()) {
                x.add(minecraftServer.getServerId());
            }
        }

        return x;
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
            for (ProxyServer minecraftServer : wrapper.getProxies().values()) {
                if (minecraftServer.getServerId().equals(serverId)) {
                    return minecraftServer;
                }
            }
        }

        return null;
    }

    public CompletableFuture<ProxyProcessMeta> startProxy(ProxyProcessMeta proxyProcessMeta, Wrapper wrapper, UUID uuid) {
        wrapper.startProxy(proxyProcessMeta);
        return this.processStartListener.waitForProxy(uuid)
                                        .whenComplete((processMeta, throwable) -> {
                                            if (throwable != null) {
                                                this.logger.log(Level.WARNING, String.format("Error starting proxy: %n"), throwable);
                                            } else {
                                                this.logger.fine(String.format("Proxy %s started successfully",
                                                                               processMeta.getServiceId().getServerId()));
                                            }
                                        });
    }

    public CompletableFuture<ServerProcessMeta> startServer(ServerProcessMeta serverProcessMeta, Wrapper wrapper, UUID uuid) {
        wrapper.startGameServer(serverProcessMeta);
        return this.processStartListener.waitForServer(uuid)
                                        .whenComplete((processMeta, throwable) -> {
                                            if (throwable != null) {
                                                this.logger.log(Level.WARNING, String.format("Error starting server: %n"), throwable);
                                            } else {
                                                this.logger.fine(String.format("Server %s started successfully",
                                                                               processMeta.getServiceId().getServerId()));
                                            }
                                        });
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

    /**
     * Calculates the amount of running servers for each template.
     * The returned map contains all templates of the given server group.
     *
     * @param serverGroup the server group to retrieve the statistics from
     *
     * @return a map containing a mapping for each template of the given server group to the amount of currently running servers of that template.
     */
    public Map<Template, Integer> getTemplateStatistics(final ServerGroup serverGroup) {
        Map<Template, Integer> templateMap = new HashMap<>();


        getServers(serverGroup.getName()).stream()
                                         .map(MinecraftServer::getProcessMeta)
                                         .map(ServerProcessMeta::getTemplate)
                                         .forEach(template -> templateMap.merge(template, 1, Integer::sum));

        wrappers.entrySet().stream().flatMap(wrapper -> wrapper.getValue().getWaitingServices().values().stream())
                .filter(quad -> quad.getServiceId().getGroup().equals(serverGroup.getName()))
                .map(WaitingService::getTemplate)
                .forEach(template -> templateMap.merge(template, 1, Integer::sum));

        // Set the value to 0 for templates that are not running
        serverGroup.getTemplates().forEach(template -> templateMap.putIfAbsent(template, 0));
        return templateMap;
    }

    public CloudModuleManager getModuleManager() {
        return moduleManager;
    }
}
