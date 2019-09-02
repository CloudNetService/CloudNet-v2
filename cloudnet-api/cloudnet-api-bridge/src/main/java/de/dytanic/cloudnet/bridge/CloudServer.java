/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.ICloudService;
import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.api.network.packet.out.PacketOutUpdateServerInfo;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.bridge.event.bukkit.*;
import de.dytanic.cloudnet.bridge.internal.util.ReflectionUtil;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnet.lib.server.ServerState;
import de.dytanic.cloudnet.lib.server.SimpleServerGroup;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * Cloud-Server represents
 */
public class CloudServer implements ICloudService {

    private static CloudServer instance;

    private BukkitBootstrap bukkitBootstrap;

    private Map<UUID, CloudPlayer> cloudPlayers = NetworkUtils.newConcurrentHashMap();

    /*=================================================*/
    private int maxPlayers;

    private String motd;

    private String hostAdress;

    private int port;

    private ServerState serverState;

    private ServerConfig serverConfig;

    private Template template;

    private int memory;

    private boolean allowAutoStart = true;
    /*=================================================*/

    public CloudServer(BukkitBootstrap bukkitBootstrap, CloudAPI cloudAPI) {
        instance = this;
        cloudAPI.setCloudService(this);

        this.bukkitBootstrap = bukkitBootstrap;
        ServerInfo serverInfo = cloudAPI.getConfig().getObject("serverInfo", new TypeToken<ServerInfo>() {}.getType());

        cloudAPI.getNetworkHandlerProvider().registerHandler(new NetworkHandlerImpl());
        this.allowAutoStart = !cloudAPI.getConfig().contains("cloudProcess");
        this.maxPlayers = serverInfo.getMaxPlayers();
        this.motd = serverInfo.getMotd();
        this.hostAdress = serverInfo.getHost();
        this.port = serverInfo.getPort();
        this.serverConfig = serverInfo.getServerConfig();
        this.memory = serverInfo.getMemory();
        this.template = serverInfo.getTemplate();
        this.serverState = ServerState.LOBBY;
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
        List<String> list = new CopyOnWriteArrayList<>();

        for (Player all : Bukkit.getOnlinePlayers()) {
            list.add(all.getName());
        }

        ServerInfo serverInfo = new ServerInfo(CloudAPI.getInstance().getServiceId(),
                                               hostAdress,
                                               port,
                                               false,
                                               list,
                                               memory,
                                               motd,
                                               Bukkit.getOnlinePlayers().size(),
                                               maxPlayers,
                                               serverState,
                                               serverConfig,
                                               template);
        CloudAPI.getInstance().getNetworkConnection().sendPacketSynchronized(new PacketOutUpdateServerInfo(serverInfo));
    }

    /**
     * Updates the ServerInfo on a asynchronized BukkitScheduler Task
     */
    public void updateAsync() {
        bukkitBootstrap.getServer().getScheduler().runTaskAsynchronously(bukkitBootstrap, new Runnable() {
            @Override
            public void run() {
                List<String> list = new CopyOnWriteArrayList<>();
                for (Player all : Bukkit.getOnlinePlayers()) {
                    list.add(all.getName());
                }

                ServerInfo serverInfo = new ServerInfo(CloudAPI.getInstance().getServiceId(),
                                                       hostAdress,
                                                       port,
                                                       true,
                                                       list,
                                                       memory,
                                                       motd,
                                                       Bukkit.getOnlinePlayers().size(),
                                                       maxPlayers,
                                                       serverState,
                                                       serverConfig,
                                                       template);
                CloudAPI.getInstance().update(serverInfo);
            }
        });
    }

    /**
     * Changed the State to INGAME and Start a gameserver
     */
    public void changeToIngame() {
        setServerState(ServerState.INGAME);

        if (isAllowAutoStart()) {
            SimpleServerGroup simpleServerGroup = CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup());
            CloudAPI.getInstance().startGameServer(simpleServerGroup, template);
            setAllowAutoStart(false);

            Bukkit.getScheduler().runTaskLater(bukkitBootstrap, new Runnable() {
                @Override
                public void run() {
                    setAllowAutoStart(true);
                }
            }, 6000);
        }

