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

package eu.cloudnetservice.cloudnet.v2.bridge;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.api.CloudService;
import eu.cloudnetservice.cloudnet.v2.api.builders.ApiServerProcessBuilder;
import eu.cloudnetservice.cloudnet.v2.api.handlers.NetworkHandler;
import eu.cloudnetservice.cloudnet.v2.bridge.event.bukkit.*;
import eu.cloudnetservice.cloudnet.v2.bridge.internal.util.ReflectionUtil;
import eu.cloudnetservice.cloudnet.v2.lib.CloudNetwork;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.cloudnet.v2.lib.player.OfflinePlayer;
import eu.cloudnetservice.cloudnet.v2.lib.server.*;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.server.template.Template;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Cloud-Server represents
 */
@SuppressWarnings("unused")
public class CloudServer implements CloudService, NetworkHandler {

    private static CloudServer instance;

    private final BukkitBootstrap bukkitBootstrap;

    private final Map<UUID, CloudPlayer> cloudPlayers = new ConcurrentHashMap<>();
    private final InetAddress hostAddress;
    private final int port;
    private final Template template;
    private final int memory;
    private final CloudAPI cloudAPI;

    /**
     * A map of all proxies which are requested to be launched.
     */
    private final Map<UUID, CompletableFuture<ProxyProcessMeta>> waitingProxies = new ConcurrentHashMap<>();

    /**
     * A map of all servers which are requested to be launched.
     */
    private final Map<UUID, CompletableFuture<ServerProcessMeta>> waitingServers = new ConcurrentHashMap<>();

    private int maxPlayers;
    private String motd;
    private ServerState serverState;
    private ServerConfig serverConfig;
    private boolean allowAutoStart = true;

    public CloudServer(BukkitBootstrap bukkitBootstrap, CloudAPI cloudAPI) {
        if (instance != null) {
            throw new IllegalStateException("CloudServer already initialized, use the instance!");
        }
        instance = this;
        cloudAPI.setCloudService(this);

        this.bukkitBootstrap = bukkitBootstrap;
        ServerInfo serverInfo = cloudAPI.getConfig().getObject("serverInfo", ServerInfo.TYPE);

        cloudAPI.getNetworkHandlerProvider().registerHandler(this);
        this.allowAutoStart = !cloudAPI.getConfig().contains("cloudProcess");
        this.maxPlayers = serverInfo.getMaxPlayers();
        this.motd = serverInfo.getMotd();
        this.hostAddress = serverInfo.getHost();
        this.port = serverInfo.getPort();
        this.serverConfig = serverInfo.getServerConfig();
        this.memory = serverInfo.getMemory();
        this.template = serverInfo.getTemplate();
        this.serverState = ServerState.LOBBY;
        this.cloudAPI = cloudAPI;
    }

    /**
     * Returns the instance from the CloudServer
     *
     * @return
     */
    public static CloudServer getInstance() {
        return instance;
    }

    public void updateDisable() {
        List<String> list = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());

