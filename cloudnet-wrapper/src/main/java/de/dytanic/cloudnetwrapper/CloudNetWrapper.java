/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper;

import de.dytanic.cloudnet.command.CommandManager;
import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.interfaces.Executable;
import de.dytanic.cloudnet.lib.network.NetDispatcher;
import de.dytanic.cloudnet.lib.network.NetworkConnection;
import de.dytanic.cloudnet.lib.network.auth.Auth;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.scheduler.TaskScheduler;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.user.SimpledUser;
import de.dytanic.cloudnet.lib.utility.threading.Scheduler;
import de.dytanic.cloudnet.logging.CloudLogger;
import de.dytanic.cloudnet.logging.handler.ICloudLoggerHandler;
import de.dytanic.cloudnet.setup.spigot.PaperBuilder;
import de.dytanic.cloudnet.setup.spigot.SetupSpigotVersion;
import de.dytanic.cloudnet.setup.spigot.SpigotBuilder;
import de.dytanic.cloudnet.web.client.WebClient;
import de.dytanic.cloudnetwrapper.command.*;
import de.dytanic.cloudnetwrapper.handlers.IWrapperHandler;
import de.dytanic.cloudnetwrapper.handlers.ReadConsoleLogHandler;
import de.dytanic.cloudnetwrapper.handlers.StopTimeHandler;
import de.dytanic.cloudnetwrapper.network.packet.in.*;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutSetReadyWrapper;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutUpdateCPUUsage;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutUpdateWrapperInfo;
import de.dytanic.cloudnetwrapper.network.packet.out.PacketOutWrapperScreen;
import de.dytanic.cloudnetwrapper.server.BungeeCord;
import de.dytanic.cloudnetwrapper.server.CloudGameServer;
import de.dytanic.cloudnetwrapper.server.GameServer;
import de.dytanic.cloudnetwrapper.server.process.ServerProcessQueue;
import de.dytanic.cloudnetwrapper.util.FileUtility;
import de.dytanic.cloudnetwrapper.util.ShutdownHook;
import de.dytanic.cloudnetwrapper.util.ShutdownOnCentral;
import joptsimple.OptionSet;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public final class CloudNetWrapper implements Executable, ShutdownOnCentral {

    public static volatile boolean RUNNING = false;

    private static CloudNetWrapper instance;

    private final NetworkConnection networkConnection;
    private final CloudLogger cloudNetLogging;
    private final CloudNetWrapperConfig wrapperConfig;
    private final Scheduler scheduler = new Scheduler(40);
    private final CommandManager commandManager = new CommandManager();
    private final WebClient webClient = new WebClient();
    private final java.util.Map<String, GameServer> servers = NetworkUtils.newConcurrentHashMap();
    private final java.util.Map<String, BungeeCord> proxys = NetworkUtils.newConcurrentHashMap();
    private final java.util.Map<String, CloudGameServer> cloudServers = NetworkUtils.newConcurrentHashMap();
    private final java.util.Map<String, ServerGroup> serverGroups = NetworkUtils.newConcurrentHashMap();
    private final java.util.Map<String, ProxyGroup> proxyGroups = NetworkUtils.newConcurrentHashMap();
    private Auth auth;
    private OptionSet optionSet;
    private ServerProcessQueue serverProcessQueue;
    private SimpledUser simpledUser;
    //Sytem meta
    private int maxMemory;
    private boolean canDeployed = false;

    public CloudNetWrapper(OptionSet optionSet, CloudNetWrapperConfig cloudNetWrapperConfig, CloudLogger cloudNetLogging) throws Exception {

        if (instance == null) {
            instance = this;
        }

        this.wrapperConfig = cloudNetWrapperConfig;
        this.cloudNetLogging = cloudNetLogging;
        this.networkConnection = new NetworkConnection(new ConnectableAddress(cloudNetWrapperConfig.getCloudnetHost(),
                                                                              cloudNetWrapperConfig.getCloudnetPort()), new Runnable() {
            @Override
            public void run() {
                try {
                    onShutdownCentral();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        String key = NetworkUtils.readWrapperKey();

        if (key == null) {
            System.out.println("Please copy the WRAPPER_KEY.cnd into the root directory of the CloudNet-Wrapper for authentication!");
            System.out.println("The Wrapper stops in 5 seconds");
            NetworkUtils.sleepUninterruptedly(2000);
            System.exit(0);
            return;
        }

        this.auth = new Auth(key, cloudNetWrapperConfig.getWrapperId());
        this.serverProcessQueue = new ServerProcessQueue(cloudNetWrapperConfig.getProcessQueueSize());
        this.maxMemory = cloudNetWrapperConfig.getMaxMemory();
        this.optionSet = optionSet;
    }

    @Override
    public void onShutdownCentral() throws Exception {

        if (SpigotBuilder.getExec() != null) {
            SpigotBuilder.getExec().destroyForcibly();
        }
        if (PaperBuilder.getExec() != null) {
            PaperBuilder.getExec().destroyForcibly();
        }
        canDeployed = false;
        if (serverProcessQueue != null) {
            serverProcessQueue.getProxys().clear();
            serverProcessQueue.getServers().clear();
            serverProcessQueue.setRunning(false);
        }

        for (GameServer gameServer : servers.values()) {
            gameServer.shutdown();
        }

        for (BungeeCord gameServer : proxys.values()) {
            gameServer.shutdown();
        }

        proxyGroups.clear();
        serverGroups.clear();

        System.out.println("Wrapper try to connect to the CloudNet-Master");
        FileUtility.deleteDirectory(new File("temp"));

        new File("temp").mkdir();

        while (networkConnection.getChannel() == null) {
            networkConnection.tryConnect(optionSet.has("ssl"), new NetDispatcher(networkConnection, false), auth);
            if (networkConnection.getChannel() != null) {
                networkConnection.sendPacketSynchronized(new PacketOutUpdateWrapperInfo());
                break;
            }
            Thread.sleep(2000);
        }

        canDeployed = true;
        if (serverProcessQueue != null) {
            serverProcessQueue.setRunning(true);
        }

    }

    public static CloudNetWrapper getInstance() {
        return CloudNetWrapper.instance;
    }

    @Override
    public boolean bootstrap() throws Exception {

        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(this)));
        if (!optionSet.has("disable-autoupdate")) {
            checkForUpdates();
        }

        if (!optionSet.has("disallow_bukkit_download") && !Files.exists(Paths.get("local/spigot.jar"))) {
            new SetupSpigotVersion().accept(cloudNetLogging.getReader());
        }

        Thread thread = new Thread(scheduler);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(true);
        thread.start();

        Thread processQueueThread = new Thread(serverProcessQueue);
        processQueueThread.setPriority(Thread.MIN_PRIORITY);
        processQueueThread.setDaemon(true);
        processQueueThread.start();

        commandManager.registerCommand(new CommandHelp())
                      .registerCommand(new CommandClear())
                      .registerCommand(new CommandVersion())
                      .registerCommand(new CommandClearCache())
                      .registerCommand(new CommandStop())
                      .registerCommand(new CommandReload());

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
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 13, PacketInStartCloudServer.class);
        networkConnection.getPacketManager().registerHandler(PacketRC.CN_CORE + 14, PacketInCopyDirectory.class);

        networkConnection.getPacketManager().registerHandler(PacketRC.TEST + 1, PacketInTestResult.class);

        System.out.println("Trying to connect " + networkConnection.getConnectableAddress()
                                                                   .getHostName() + ':' + networkConnection.getConnectableAddress()
                                                                                                           .getPort());
        while (networkConnection.getConnectionTrys() < 5 && networkConnection.getChannel() == null) {
            networkConnection.tryConnect(optionSet.has("ssl"), new NetDispatcher(networkConnection, false), auth);
            if (networkConnection.getChannel() != null) {
                networkConnection.sendPacketSynchronized(new PacketOutUpdateWrapperInfo());
                break;
            }
            Thread.sleep(2000);

            if (networkConnection.getConnectionTrys() == 5) {
                System.exit(0);
            }
        }

        if (!Files.exists(Paths.get("local/server-icon.png"))) {
            FileUtility.insertData("files/server-icon.png", "local/server-icon.png");
        }

        //Server Handlers
        {
            networkConnection.sendPacket(new PacketOutSetReadyWrapper(true));
            IWrapperHandler iWrapperHandler = new StopTimeHandler(), readConsoleLogWrapperHandler = new ReadConsoleLogHandler();

            scheduler.runTaskRepeatSync(iWrapperHandler.toExecutor(), 0, iWrapperHandler.getTicks());
            scheduler.runTaskRepeatSync(readConsoleLogWrapperHandler.toExecutor(), 0, readConsoleLogWrapperHandler.getTicks());

            scheduler.runTaskRepeatAsync(new Runnable() {
                @Override
                public void run() {
                    networkConnection.sendPacket(new PacketOutUpdateCPUUsage(getCpuUsage()));
                }
            }, 0, 200);
        }

        cloudNetLogging.getHandler().add(new ICloudLoggerHandler() {
            @Override
            public void handleConsole(String input) {
                if (networkConnection.isConnected()) {
                    networkConnection.sendPacket(new PacketOutWrapperScreen(input));
                }
            }
        });

        canDeployed = true;
        RUNNING = true;

        return true;
    }

    public void checkForUpdates() {
        if (!wrapperConfig.isAutoUpdate()) {
            return;
        }

        String version = webClient.getNewstVersion();

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

    public double getCpuUsage() {
        return NetworkUtils.cpuUsage();
    }

    @Override
    public boolean shutdown() {

        if (SpigotBuilder.getExec() != null) {
            SpigotBuilder.getExec().destroyForcibly();
        }
        if (PaperBuilder.getExec() != null) {
            PaperBuilder.getExec().destroyForcibly();
        }
        if (!RUNNING) {
            return false;
        }
        System.out.println("Wrapper shutdown...");
        TaskScheduler.runtimeScheduler().shutdown();

        scheduler.cancelAllTasks();

        if (serverProcessQueue != null) {
            serverProcessQueue.setRunning(false);
        }

        for (GameServer gameServer : servers.values()) {
            gameServer.shutdown();
        }

        for (BungeeCord gameServer : proxys.values()) {
            gameServer.shutdown();
        }

        for (CloudGameServer gameServer : cloudServers.values()) {
            gameServer.shutdown();
        }

        this.cloudNetLogging.shutdownAll();
        if (networkConnection.getChannel() != null) {
            networkConnection.tryDisconnect();
        }

        FileUtility.deleteDirectory(new File("temp"));

        System.out.println("\n    _  _     _______   _                       _          \n" + "  _| || |_  |__   __| | |                     | |         \n" + " |_  __  _|    | |    | |__     __ _   _ __   | | __  ___ \n" + "  _| || |_     | |    | '_ \\   / _` | | '_ \\  | |/ / / __|\n" + " |_  __  _|    | |    | | | | | (_| | | | | | |   <  \\__ \\\n" + "   |_||_|      |_|    |_| |_|  \\__,_| |_| |_| |_|\\_\\ |___/\n" + "                                                          \n" + "                                                          ");
        RUNNING = false;
        System.exit(0);
        return true;
    }

    public int getUsedMemory() {
        int m = 0;
        for (GameServer gameServer : servers.values()) {
            m = m + gameServer.getServerProcess().getMeta().getMemory();
        }

        for (BungeeCord bungeeCord : proxys.values()) {
            m = m + bungeeCord.getProxyProcessMeta().getMemory();
        }

        return m;
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

    public Scheduler getScheduler() {
        return this.scheduler;
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

    public void setServerProcessQueue(ServerProcessQueue serverProcessQueue) {
        this.serverProcessQueue = serverProcessQueue;
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

    public Map<String, BungeeCord> getProxys() {
        return this.proxys;
    }

    public Map<String, CloudGameServer> getCloudServers() {
        return this.cloudServers;
    }

    public Map<String, ServerGroup> getServerGroups() {
        return this.serverGroups;
    }

    public Map<String, ProxyGroup> getProxyGroups() {
        return this.proxyGroups;
    }

    public boolean isCanDeployed() {
        return this.canDeployed;
    }
}
