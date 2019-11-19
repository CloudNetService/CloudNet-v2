/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.ICloudService;
import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.bridge.event.proxied.*;
import de.dytanic.cloudnet.bridge.internal.chat.PlayerChatExecutor;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.MultiValue;
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
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This Class represents the Proxy Instance on based on cloudnet
 */
public class CloudProxy implements ICloudService, PlayerChatExecutor {

    private static CloudProxy instance;

    private ProxiedBootstrap proxiedBootstrap;
    private ProxyProcessMeta proxyProcessMeta;

    private Map<String, ServerInfo> cachedServers = new ConcurrentHashMap<>();
    private Map<UUID, CloudPlayer> cloudPlayers = new ConcurrentHashMap<>();

    CloudProxy(ProxiedBootstrap proxiedBootstrap, CloudAPI cloudAPI) {
        instance = this;

        this.proxiedBootstrap = proxiedBootstrap;
        this.proxyProcessMeta = cloudAPI.getConfig().getObject("proxyProcess", ProxyProcessMeta.TYPE);
        cloudAPI.getNetworkHandlerProvider().registerHandler(new NetworkHandlerImpl());
        cloudAPI.getServers().forEach(server -> {
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
        cloudAPI.setCloudService(this);
    }

    public ProxyGroup getProxyGroup() {
        return CloudAPI.getInstance().getProxyGroupData(CloudAPI.getInstance().getServiceId().getGroup());
    }

    public String fallback(ProxiedPlayer cloudPlayer) {
        return fallback(cloudPlayer, null);
    }

    /**
     * Returns the instance which respens the api
     *
     * @return
     */
    public static CloudProxy getInstance() {
        return instance;
    }

    public List<String> getServers(String group) {
        List<String> x = new ArrayList<>();
        for (ServerInfo server : this.cachedServers.values()) {
            if (server.getServiceId().getGroup().equalsIgnoreCase(group)) {
                x.add(server.getServiceId().getServerId());
            }
        }
        return x;
    }

    /**
     * Returns the Servers on cloudnet
     *
     * @return
     */
    public Map<String, ServerInfo> getCachedServers() {
        return cachedServers;
    }

    public String fallback(ProxiedPlayer cloudPlayer, String kickedFrom) {
        String dynamicFallbackServer = getDynamicFallbackServer(cloudPlayer);

        if (dynamicFallbackServer != null) {
            return dynamicFallbackServer;
        } else {
            // Default defaultFallback
            return getDefaultFallbackServer(kickedFrom);
        }
    }

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

    public void updateAsync() {
        proxiedBootstrap.getProxy().getScheduler().runAsync(proxiedBootstrap, this::update);
    }

    public void update() {
        ProxyInfo proxyInfo = new ProxyInfo(CloudAPI.getInstance().getServiceId(),
                                            CloudAPI.getInstance().getConfig().getString("host"),
                                            0,
                                            true,
                                            ProxyServer.getInstance().getPlayers().stream()
                                                       .map(player -> new MultiValue<>(player.getUniqueId(), player.getName()))
                                                       .collect(Collectors.toList()),
                                            proxyProcessMeta.getMemory(),
                                            ProxyServer.getInstance().getOnlineCount());
        CloudAPI.getInstance().update(proxyInfo);
    }

    /**
     * Returns the cloudPlayers online
     *
     * @return
     */
    public Map<UUID, CloudPlayer> getCloudPlayers() {
        return cloudPlayers;
    }

    /**
     * Returns the API of the plugin instance
     *
     * @return
     */
    public Plugin getPlugin() {
        return proxiedBootstrap;
    }

    @Override
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
        return true;
    }

    @Override
    public Map<String, ServerInfo> getServers() {
        return this.getCachedServers();
    }

    private class NetworkHandlerImpl implements NetworkHandler {
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

            if (CloudAPI.getInstance().getModuleProperties().contains("notifyService") &&
                CloudAPI.getInstance().getModuleProperties().getBoolean("notifyService")) {
                for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    if (proxiedPlayer.hasPermission("cloudnet.notify")) {
                        proxiedPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                                                         CloudAPI.getInstance()
                                                                                                 .getCloudNetwork()
                                                                                                 .getMessages()
                                                                                                 .getString("notify-message-server-add")
                                                                                                 .replace("%server%",
                                                                                                          serverInfo.getServiceId()
                                                                                                                    .getServerId())));
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

            try {
                ProxyServer.getInstance().getServers().remove(serverInfo.getServiceId().getServerId());
            } catch (Throwable ignored) {
            }

            cachedServers.remove(serverInfo.getServiceId().getServerId());

            // Remove default fallback to server priority of Bungeecord
            if (serverInfo.getServiceId().getGroup().equals(
                getProxyGroup().getProxyConfig().getDynamicFallback().getDefaultFallback())) {
                ProxyServer.getInstance().getConfig().getListeners().forEach(
                    listener -> listener.getServerPriority().remove(serverInfo.getServiceId().getServerId()));
            }

            if (CloudAPI.getInstance().getModuleProperties().contains("notifyService") && CloudAPI.getInstance()
                                                                                                  .getModuleProperties()
                                                                                                  .getBoolean("notifyService")) {
                for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    if (proxiedPlayer.hasPermission("cloudnet.notify")) {
                        proxiedPlayer.sendMessage(
                            ChatColor.translateAlternateColorCodes('&',
                                                                   CloudAPI.getInstance()
                                                                           .getCloudNetwork()
                                                                           .getMessages()
                                                                           .getString("notify-message-server-remove")
                                                                           .replace("%server%", serverInfo.getServiceId().getServerId())));
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

            if (cloudNetwork.getProxyGroups().containsKey(CloudAPI.getInstance().getGroup())) {
                ProxyGroup proxyGroup = cloudNetwork.getProxyGroups().get(CloudAPI.getInstance().getGroup());
                if (proxyGroup.getProxyConfig().isEnabled() && proxyGroup.getProxyConfig().isMaintenance()) {
                    for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                        if (!proxyGroup.getProxyConfig().getWhitelist().contains(proxiedPlayer.getName()) && !proxiedPlayer.hasPermission(
                            "cloudnet.maintenance")) {
                            proxiedPlayer.disconnect(ChatColor.translateAlternateColorCodes('&',
                                                                                            CloudAPI.getInstance()
                                                                                                    .getCloudNetwork()
                                                                                                    .getMessages()
                                                                                                    .getString("kick-maintenance")));
                        }
                    }
                }
            }

            if (CloudProxy.getInstance().getProxyGroup() != null && CloudProxy.getInstance()
                                                                              .getProxyGroup()
                                                                              .getProxyConfig()
                                                                              .getTabList()
                                                                              .isEnabled()) {
                TabList tabList = CloudProxy.getInstance().getProxyGroup().getProxyConfig().getTabList();

                for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    proxiedPlayer.setTabHeader(new TextComponent(ChatColor.translateAlternateColorCodes('&', tabList.getHeader()
                                                                                                                    .replace("%proxy%",
                                                                                                                             CloudAPI.getInstance()
                                                                                                                                     .getServerId())
                                                                                                                    .replace("%server%",
                                                                                                                             (proxiedPlayer.getServer() != null ? proxiedPlayer
                                                                                                                                 .getServer()
                                                                                                                                 .getInfo()
                                                                                                                                 .getName() : CloudProxy
                                                                                                                                 .getInstance()
                                                                                                                                 .getProxyGroup()
                                                                                                                                 .getName()))
                                                                                                                    .replace(
                                                                                                                        "%online_players%",
                                                                                                                        CloudAPI.getInstance()
                                                                                                                                .getOnlineCount() + NetworkUtils.EMPTY_STRING)
                                                                                                                    .replace("%max_players%",
                                                                                                                             CloudProxy.getInstance()
                                                                                                                                       .getProxyGroup()
                                                                                                                                       .getProxyConfig()
                                                                                                                                       .getMaxPlayers() + NetworkUtils.EMPTY_STRING)
                                                                                                                    .replace("%group%",
                                                                                                                             (proxiedPlayer.getServer() != null && CloudProxy
                                                                                                                                 .getInstance()
                                                                                                                                 .getCachedServers()
                                                                                                                                 .containsKey(
                                                                                                                                     proxiedPlayer
                                                                                                                                         .getServer()
                                                                                                                                         .getInfo()
                                                                                                                                         .getName()) ? CloudProxy
                                                                                                                                 .getInstance()
                                                                                                                                 .getCachedServers()
                                                                                                                                 .get(
                                                                                                                                     proxiedPlayer
                                                                                                                                         .getServer()
                                                                                                                                         .getInfo()
                                                                                                                                         .getName())
                                                                                                                                 .getServiceId()
                                                                                                                                 .getGroup() : "Hub"))
                                                                                                                    .replace("%proxy_group%",
                                                                                                                             CloudProxy.getInstance()
                                                                                                                                       .getProxyGroup()
                                                                                                                                       .getName()))),
                                               new TextComponent(ChatColor.translateAlternateColorCodes('&', tabList.getFooter()
                                                                                                                    .replace("%proxy%",
                                                                                                                             CloudAPI.getInstance()
                                                                                                                                     .getServerId())
                                                                                                                    .replace("%server%",
                                                                                                                             (proxiedPlayer.getServer() != null ? proxiedPlayer
                                                                                                                                 .getServer()
                                                                                                                                 .getInfo()
                                                                                                                                 .getName() : CloudProxy
                                                                                                                                 .getInstance()
                                                                                                                                 .getProxyGroup()
                                                                                                                                 .getName()))
                                                                                                                    .replace(
                                                                                                                        "%online_players%",
                                                                                                                        CloudAPI.getInstance()
                                                                                                                                .getOnlineCount() + NetworkUtils.EMPTY_STRING)
                                                                                                                    .replace("%max_players%",
                                                                                                                             CloudProxy.getInstance()
                                                                                                                                       .getProxyGroup()
                                                                                                                                       .getProxyConfig()
                                                                                                                                       .getMaxPlayers() + NetworkUtils.EMPTY_STRING)
                                                                                                                    .replace("%group%",
                                                                                                                             (proxiedPlayer.getServer() != null && CloudProxy
                                                                                                                                 .getInstance()
                                                                                                                                 .getCachedServers()
                                                                                                                                 .containsKey(
                                                                                                                                     proxiedPlayer
                                                                                                                                         .getServer()
                                                                                                                                         .getInfo()
                                                                                                                                         .getName()) ? CloudProxy
                                                                                                                                 .getInstance()
                                                                                                                                 .getCachedServers()
                                                                                                                                 .get(
                                                                                                                                     proxiedPlayer
                                                                                                                                         .getServer()
                                                                                                                                         .getInfo()
                                                                                                                                         .getName())
                                                                                                                                 .getServiceId()
                                                                                                                                 .getGroup() : "Hub"))
                                                                                                                    .replace("%proxy_group%",
                                                                                                                             CloudProxy.getInstance()
                                                                                                                                       .getProxyGroup()
                                                                                                                                       .getName()))));
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
                            proxiedPlayer.sendMessage(new TextComponent(TextComponent.fromLegacyText(document.getString("message"))));
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
                            proxiedPlayer.disconnect(document.getString("reason"));
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
                    if (!document.contains("stay") || !document.contains("fadeIn") || !document.contains("fadeOut") || !document.contains(
                        "uniqueId")) {
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

                        title.fadeIn(document.getInt("fadeIn")).fadeOut(document.getInt("fadeOut")).stay(document.getInt("stay"));

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
                    ProxyServer.getInstance().getPluginManager().callEvent(new ProxiedPlayerServerSwitchEvent(document.getObject("player",
                                                                                                                                 CloudPlayer.TYPE),
                                                                                                              document.getString("server")));

                    return true;
                }


                return true;
            } else {
                return false;
            }
        }

    }
}