        new ServerInfo(this.cloudAPI.getServiceId(),
                       hostAddress,
                       port,
                       false,
                       list,
                       memory,
                       motd,
                       maxPlayers,
                       serverState,
                       serverConfig,
                       template)
            .fetch(this.cloudAPI::update);
    }

    /**
     * Updates the ServerInfo on a asynchronized BukkitScheduler Task
     */
    public void updateAsync() {
        bukkitBootstrap.getServer().getScheduler().runTaskAsynchronously(bukkitBootstrap, () -> {
            List<String> list = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());

            new ServerInfo(this.cloudAPI.getServiceId(),
                           hostAddress,
                           port,
                           true,
                           list,
                           memory,
                           motd,
                           maxPlayers,
                           serverState,
                           serverConfig,
                           template)
                .fetch(this.cloudAPI::update);
        });
    }

    /**
     * Changed the State to INGAME and Start a gameserver
     */
    public void changeToIngame() {
        serverState = ServerState.INGAME;

        if (allowAutoStart) {
            ApiServerProcessBuilder.create(this.cloudAPI.getGroup())
                                   .template(template)
                                   .startServer();
            allowAutoStart = false;

            Bukkit.getScheduler().runTaskLater(bukkitBootstrap, () -> allowAutoStart = true, 6000);
        }

        update();
    }

    /**
     * Updates the ServerInfo
     */
    public void update() {
        List<String> list = Bukkit.getOnlinePlayers().stream()
                                  .map(HumanEntity::getName)
                                  .collect(Collectors.toList());

        new ServerInfo(this.cloudAPI.getServiceId(),
                       hostAddress,
                       port,
                       true,
                       list,
                       memory,
                       motd,
                       maxPlayers,
                       serverState,
                       serverConfig,
                       template)
            .fetch(this.cloudAPI::update);
    }

    /**
     * Checks if this instance can starting game servers auto
     *
     * @return
     */
    public boolean isAllowAutoStart() {
        return allowAutoStart;
    }

    /**
     * You can disable the Autostart funtction from this server
     *
     * @param allowAutoStart
     */
    public void setAllowAutoStart(boolean allowAutoStart) {
        this.allowAutoStart = allowAutoStart;
    }

    public void setServerStateAndUpdate(ServerState serverStateAndUpdate) {
        this.serverState = serverStateAndUpdate;
        update();
    }

    public int getPort() {
        return port;
    }

    public InetAddress getHostAddress() {
        return hostAddress;
    }

    /**
     * Returns the serverConfig from this instance
     *
     * @return
     */
    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    /**
     * Sets the serverConfig in a new default style
     *
     * @param serverConfig
     */
    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    /**
     * Returns the ServerState from this instance
     *
     * @return
     */
    public ServerState getServerState() {
        return serverState;
    }

    /**
     * Set the serverState INGAME, LOBBY, OFFLINE for switching Signs or your API thinks
     *
     * @param serverState
     */
    public void setServerState(ServerState serverState) {
        this.serverState = serverState;
    }

    /**
     * Returns the max players from the acceptings
     *
     * @return
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Set the maxPlayers from this instance
     *
     * @param maxPlayers
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMaxPlayersAndUpdate(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        update();
    }

    public void setMotdAndUpdate(String motd) {
        this.motd = motd;
        update();
    }

    /**
     * Returns the motd from the server marks for the cloud
     *
     * @return
     */
    public String getMotd() {
        return motd;
    }

    /**
     * Sets the Motd for the ServerInfo
     *
     * @param motd
     */
    public void setMotd(String motd) {
        this.motd = motd;
    }

    /**
     * Returns the Template of the ServerInfo
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * Registerd one command
     *
     * @param command
     */
    public void registerCommand(Command command) {
        try {
            Class<?> clazz = ReflectionUtil.reflectCraftClazz(".CraftServer");
            CommandMap commandMap;
            if (clazz != null) {
                commandMap = (CommandMap) clazz.getMethod("getCommandMap").invoke(Bukkit.getServer());
            } else {
                commandMap = (CommandMap) Class.forName("net.glowstone.GlowServer").getMethod("getCommandMap").invoke(Bukkit.getServer());
            }
            commandMap.register("cloudnet", command);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the SimpleServerGroup of the instance
     *
     * @return
     */
    public SimpleServerGroup getGroupData() {
        return this.cloudAPI.getCloudNetwork().getServerGroups().get(this.cloudAPI.getGroup());
    }

    public double getPercentOfPlayerNowOnline() {
        return (((double) Bukkit.getOnlinePlayers().size()) / (double) maxPlayers) * 100;
    }

    /**
     * Returns the ServerProcessMeta for the bootstrap of the software
     *
     * @return
     */
    public ServerProcessMeta getServerProcessMeta() {
        return this.cloudAPI.getConfig().getObject("serverProcess", ServerProcessMeta.TYPE);
    }

    private void initScoreboard(Player all) {
        if (all.getScoreboard() == null) {
            all.setScoreboard(all.getServer().getScoreboardManager().getNewScoreboard());
        }
    }

    public CloudPlayer getCachedPlayer(UUID uniqueId) {
        return cloudPlayers.get(uniqueId);
    }

    public CloudPlayer getCachedPlayer(String name) {
        for (final CloudPlayer player : cloudPlayers.values()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public boolean isProxyInstance() {
        return false;
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<ProxyProcessMeta> waitForProxy(final UUID uuid) {
        final CompletableFuture<ProxyProcessMeta> future = new CompletableFuture<>();
        this.waitingProxies.put(uuid, future);
        return future;
    }

    @Override
    public CompletableFuture<ServerProcessMeta> waitForServer(final UUID uuid) {
        final CompletableFuture<ServerProcessMeta> future = new CompletableFuture<>();
        this.waitingServers.put(uuid, future);
        return future;
    }

    /**
     * Returns the cached CloudPlayer Objectives
     *
     * @return
     */
    public Map<UUID, CloudPlayer> getCloudPlayers() {
        return cloudPlayers;
    }

    @Override
    public void onServerAdd(ServerInfo serverInfo) {
        Bukkit.getPluginManager().callEvent(new BukkitServerAddEvent(serverInfo));
    }

    @Override
    public void onServerInfoUpdate(ServerInfo serverInfo) {
        Bukkit.getPluginManager().callEvent(new BukkitServerInfoUpdateEvent(serverInfo));
    }

    @Override
    public void onServerRemove(ServerInfo serverInfo) {
        Bukkit.getPluginManager().callEvent(new BukkitServerRemoveEvent(serverInfo));
    }

    @Override
    public void onProxyAdd(ProxyInfo proxyInfo) {
        Bukkit.getPluginManager().callEvent(new BukkitProxyAddEvent(proxyInfo));
    }

    @Override
    public void onProxyInfoUpdate(ProxyInfo proxyInfo) {
        Bukkit.getPluginManager().callEvent(new BukkitProxyInfoUpdateEvent(proxyInfo));
    }

    @Override
    public void onProxyRemove(ProxyInfo proxyInfo) {
        Bukkit.getPluginManager().callEvent(new BukkitProxyRemoveEvent(proxyInfo));
    }

    @Override
    public void onCloudNetworkUpdate(CloudNetwork cloudNetwork) {
        Bukkit.getPluginManager().callEvent(new BukkitCloudNetworkUpdateEvent(cloudNetwork));
    }

    @Override
    public void onCustomChannelMessageReceive(String channel, String message, Document document) {
        Bukkit.getPluginManager().callEvent(new BukkitCustomChannelMessageReceiveEvent(channel, message, document));
    }

    @Override
    public void onCustomSubChannelMessageReceive(String channel, String message, Document document) {
        Bukkit.getPluginManager().callEvent(new BukkitSubChannelMessageEvent(channel, message, document));

        if (channel.equalsIgnoreCase("cloudnet_internal")) {
            if (message.equalsIgnoreCase("install_plugin")) {
                String url = document.getString("url");
                try {
                    URLConnection urlConnection = new URL(url).openConnection();
                    urlConnection.setRequestProperty("User-Agent", NetworkUtils.USER_AGENT);
                    urlConnection.connect();
                    Files.copy(urlConnection.getInputStream(), Paths.get("plugins/" + document.getString("name") + ".jar"));
                    File file = new File("plugins/" + document.getString("name") + ".jar");

                    Bukkit.getScheduler().runTask(CloudServer.this.getPlugin(), () -> {
                        try {
                            Plugin plugin = Bukkit.getPluginManager().loadPlugin(file);
                            Bukkit.getPluginManager().enablePlugin(plugin);
                        } catch (InvalidPluginException | InvalidDescriptionException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (message.equals("startedProxy")) {
                ProxyProcessMeta meta = document.getObject("proxyProcessMeta", ProxyProcessMeta.TYPE);
                UUID uuid = meta.getProperties().getObject("cloudnet:requestId", UUID.class);
                this.waitingProxies.forEach((proxyProcessUuid, future) -> {
                    if (proxyProcessUuid.equals(uuid)) {
                        future.complete(meta);
                    }
                });
            } else if (message.equals("startedServer")) {
                ServerProcessMeta meta = document.getObject("serverProcessMeta", ServerProcessMeta.TYPE);
                UUID uuid = meta.getServerConfig().getProperties().getObject("cloudnet:requestId", UUID.class);
                this.waitingServers.forEach((proxyProcessUuid, future) -> {
                    if (proxyProcessUuid.equals(uuid)) {
                        future.complete(meta);
                    }
                });
            }
        }

    }

    /**
     * Returns the Plugin instance from this CLoud-System
     *
     * @return
     */
    public JavaPlugin getPlugin() {
        return bukkitBootstrap;
    }

    @Override
    public void onPlayerLoginNetwork(CloudPlayer cloudPlayer) {
        //cloudPlayers.put(cloudPlayer.getUniqueId(), cloudPlayer);
        Bukkit.getPluginManager().callEvent(new BukkitPlayerLoginNetworkEvent(cloudPlayer));
    }

    @Override
    public void onPlayerDisconnectNetwork(CloudPlayer cloudPlayer) {
        Bukkit.getPluginManager().callEvent(new BukkitPlayerDisconnectEvent(cloudPlayer));
    }

    @Override
    public void onPlayerDisconnectNetwork(UUID uniqueId) {

    }

    @Override
    public void onPlayerUpdate(CloudPlayer cloudPlayer) {
        if (cloudPlayers.containsKey(cloudPlayer.getUniqueId())) {
            cloudPlayers.put(cloudPlayer.getUniqueId(), cloudPlayer);
        }
        Bukkit.getPluginManager().callEvent(new BukkitPlayerUpdateEvent(cloudPlayer));
    }

    @Override
    public void onOfflinePlayerUpdate(OfflinePlayer offlinePlayer) {
        Bukkit.getPluginManager().callEvent(new BukkitOfflinePlayerUpdateEvent(offlinePlayer));
    }

    @Override
    public void onUpdateOnlineCount(int onlineCount) {
        Bukkit.getPluginManager().callEvent(new BukkitOnlineCountUpdateEvent(onlineCount));
    }
}
