/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.ICloudService;
import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.bridge.event.proxied.*;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.proxylayout.ServerFallback;
import de.dytanic.cloudnet.lib.proxylayout.TabList;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ProxyProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class represents a proxy service used in conjunction with CloudNet.
 * This class is a singleton, use the {@link #getInstance()} method for accessing this
 * class' methods.
 */
public class CloudProxy implements ICloudService {

    /**
     * The singleton instance of this cloud proxy class.
     */
    private static CloudProxy instance;

    /**
     * The CloudNet API used in conjunction with some methods.
     */
    private final CloudAPI cloudAPI;

    /**
     * The bootstrap instance that created this proxy instance.
     * Also used whenever a plugin instance is needed.
     */
    private final ProxiedBootstrap proxiedBootstrap;

    /**
     * Meta information about the proxy process.
     */
    private final ProxyProcessMeta proxyProcessMeta;

    /**
     * Cache of all currently running servers that are connected to this proxy.
     */
    private final Map<String, ServerInfo> cachedServers = new ConcurrentHashMap<>();

    /**
     * Cache of all players currently connected to this proxy.
     * This is needed for special cloud operations and plugins.
     */
    private final Map<UUID, CloudPlayer> cloudPlayers = new ConcurrentHashMap<>();

    /**
     * Initializes a new proxy instance.
     * This method should only be called once and only by the bootstrapping class.
     * Subsequent construction attempts are going to fail!
     *
     * @param proxiedBootstrap the bootstrapping instance required for some methods of this class.
     * @param cloudAPI         the API instance required for some functionality, including handling networking.
     *
     * @throws IllegalStateException when the singleton instance has already been constructed.
     */
    CloudProxy(ProxiedBootstrap proxiedBootstrap, CloudAPI cloudAPI) {
        if (instance != null) {
            throw new IllegalStateException("CloudProxy already initialized, use the instance!");
        }
        instance = this;

        this.proxiedBootstrap = proxiedBootstrap;
        this.cloudAPI = cloudAPI;
        this.proxyProcessMeta = this.cloudAPI.getConfig().getObject("proxyProcess", ProxyProcessMeta.TYPE);
        this.cloudAPI.getNetworkHandlerProvider().registerHandler(new NetworkHandlerImpl(cloudAPI));
        this.cloudAPI.getServers().forEach(server -> {
            final String serverId = server.getServiceId().getServerId();
            ProxyServer.getInstance().getServers().put(
                serverId, ProxyServer.getInstance().constructServerInfo(
                    serverId,
                    new InetSocketAddress(server.getHost(), server.getPort()),
                    "CloudNet2 Game-Server",
                    false)
            );
            // Add default fallback to server priority of Bungeecord
            if (server.getServiceId().getGroup().equals(
                getProxyGroup().getProxyConfig().getDynamicFallback().getDefaultFallback())) {
                ProxyServer.getInstance().getConfig().getListeners().forEach(
                    listener -> listener.getServerPriority().add(serverId));
            }
            cachedServers.put(serverId, server);
        });
        this.cloudAPI.setCloudService(this);
    }

    /**
     * Returns the current proxy group of the running proxy server.
     *
     * @return the current proxy group.
     */
    public ProxyGroup getProxyGroup() {
        return this.cloudAPI.getProxyGroupData(this.cloudAPI.getServiceId().getGroup());
    }

    /**
     * Determines and returns the fallback server for the given player.
     *
     * @param cloudPlayer the player to get the fallback server for.
     *
     * @return the fallback server for the player.
     *
     * @see #fallback(ProxiedPlayer, String)
     */
    public String fallback(ProxiedPlayer cloudPlayer) {
        return fallback(cloudPlayer, null);
    }

    /**
     * Returns the instance of this {@link CloudProxy}.
     *
     * @return the singleton instance of this class.
     */
    public static CloudProxy getInstance() {
        return instance;
    }

    /**
     * Determines and returns the server to fall back to for the specified server depending
     * on the server the player has been kicked from.
     *
     * @param cloudPlayer the player to determine the fallback server for.
     * @param kickedFrom  the server the player hsa been kicked from.
     *
     * @return the determined fallback server.
     */
    public String fallback(ProxiedPlayer cloudPlayer, String kickedFrom) {
        String dynamicFallbackServer = getDynamicFallbackServer(cloudPlayer);

        if (dynamicFallbackServer != null) {
            return dynamicFallbackServer;
        } else {
            // Default defaultFallback
            return getDefaultFallbackServer(kickedFrom);
        }
    }

    /**
     * Determines a dynamic server to fall back to for the given player.
     * The result can contain a default fallback server.
     *
     * @param cloudPlayer the player to determine the fallback server for.
     *
     * @return the fallback server or null, if none could be determined.
     */
    private String getDynamicFallbackServer(final ProxiedPlayer cloudPlayer) {
        String defaultFallback = getProxyGroup().getProxyConfig().getDynamicFallback().getDefaultFallback();

        // Choose dynamic defaultFallback
        for (ServerFallback serverFallback : CloudProxy.getInstance()
                                                       .getProxyGroup()
                                                       .getProxyConfig()
                                                       .getDynamicFallback()
                                                       .getFallbacks()) {
            if (serverFallback.getGroup().equals(defaultFallback)) {
                continue;
            }

            if (serverFallback.getPermission() == null || cloudPlayer.hasPermission(serverFallback.getPermission())) {
                List<String> servers = CloudProxy.getInstance().getServers(serverFallback.getGroup());
                if (servers.size() != 0) {
                    return servers.get(NetworkUtils.RANDOM.nextInt(servers.size()));
                }
            }
        }
        return null;
    }

    /**
     * Determines the default fallback server depending on the server a player was kicked from.
     * Due to the fact that any player can join the default fallback server, no player is needed
     * to determine the server.
     * The server the player was kicked from will be excluded from any results.
     *
     * @param kickedFrom the server the player was kicked from.
     *
     * @return a default fallback server for the player to connect to or null, if no suitable server has been found.
     */
    private String getDefaultFallbackServer(final String kickedFrom) {
        String defaultFallback = getProxyGroup().getProxyConfig().getDynamicFallback().getDefaultFallback();

        final List<String> fallbackServers = cachedServers.entrySet()
                                                          .stream()
                                                          .filter(entry -> entry.getValue()
                                                                                .getServiceId()
                                                                                .getGroup()
                                                                                .equals(defaultFallback))
                                                          .map(Map.Entry::getKey)
                                                          .filter(server -> !server.equals(kickedFrom))
                                                          .collect(Collectors.toList());

        if (fallbackServers.size() != 0) {
            return fallbackServers.get(NetworkUtils.RANDOM.nextInt(fallbackServers.size()));
        } else {
            return null;
        }
    }

    /**
     * Returns a list of all currently present servers of a given server group, connected to this proxy.
     *
     * @param group the server group to get the servers for.
     *
     * @return a list of server ids of all currently present servers of the given server group.
     */
    public List<String> getServers(String group) {
        return this.cachedServers.values()
                                 .stream()
                                 .filter(server -> server.getServiceId().getGroup().equalsIgnoreCase(group))
                                 .map(server -> server.getServiceId().getServerId())
                                 .collect(Collectors.toList());
    }

    /**
     * Determines the fallback server for a player that has been kicked from a server.
     *
     * @param cloudPlayer the player to determine the fallback server for
     * @param group       the group of the server that the player was kicked from
     * @param kickedFrom  the server-id of the server the player was kicked from
     *
     * @return the server-id of the server to fall back to
     */
    public String fallbackOnEnabledKick(ProxiedPlayer cloudPlayer, String group, String kickedFrom) {
        String dynamicFallbackServer = getDynamicFallbackServer(cloudPlayer);

        if (dynamicFallbackServer != null) {
            return dynamicFallbackServer;
        }

        List<String> fallbackServers = cachedServers.entrySet()
                                                    .stream()
                                                    .filter(entry -> entry.getValue().getServiceId().getGroup().equals(group))
                                                    .map(Map.Entry::getKey)
                                                    .filter(server -> !server.equals(kickedFrom))
                                                    .collect(Collectors.toList());

        if (fallbackServers.size() != 0) {
            return fallbackServers.get(NetworkUtils.RANDOM.nextInt(fallbackServers.size()));
        }

        //Default defaultFallback
        return getDefaultFallbackServer(kickedFrom);
    }

    /**
     * Asynchronously updates this proxy instance.
     *
     * @see #update()
     */
    public void updateAsync() {
        proxiedBootstrap.getProxy().getScheduler().runAsync(proxiedBootstrap, this::update);
    }

    /**
     * Updates this proxy instance with all of its' state using the API.
     */
    public void update() {
        ProxyInfo proxyInfo = new ProxyInfo(this.cloudAPI.getServiceId(),
                                            this.cloudAPI.getConfig().getString("host"),
                                            0,
                                            true,
                                            ProxyServer.getInstance().getPlayers().stream()
                                                       .collect(Collectors.toMap(ProxiedPlayer::getUniqueId, CommandSender::getName)),
                                            proxyProcessMeta.getMemory(),
                                            ProxyServer.getInstance().getOnlineCount());
        this.cloudAPI.update(proxyInfo);
    }

    /**
     * Returns a map of all currently online players mapped to their UUIDs.
     *
     * @return a map of all online players.
     */
    public Map<UUID, CloudPlayer> getCloudPlayers() {
        return cloudPlayers;
    }

    /**
     * Returns the backing plugin instance.
     *
     * @return the plugin that backs this API instance.
     */
    public Plugin getPlugin() {
        return proxiedBootstrap;
    }

    @Override
    public CloudPlayer getCachedPlayer(UUID uniqueId) {
        return cloudPlayers.get(uniqueId);
    }

    /**
     * Finds and returns an online player by the given name.
     * The player has to be connected to this proxy instance.
     * The name supplied is matched case-insensitive.
     *
     * @param name the name of the player to get
     *
     * @return the cloud player currently online on this proxy and matching the given name.
     */
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
        return true;
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        return this.cachedServers;
    }

    private class NetworkHandlerImpl implements NetworkHandler {

        private final CloudAPI cloudAPI;

        public NetworkHandlerImpl(final CloudAPI cloudAPI) {
            this.cloudAPI = cloudAPI;
        }

        @Override
        public void onServerAdd(ServerInfo serverInfo) {
            if (serverInfo == null) {
                return;
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedServerAddEvent(serverInfo));
            ProxyServer.getInstance().getServers().put(serverInfo.getServiceId().getServerId(),
                                                       ProxyServer.getInstance().constructServerInfo(
                                                           serverInfo.getServiceId().getServerId(),
                                                           new InetSocketAddress(serverInfo.getHost(), serverInfo.getPort()),
                                                           "CloudNet2 Game-Server",
                                                           false));
            // Add default fallback to server priority of Bungeecord
            if (serverInfo.getServiceId().getGroup().equals(
                getProxyGroup().getProxyConfig().getDynamicFallback().getDefaultFallback())) {
                ProxyServer.getInstance().getConfig().getListeners()
                           .forEach(listener ->
                                        listener.getServerPriority().add(serverInfo.getServiceId().getServerId()));
            }
            cachedServers.put(serverInfo.getServiceId().getServerId(), serverInfo);

            if (this.cloudAPI.getModuleProperties().contains("notifyService") &&
                this.cloudAPI.getModuleProperties().getBoolean("notifyService")) {
                for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    if (proxiedPlayer.hasPermission("cloudnet.notify")) {
                        proxiedPlayer.sendMessage(
                            TextComponent.fromLegacyText(
                                ChatColor.translateAlternateColorCodes('&',
                                                                       this.cloudAPI
                                                                           .getCloudNetwork()
                                                                           .getMessages()
                                                                           .getString("notify-message-server-add")
                                                                           .replace("%server%",
                                                                                    serverInfo.getServiceId()
                                                                                              .getServerId()))));
                    }
                }
            }

        }

        @Override
        public void onServerInfoUpdate(ServerInfo serverInfo) {
            if (serverInfo == null) {
                return;
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedServerInfoUpdateEvent(serverInfo));
            cachedServers.put(serverInfo.getServiceId().getServerId(), serverInfo);
        }

        @Override
        public void onServerRemove(ServerInfo serverInfo) {
            if (serverInfo == null) {
                return;
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedServerRemoveEvent(serverInfo));
            ProxyServer.getInstance().getServers().remove(serverInfo.getServiceId().getServerId());
            cachedServers.remove(serverInfo.getServiceId().getServerId());

            // Remove default fallback to server priority of Bungeecord
            if (serverInfo.getServiceId().getGroup().equals(
                getProxyGroup().getProxyConfig().getDynamicFallback().getDefaultFallback())) {
                ProxyServer.getInstance().getConfig().getListeners().forEach(
                    listener -> listener.getServerPriority().remove(serverInfo.getServiceId().getServerId()));
            }

            if (this.cloudAPI.getModuleProperties().getBoolean("notifyService")) {
                for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    if (proxiedPlayer.hasPermission("cloudnet.notify")) {
                        proxiedPlayer.sendMessage(
                            TextComponent.fromLegacyText(
                                ChatColor.translateAlternateColorCodes(
                                    '&', this.cloudAPI.getCloudNetwork().getMessages().getString("notify-message-server-remove")
                                                      .replace("%server%", serverInfo.getServiceId().getServerId()))));
                    }
                }
            }
        }

        @Override
        public void onProxyAdd(ProxyInfo proxyInfo) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedProxyAddEvent(proxyInfo));
        }

        @Override
        public void onProxyInfoUpdate(ProxyInfo proxyInfo) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedProxyInfoUpdateEvent(proxyInfo));
        }

        @Override
        public void onProxyRemove(ProxyInfo proxyInfo) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedProxyRemoveEvent(proxyInfo));
        }

        @Override
        public void onCloudNetworkUpdate(CloudNetwork cloudNetwork) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedCloudNetworkUpdateEvent(cloudNetwork));

            if (cloudNetwork.getProxyGroups().containsKey(this.cloudAPI.getGroup())) {
                ProxyGroup proxyGroup = cloudNetwork.getProxyGroups().get(this.cloudAPI.getGroup());
                if (proxyGroup.getProxyConfig().isEnabled() && proxyGroup.getProxyConfig().isMaintenance()) {
                    for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                        if (!proxyGroup.getProxyConfig().getWhitelist().contains(proxiedPlayer.getName()) &&
                            !proxiedPlayer.hasPermission("cloudnet.maintenance")) {
                            proxiedPlayer.disconnect(
                                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes(
                                    '&', this.cloudAPI.getCloudNetwork().getMessages().getString("kick-maintenance"))));
                        }
                    }
                }
            }

            if (CloudProxy.getInstance().getProxyGroup() != null &&
                CloudProxy.getInstance().getProxyGroup().getProxyConfig().getTabList().isEnabled()) {
                TabList tabList = CloudProxy.getInstance().getProxyGroup().getProxyConfig().getTabList();

                final String proxyId = this.cloudAPI.getServerId();
                final String proxyGroup = CloudProxy.getInstance()
                                                    .getProxyGroup()
                                                    .getName();
                final String onlinePlayers = this.cloudAPI.getOnlineCount() + NetworkUtils.EMPTY_STRING;
                final String maxPlayers = CloudProxy.getInstance()
                                                    .getProxyGroup()
                                                    .getProxyConfig()
                                                    .getMaxPlayers() + NetworkUtils.EMPTY_STRING;

                for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    final String serverName = proxiedPlayer.getServer() != null ?
                        proxiedPlayer.getServer().getInfo().getName() : proxyGroup;
                    final String groupName = proxiedPlayer.getServer() != null &&
                        CloudProxy.getInstance().getServers().containsKey(proxiedPlayer.getServer().getInfo().getName()) ?
                        CloudProxy.getInstance().getServers()
                                  .get(proxiedPlayer.getServer().getInfo().getName())
                                  .getServiceId()
                                  .getGroup() : "Hub";
                    proxiedPlayer.setTabHeader(
                        new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes(
                            '&', tabList.getHeader()
                                        .replace("%proxy%", proxyId)
                                        .replace("%server%", serverName)
                                        .replace("%online_players%", onlinePlayers)
                                        .replace("%max_players%", maxPlayers)
                                        .replace("%group%", groupName)
                                        .replace("%proxy_group%", proxyGroup)))),
                        new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes(
                            '&', tabList.getFooter()
                                        .replace("%proxy%", proxyId)
                                        .replace("%server%", serverName)
                                        .replace("%online_players%", onlinePlayers)
                                        .replace("%max_players%", maxPlayers)
                                        .replace("%group%", groupName)
                                        .replace("%proxy_group%", proxyGroup)))));
                }
            }

        }

        @Override
        public void onCustomChannelMessageReceive(String channel, String message, Document document) {
            if (handle(channel, message, document)) {
                return;
            }
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedCustomChannelMessageReceiveEvent(channel, message, document));
        }

        @Override
        public void onCustomSubChannelMessageReceive(String channel, String message, Document document) {
            if (handle(channel, message, document)) {
                return;
            }
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedSubChannelMessageEvent(channel, message, document));
        }

        @Override
        public void onPlayerLoginNetwork(CloudPlayer cloudPlayer) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerLoginEvent(cloudPlayer));
        }

        @Override
        public void onPlayerDisconnectNetwork(CloudPlayer cloudPlayer) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerLogoutEvent(cloudPlayer));
            cloudPlayers.remove(cloudPlayer.getUniqueId());
        }

        @Override
        public void onPlayerDisconnectNetwork(UUID uniqueId) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerLogoutUniqueEvent(uniqueId));
            cloudPlayers.remove(uniqueId);
        }

        @Override
        public void onPlayerUpdate(CloudPlayer cloudPlayer) {
            if (cloudPlayers.containsKey(cloudPlayer.getUniqueId())) {
                cloudPlayers.put(cloudPlayer.getUniqueId(), cloudPlayer);
            }
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerUpdateEvent(cloudPlayer));
        }

        @Override
        public void onOfflinePlayerUpdate(OfflinePlayer offlinePlayer) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedOfflinePlayerUpdateEvent(offlinePlayer));
        }

        @Override
        public void onUpdateOnlineCount(int onlineCount) {
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedOnlineCountUpdateEvent(onlineCount));
        }

        private boolean handle(String channel, String message, Document document) {

            if (channel.equalsIgnoreCase("cloudnet_internal")) {

                if (message == null) {
                    return false;
                }

                if (message.equalsIgnoreCase("sendMessage")) {
                    UUID uniqueId = document.getObject("uniqueId", UUID.class);
                    if (uniqueId != null) {
                        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uniqueId);

                        if (proxiedPlayer != null) {
                            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(document.getString("message")));
                        }
                    }
                    return true;
                }


                if (message.equalsIgnoreCase("sendMessage_basecomponent")) {
                    UUID uniqueId = document.getObject("uniqueId", UUID.class);
                    if (uniqueId != null) {
                        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uniqueId);

                        if (proxiedPlayer != null) {
                            proxiedPlayer.sendMessage(document.getObject("baseComponent", BaseComponent.class));
                        }
                    }
                }

                if (message.equalsIgnoreCase("kickPlayer")) {
                    UUID uniqueId = document.getObject("uniqueId", UUID.class);
                    if (uniqueId != null) {
                        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uniqueId);

                        if (proxiedPlayer != null) {
                            proxiedPlayer.disconnect(TextComponent.fromLegacyText(document.getString("reason")));
                        }
                    }
                    return true;
                }

                if (message.equalsIgnoreCase("sendActionbar")) {
                    UUID uniqueId = document.getObject("uniqueId", UUID.class);
                    if (uniqueId != null) {
                        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uniqueId);

                        if (proxiedPlayer != null) {
                            proxiedPlayer.sendMessage(ChatMessageType.ACTION_BAR,
                                                      TextComponent.fromLegacyText(document.getString("message")));
                        }
                    }
                    return true;
                }

                if (message.equalsIgnoreCase("sendTitle")) {
                    if (!document.contains("stay") ||
                        !document.contains("fadeIn") ||
                        !document.contains("fadeOut") ||
                        !document.contains("uniqueId")) {
                        return true;
                    }

                    UUID uniqueId = document.getObject("uniqueId", UUID.class);
                    ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(uniqueId);

                    if (proxiedPlayer != null) {
                        Title title = ProxyServer.getInstance().createTitle();

                        if (document.contains("title")) {
                            title.title(TextComponent.fromLegacyText(document.getString("title")));
                        }

                        if (document.contains("subTitle")) {
                            title.subTitle(TextComponent.fromLegacyText(document.getString("subTitle")));
                        }

                        title.fadeIn(document.getInt("fadeIn"))
                             .fadeOut(document.getInt("fadeOut"))
                             .stay(document.getInt("stay"));

                        proxiedPlayer.sendTitle(title);
                    }
                    return true;
                }

                if (message.equalsIgnoreCase("sendPlayer")) {
                    net.md_5.bungee.api.config.ServerInfo serverInfo = ProxyServer.getInstance()
                                                                                  .getServerInfo(document.getString("server"));
                    if (serverInfo != null) {
                        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(document.getObject("uniqueId", UUID.class));
                        if (proxiedPlayer != null) {
                            proxiedPlayer.connect(serverInfo);
                        }
                    }
                    return true;
                }

                if (message.equalsIgnoreCase("player_server_switch")) {
                    ProxyServer.getInstance().getPluginManager().callEvent(
                        new ProxiedPlayerServerSwitchEvent(document.getObject("player", CloudPlayer.TYPE), document.getString("server")));
                    return true;
                }

                return true;
            } else {
                return false;
            }
        }

    }
}