        update();
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
     * Updates the ServerInfo
     */
    public void update() {
        List<String> list = new CopyOnWriteArrayList<>();
        for (Player all : Bukkit.getOnlinePlayers()) {
            list.add(all.getName());
        }

        ServerInfo serverInfo = new ServerInfo(CloudAPI.getInstance().getServiceId(),
                                               hostAdress,
                                               port,
                                               true,
                                               list,
                                               memory,
                                               motd,
                                               Bukkit.getOnlinePlayers().size(),
                                               maxPlayers,
                                               serverState,
                                               serverConfig,
                                               template);
        CloudAPI.getInstance().update(serverInfo);
    }

    /**
     * You can disable the Autostart funtction from this server
     *
     * @param allowAutoStart
     */
    public void setAllowAutoStart(boolean allowAutoStart) {
        this.allowAutoStart = allowAutoStart;
    }

    @Deprecated
    public void getPlayerAndCache(UUID uniqueId) {
        CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(uniqueId);
        if (cloudPlayer != null) {
            cloudPlayer.setPlayerExecutor(new PlayerExecutorBridge());
            this.cloudPlayers.put(uniqueId, cloudPlayer);
        }
    }

    public void setServerStateAndUpdate(ServerState serverStateAndUpdate) {
        this.serverState = serverStateAndUpdate;
        update();
    }

    public int getPort() {
        return port;
    }

    public String getHostAdress() {
        return hostAdress;
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
        return CloudAPI.getInstance().getCloudNetwork().getServerGroups().get(CloudAPI.getInstance().getGroup());
    }

    public double getPercentOfPlayerNowOnline() {
        return (((double) Bukkit.getOnlinePlayers().size()) / (double) maxPlayers) * 100;
    }

    /**
     * Returns the Plugin instance from this CLoud-System
     *
     * @return
     */
    public JavaPlugin getPlugin() {
        return bukkitBootstrap;
    }

    /**
     * Returns the ServerProcessMeta for the bootstrap of the software
     *
     * @return
     */
    public ServerProcessMeta getServerProcessMeta() {
        return CloudAPI.getInstance().getConfig().getObject("serverProcess", new TypeToken<ServerProcessMeta>() {}.getType());
    }

    /**
     * @param player
     */
    public void updateNameTags(Player player) {
        this.updateNameTags(player, null);
    }

    public void updateNameTags(Player player, Function<Player, PermissionGroup> playerPermissionGroupFunction) {
        this.updateNameTags(player, playerPermissionGroupFunction, null);
    }

    public void updateNameTags(Player player,
                               Function<Player, PermissionGroup> playerPermissionGroupFunction,
                               Function<Player, PermissionGroup> allOtherPlayerPermissionGroupFunction) {
        if (CloudAPI.getInstance().getPermissionPool() == null || !CloudAPI.getInstance().getPermissionPool().isAvailable()) {
            return;
        }

        PermissionGroup playerPermissionGroup = playerPermissionGroupFunction != null ? playerPermissionGroupFunction.apply(player) : getCloudPlayers()
            .get(player.getUniqueId())
            .getPermissionEntity()
            .getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());

        initScoreboard(player);

