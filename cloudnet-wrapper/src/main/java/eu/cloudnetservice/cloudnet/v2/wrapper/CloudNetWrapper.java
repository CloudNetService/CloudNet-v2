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

package eu.cloudnetservice.cloudnet.v2.wrapper;

import eu.cloudnetservice.cloudnet.v2.command.CommandManager;
import eu.cloudnetservice.cloudnet.v2.console.ConsoleManager;
import eu.cloudnetservice.cloudnet.v2.console.ConsoleRegistry;
import eu.cloudnetservice.cloudnet.v2.console.SignalManager;
import eu.cloudnetservice.cloudnet.v2.lib.ConnectableAddress;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Executable;
import eu.cloudnetservice.cloudnet.v2.lib.network.NetDispatcher;
import eu.cloudnetservice.cloudnet.v2.lib.network.NetworkConnection;
import eu.cloudnetservice.cloudnet.v2.lib.network.auth.Auth;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroup;
import eu.cloudnetservice.cloudnet.v2.lib.user.SimpledUser;
import eu.cloudnetservice.cloudnet.v2.logging.CloudLogger;
import eu.cloudnetservice.cloudnet.v2.web.client.WebClient;
import eu.cloudnetservice.cloudnet.v2.wrapper.command.*;
import eu.cloudnetservice.cloudnet.v2.wrapper.handlers.IWrapperHandler;
import eu.cloudnetservice.cloudnet.v2.wrapper.handlers.ReadConsoleLogHandler;
import eu.cloudnetservice.cloudnet.v2.wrapper.handlers.StopTimeHandler;
import eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.in.*;
import eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.out.PacketOutSetReadyWrapper;
import eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.out.PacketOutUpdateCPUUsage;
import eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.out.PacketOutUpdateWrapperInfo;
import eu.cloudnetservice.cloudnet.v2.wrapper.server.BungeeCord;
import eu.cloudnetservice.cloudnet.v2.wrapper.server.GameServer;
import eu.cloudnetservice.cloudnet.v2.wrapper.server.process.ServerProcessQueue;
import eu.cloudnetservice.cloudnet.v2.wrapper.setup.spigot.PaperBuilder;
import eu.cloudnetservice.cloudnet.v2.wrapper.setup.spigot.SpigotBuilder;
import eu.cloudnetservice.cloudnet.v2.wrapper.util.FileUtility;
import eu.cloudnetservice.cloudnet.v2.wrapper.util.ShutdownHook;
import eu.cloudnetservice.cloudnet.v2.wrapper.util.ShutdownOnCentral;
import joptsimple.OptionSet;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class CloudNetWrapper implements Executable, ShutdownOnCentral {

    public static volatile boolean RUNNING = false;

    private static CloudNetWrapper instance;

    private NetworkConnection networkConnection;
    private final CloudLogger cloudNetLogging;
    private final CloudNetWrapperConfig wrapperConfig;
    private final CommandManager commandManager;
    private final ConsoleManager consoleManager;
    private final WebClient webClient = new WebClient();
    private final Map<String, GameServer> servers = new ConcurrentHashMap<>();
    private final Map<String, BungeeCord> proxies = new ConcurrentHashMap<>();
    private final Map<String, ServerGroup> serverGroups = new ConcurrentHashMap<>();
    private final Map<String, ProxyGroup> proxyGroups = new ConcurrentHashMap<>();
    private Auth auth;
    private OptionSet optionSet;
    private ServerProcessQueue serverProcessQueue;
    private SimpledUser simpledUser;
    private int maxMemory;

    public CloudNetWrapper(OptionSet optionSet, CloudNetWrapperConfig cloudNetWrapperConfig, CloudLogger cloudNetLogging, ConsoleManager consoleManager) {

        if (instance == null) {
            instance = this;
        }

        this.wrapperConfig = cloudNetWrapperConfig;
        this.cloudNetLogging = cloudNetLogging;
        this.consoleManager = consoleManager;
        this.commandManager = new CommandManager(consoleManager, commandManager1 -> {
            getConsoleManager().setPrompt(System.getProperty("user.name") + "@" + wrapperConfig.getWrapperId() + " $ ");
        });
        this.optionSet = optionSet;
    }

    @Override
    public void onShutdownCentral() throws Exception {

        shutdownProcesses();

        proxyGroups.clear();
        serverGroups.clear();

        System.out.println("Wrapper try to connect to the CloudNet-Master");
        FileUtility.deleteDirectory(new File("temp"));
        Files.createDirectories(Paths.get("temp"));

        while (networkConnection.getChannel() == null && RUNNING) {
            networkConnection.tryConnect(new NetDispatcher(networkConnection, false), auth);
            if (networkConnection.getChannel() != null) {
                networkConnection.sendPacketSynchronized(new PacketOutUpdateWrapperInfo());
                break;
            }
            //noinspection BusyWait
            Thread.sleep(2000);
        }

        if (serverProcessQueue != null) {
            serverProcessQueue.setRunning(RUNNING);
        }

    }

    private void shutdownProcesses() {
        if (SpigotBuilder.getExec() != null) {
            SpigotBuilder.getExec().destroyForcibly();
        }
        if (PaperBuilder.getExec() != null) {
            PaperBuilder.getExec().destroyForcibly();
        }
        if (serverProcessQueue != null) {
            serverProcessQueue.setRunning(false);
            serverProcessQueue.getProxies().clear();
            serverProcessQueue.getServers().clear();
        }

        for (GameServer gameServer : servers.values()) {
            gameServer.shutdown();
        }

        for (BungeeCord gameServer : proxies.values()) {
            gameServer.shutdown();
        }
    }

    public static CloudNetWrapper getInstance() {
        return CloudNetWrapper.instance;
    }

    @Override
    public boolean bootstrap() {



        commandManager.registerCommand(new CommandHelp())
                      .registerCommand(new CommandClear())
                      .registerCommand(new CommandVersion())
                      .registerCommand(new CommandClearCache())
                      .registerCommand(new CommandStop())
                      .registerCommand(new CommandReload())
                      .registerCommand(new CommandSetup(this));



        if (!optionSet.has("disallow_bukkit_download") && !Files.exists(Paths.get("local/spigot.jar"))) {
            System.out.println("No spigot.jar has been found!");
            this.cloudNetLogging.log(Level.INFO, "Please use the command \"setup\" to install the spigot.jar");
        }
        this.networkConnection = new NetworkConnection(
            new ConnectableAddress(
                wrapperConfig.getCloudNetHost(),
                wrapperConfig.getCloudNetPort()),
            new ConnectableAddress(wrapperConfig.getInternalIP(), 0));

        String key = NetworkUtils.readWrapperKey();

        if (key == null) {
            System.out.println("Please copy the WRAPPER_KEY.cnd into the root directory of the CloudNet-Wrapper for authentication!");
            System.out.println("The Wrapper stops in 5 seconds");
            NetworkUtils.sleepUninterruptedly(2000);
            System.exit(0);
        }

        this.auth = new Auth(key, wrapperConfig.getWrapperId());
        this.serverProcessQueue = new ServerProcessQueue(wrapperConfig.getProcessQueueSize());
        this.maxMemory = wrapperConfig.getMaxMemory();
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(this)));
        if (!optionSet.has("disable-autoupdate")) {
            checkForUpdates();
        }


        NetworkUtils.getExecutor().scheduleWithFixedDelay(serverProcessQueue, 500, 500, TimeUnit.MILLISECONDS);


        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE, PacketInWrapperInfo.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 1, PacketInStartProxy.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 2, PacketInStopProxy.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 3, PacketInStartServer.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 4, PacketInStopServer.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 5, PacketInCreateTemplate.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 6, PacketInScreen.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 7, PacketInExecuteServerCommand.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 8, PacketInInstallUpdate.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 9, PacketInExecuteCommand.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 10, PacketInCopyServer.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 11, PacketInOnlineServer.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 12, PacketInUpdateWrapperProperties.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 14, PacketInCopyDirectory.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 15, PacketInConsoleSettings.class);

        System.out.println(String.format("Trying to connect %s:%d",
                          networkConnection.getConnectableAddress().getHostName(),
                          networkConnection.getConnectableAddress().getPort()));

        NetworkUtils.getExecutor().execute(() -> {
            while (networkConnection.getConnectionTries() < 5 && networkConnection.getChannel() == null) {
                networkConnection.tryConnect(new NetDispatcher(networkConnection, false), auth);
                if (networkConnection.getChannel() != null) {
                    networkConnection.sendPacketSynchronized(new PacketOutUpdateWrapperInfo());
                    break;
                }
                try {
                    wait(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }

                if (networkConnection.getConnectionTries() == 5) {
                    System.exit(0);
                }
            }
        });
        if (!Files.exists(Paths.get("local/server-icon.png"))) {
            FileUtility.insertData("files/server-icon.png", "local/server-icon.png");
        }

        //Server Handlers
        networkConnection.sendPacket(new PacketOutSetReadyWrapper(true));
        IWrapperHandler iWrapperHandler = new StopTimeHandler();
        IWrapperHandler readConsoleLogWrapperHandler = new ReadConsoleLogHandler();

        NetworkUtils.getExecutor().scheduleWithFixedDelay(iWrapperHandler.toExecutor(), 0, 250, TimeUnit.MILLISECONDS);
        NetworkUtils.getExecutor().scheduleWithFixedDelay(readConsoleLogWrapperHandler.toExecutor(), 0, 1, TimeUnit.SECONDS);

        NetworkUtils.getExecutor().scheduleWithFixedDelay(
            () -> networkConnection.sendPacket(new PacketOutUpdateCPUUsage(NetworkUtils.cpuUsage())), 0, 5, TimeUnit.SECONDS);

        RUNNING = true;

        return true;
    }

    public void checkForUpdates() {
        if (!wrapperConfig.isAutoUpdate()) {
            return;
        }

        String version = webClient.getLatestVersion();

        if (version != null) {
            if (!version.equals(CloudNetWrapper.class.getPackage().getImplementationVersion())) {
                System.out.println("Preparing update...");
                webClient.update(version);
                shutdown();

            } else {
                System.out.println("No updates found!");
            }
        } else {
            System.out.println("Failed to check for updates");
        }

    }

    @Override
    public boolean shutdown() {
        if (!RUNNING) {
            return false;
        } else {
            RUNNING = false;
        }
        System.out.println("Wrapper shutdown...");

        networkConnection.tryDisconnect();
        shutdownProcesses();
        NetworkUtils.getExecutor().shutdownNow();

        FileUtility.deleteDirectory(new File("temp"));
        this.cloudNetLogging.info("    _  _     _______   _                       _          ");
        this.cloudNetLogging.info("  _| || |_  |__   __| | |                     | |         ");
        this.cloudNetLogging.info(" |_  __  _|    | |    | |__     __ _   _ __   | | __  ___ ");
        this.cloudNetLogging.info("  _| || |_     | |    | '_ \\   / _` | | '_ \\  | |/ / / __|");
        this.cloudNetLogging.info(" |_  __  _|    | |    | | | | | (_| | | | | | |   <  \\__ \\");
        this.cloudNetLogging.info("   |_||_|      |_|    |_| |_|  \\__,_| |_| |_| |_|\\_\\ |___/");

        this.cloudNetLogging.shutdownAll();
        try {
            NetworkUtils.getExecutor().awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        System.exit(1);
        return true;
    }

    public int getUsedMemory() {
        int memory = 0;
        for (GameServer gameServer : servers.values()) {
            memory += gameServer.getServerProcessMeta().getMemory();
        }

        for (BungeeCord bungeeCord : proxies.values()) {
            memory += bungeeCord.getProxyProcessMeta().getMemory();
        }

        return memory;
    }

    public NetworkConnection getNetworkConnection() {
        return this.networkConnection;
    }

    public CloudLogger getCloudNetLogging() {
        return this.cloudNetLogging;
    }

    public CloudNetWrapperConfig getWrapperConfig() {
        return this.wrapperConfig;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public WebClient getWebClient() {
        return this.webClient;
    }

    public Auth getAuth() {
        return this.auth;
    }

    public OptionSet getOptionSet() {
        return this.optionSet;
    }

    public ServerProcessQueue getServerProcessQueue() {
        return this.serverProcessQueue;
    }

    public SimpledUser getSimpledUser() {
        return this.simpledUser;
    }

    public void setSimpledUser(SimpledUser simpledUser) {
        this.simpledUser = simpledUser;
    }

    public int getMaxMemory() {
        return this.maxMemory;
    }

    public void setMaxMemory(int maxMemory) {
        this.maxMemory = maxMemory;
    }

    public Map<String, GameServer> getServers() {
        return this.servers;
    }

    public Map<String, BungeeCord> getProxies() {
        return this.proxies;
    }

    public Map<String, ServerGroup> getServerGroups() {
        return this.serverGroups;
    }

    public Map<String, ProxyGroup> getProxyGroups() {
        return this.proxyGroups;
    }

    public ConsoleManager getConsoleManager() {
        return consoleManager;
    }
}