        for (Player all : player.getServer().getOnlinePlayers()) {
            initScoreboard(all);

            if (playerPermissionGroup != null) {
                addTeamEntry(player, all, playerPermissionGroup);
            }

            PermissionGroup targetPermissionGroup = allOtherPlayerPermissionGroupFunction != null ? allOtherPlayerPermissionGroupFunction.apply(
                all) : null;

            if (targetPermissionGroup == null) {
                targetPermissionGroup = getCachedPlayer(all.getUniqueId()).getPermissionEntity()
                                                                          .getHighestPermissionGroup(CloudAPI.getInstance()
                                                                                                             .getPermissionPool());
            }

            if (targetPermissionGroup != null) {
                addTeamEntry(all, player, targetPermissionGroup);
            }

        }
    }

    /**
     * Returns the cached CloudPlayer Objectives
     *
     * @return
     */
    public Map<UUID, CloudPlayer> getCloudPlayers() {
        return cloudPlayers;
    }

    private void initScoreboard(Player all) {
        if (all.getScoreboard() == null) {
            all.setScoreboard(all.getServer().getScoreboardManager().getNewScoreboard());
        }
    }

    private void addTeamEntry(Player target, Player all, PermissionGroup permissionGroup) {
        String teamName = permissionGroup.getTagId() + permissionGroup.getName();
        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
            CloudAPI.getInstance()
                    .dispatchConsoleMessage("In order to prevent issues, the name (+ tagID) of the group " + permissionGroup.getName() + " was temporarily shortened to 16 characters!");
            CloudAPI.getInstance().dispatchConsoleMessage("Please fix this issue by changing the name of the group in your perms.yml");
            Bukkit.broadcast("In order to prevent issues, the name (+ tagID) of the group " + permissionGroup.getName() + " was temporarily shortened to 16 characters!",
                             "cloudnet.notify");
            Bukkit.broadcast("Please fix this issue by changing the name of the group in your perms.yml", "cloudnet.notify");
        }
        Team team = all.getScoreboard().getTeam(teamName);
        if (team == null) {
            team = all.getScoreboard().registerNewTeam(teamName);
        }

        if (permissionGroup.getPrefix().length() > 16) {
            permissionGroup.setPrefix(permissionGroup.getPrefix().substring(0, 16));
            CloudAPI.getInstance()
                    .dispatchConsoleMessage("In order to prevent issues, the prefix of the group " + permissionGroup.getName() + " was temporarily shortened to 16 characters!");
            CloudAPI.getInstance().dispatchConsoleMessage("Please fix this issue by changing the prefix in your perms.yml");
            Bukkit.broadcast("In order to prevent issues, the prefix of the group " + permissionGroup.getName() + " was temporarily shortened to 16 characters!",
                             "cloudnet.notify");
            Bukkit.broadcast("Please fix this issue by changing the prefix in your perms.yml", "cloudnet.notify");
        }
        if (permissionGroup.getSuffix().length() > 16) {
            permissionGroup.setSuffix(permissionGroup.getSuffix().substring(0, 16));
            CloudAPI.getInstance()
                    .dispatchConsoleMessage("In order to prevent issues, the suffix of the group " + permissionGroup.getName() + " was temporarily shortened to 16 characters!");
            CloudAPI.getInstance().dispatchConsoleMessage("Please fix this issue by changing the suffix in your perms.yml");
            Bukkit.broadcast("In order to prevent issues, the suffix of the group " + permissionGroup.getName() + " was temporarily shortened to 16 characters!",
                             "cloudnet.notify");
            Bukkit.broadcast("Please fix this issue by changing the suffix in your perms.yml", "cloudnet.notify");
        }

        try {
            Method setColor = team.getClass().getDeclaredMethod("setColor", ChatColor.class);
            setColor.setAccessible(true);
            if (permissionGroup.getColor().length() != 0) {
                setColor.invoke(team, ChatColor.getByChar(permissionGroup.getColor().replaceAll("&", "").replaceAll("§", "")));
            } else {
                setColor.invoke(team, ChatColor.getByChar(ChatColor.getLastColors(permissionGroup.getPrefix().replace('&', '§'))
                                                                   .replaceAll("&", "")
                                                                   .replaceAll("§", "")));
            }
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }


        team.setPrefix(ChatColor.translateAlternateColorCodes('&', permissionGroup.getPrefix()));
        team.setSuffix(ChatColor.translateAlternateColorCodes('&', permissionGroup.getSuffix()));

        team.addEntry(target.getName());

        target.setDisplayName(ChatColor.translateAlternateColorCodes('&', permissionGroup.getDisplay() + target.getName()));
    }

    public CloudPlayer getCachedPlayer(UUID uniqueId) {
        return cloudPlayers.get(uniqueId);
    }

    public CloudPlayer getCachedPlayer(String name) {
        return CollectionWrapper.filter(this.cloudPlayers.values(), new Acceptable<CloudPlayer>() {
            @Override
            public boolean isAccepted(CloudPlayer cloudPlayer) {
                return cloudPlayer.getName().equalsIgnoreCase(name);
            }
        });
    }

    @Override
    public boolean isProxyInstance() {
        return false;
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        throw new UnsupportedOperationException();
    }

    public Map<UUID, CloudPlayer> getClonedCloudPlayers() {
        return new HashMap<>(this.cloudPlayers);
    }

    //API Handler
    /*================================================================================================================*/
    private class NetworkHandlerImpl implements NetworkHandler {

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
                        urlConnection.setRequestProperty("User-Agent",
                                                         "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
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
                }
            }

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
}
