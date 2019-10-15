/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.listener.proxied;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.network.packet.out.*;
import de.dytanic.cloudnet.bridge.CloudProxy;
import de.dytanic.cloudnet.bridge.event.proxied.ProxiedOnlineCountUpdateEvent;
import de.dytanic.cloudnet.bridge.event.proxied.ProxiedPlayerFallbackEvent;
import de.dytanic.cloudnet.bridge.internal.util.CloudPlayerCommandSender;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.PlayerCommandExecution;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnet.lib.proxylayout.Motd;
import de.dytanic.cloudnet.lib.proxylayout.ProxyConfig;
import de.dytanic.cloudnet.lib.proxylayout.TabList;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.Respawn;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Tareko on 18.08.2017.
 */
public class ProxiedListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleProxyPing(ProxyPingEvent event) {
        ProxyGroup proxyGroup = CloudAPI.getInstance().getProxyGroupData(CloudAPI.getInstance().getGroup());
        if (proxyGroup != null && proxyGroup.getProxyConfig().isEnabled()) {
            ProxyConfig proxyConfig = proxyGroup.getProxyConfig();
            ServerPing serverPing = event.getResponse();

            if (!proxyConfig.isMaintenance()) {
                Motd motd = proxyConfig.getMotdsLayouts().get(NetworkUtils.RANDOM.nextInt(proxyConfig.getMotdsLayouts().size()));
                serverPing.setDescription(ChatColor.translateAlternateColorCodes('&', motd.getFirstLine() + '\n' + motd.getSecondLine())
                                                   .replace("%proxy%", CloudAPI.getInstance().getServerId())
                                                   .replace("%version%", CloudProxy.class.getPackage().getImplementationVersion()));
            } else {
                serverPing.setDescription(ChatColor.translateAlternateColorCodes('&',
                                                                                 proxyConfig.getMaintenanceMotdLayout()
                                                                                            .getFirstLine() + '\n' + proxyConfig.getMaintenanceMotdLayout()
                                                                                                                                .getSecondLine())
                                                   .replace("%proxy%", CloudAPI.getInstance().getServerId())
                                                   .replace("%version%", CloudProxy.class.getPackage().getImplementationVersion()));
            }

            int onlineCount = CloudAPI.getInstance().getOnlineCount();
            int max = (proxyConfig.getAutoSlot().isEnabled() ? onlineCount + proxyConfig.getAutoSlot()
                                                                                        .getDynamicSlotSize() : proxyConfig.getMaxPlayers());

            ServerPing.PlayerInfo[] playerInfos = new ServerPing.PlayerInfo[proxyConfig.getPlayerInfo().length];
            for (short i = 0; i < playerInfos.length; i++) {
                playerInfos[i] = new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', proxyConfig.getPlayerInfo()[i]),
                                                           UUID.randomUUID());
            }
            serverPing.setPlayers(new ServerPing.Players(max, onlineCount, playerInfos));

            if (proxyConfig.isMaintenance()) {
                serverPing.setVersion(new ServerPing.Protocol(proxyConfig.getMaintenaceProtocol(), 1));
            }
            event.setResponse(serverPing);
        }
    }

    @EventHandler
    public void handlePluginMessage(PluginMessageEvent e) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handlePluginMessage",
                                                String.format("Handling plugin message event: %s", e));
        if (e.getTag().equals("MC|BSign") || e.getTag().equals("MC|BEdit")) {
            if (CloudProxy.getInstance().getProxyGroup() != null && CloudProxy.getInstance()
                                                                              .getProxyGroup()
                                                                              .getProxyConfig()
                                                                              .getCustomPayloadFixer()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerServerSwitch(ServerSwitchEvent e) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handlePlayerServerSwitch",
                                                String.format("Handling server switch event: %s", e));
        CloudPlayer cloudPlayer = CloudProxy.getInstance().getCloudPlayers().get(e.getPlayer().getUniqueId());
        cloudPlayer.setServer(e.getPlayer().getServer().getInfo().getName());

        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutUpdateOnlinePlayer(cloudPlayer));

        CloudAPI.getInstance().sendCustomSubProxyMessage("cloudnet_internal",
                                                         "player_server_switch",
                                                         new Document("player", cloudPlayer).append("server",
                                                                                                    e.getPlayer()
                                                                                                     .getServer()
                                                                                                     .getInfo()
                                                                                                     .getName()));

        if (CloudProxy.getInstance().getProxyGroup() != null && CloudProxy.getInstance()
                                                                          .getProxyGroup()
                                                                          .getProxyConfig()
                                                                          .isEnabled() && CloudProxy.getInstance()
                                                                                                    .getProxyGroup()
                                                                                                    .getProxyConfig()
                                                                                                    .getTabList()
                                                                                                    .isEnabled()) {
            initTabHeaderFooter(e.getPlayer());
        }
    }

    private void initTabHeaderFooter(ProxiedPlayer proxiedPlayer) {
        TabList tabList = CloudProxy.getInstance().getProxyGroup().getProxyConfig().getTabList();
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
                                                                                                        .replace("%online_players%",
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
                                                                                                                         proxiedPlayer.getServer()
                                                                                                                                      .getInfo()
                                                                                                                                      .getName()) ? CloudProxy
                                                                                                                     .getInstance()
                                                                                                                     .getCachedServers()
                                                                                                                     .get(proxiedPlayer.getServer()
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
                                                                                                        .replace("%online_players%",
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
                                                                                                                         proxiedPlayer.getServer()
                                                                                                                                      .getInfo()
                                                                                                                                      .getName()) ? CloudProxy
                                                                                                                     .getInstance()
                                                                                                                     .getCachedServers()
                                                                                                                     .get(proxiedPlayer.getServer()
                                                                                                                                       .getInfo()
                                                                                                                                       .getName())
                                                                                                                     .getServiceId()
                                                                                                                     .getGroup() : "Hub"))
                                                                                                        .replace("%proxy_group%",
                                                                                                                 CloudProxy.getInstance()
                                                                                                                           .getProxyGroup()
                                                                                                                           .getName()))));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleLogin(LoginEvent e) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handleLogin",
                                                String.format("Handling login event: %s", e));
        PlayerConnection playerConnection = new PlayerConnection(e.getConnection().getUniqueId(),
                                                                 e.getConnection().getName(),
                                                                 e.getConnection().getVersion(),
                                                                 e.getConnection().getAddress().getAddress().getHostAddress(),
                                                                 e.getConnection().getAddress().getPort(),
                                                                 e.getConnection().isOnlineMode(),
                                                                 e.getConnection().isLegacy());

        Document result = CloudAPI.getInstance().getNetworkConnection().getPacketManager().sendQuery(new PacketOutPlayerLoginRequest(
            playerConnection), CloudAPI.getInstance().getNetworkConnection()).getResult();

        CloudPlayer cloudPlayer = result.getObject("player", new TypeToken<CloudPlayer>() {}.getType());

        if (cloudPlayer == null) {
            CloudAPI.getInstance().getLogger().finest("cloudPlayer is null!");
            e.setCancelReason(TextComponent.fromLegacyText("§cUnverified login. Reason: §e" + (result.contains("reason") ? result.getString(
                "reason") : "no reason defined")));
            e.setCancelled(true);
            return;
        }

        CommandSender cloudCommandSender = new CloudPlayerCommandSender(cloudPlayer);

        if (CloudProxy.getInstance().getProxyGroup() != null) {
            ProxyConfig proxyConfig = CloudProxy.getInstance().getProxyGroup().getProxyConfig();
            if ((proxyConfig.isEnabled() && proxyConfig.isMaintenance())) {
                PermissionCheckEvent permissionCheckEvent = new PermissionCheckEvent(cloudCommandSender, "cloudnet.maintenance", false);

                if (!proxyConfig.getWhitelist().contains(e.getConnection().getName()) && !proxyConfig.getWhitelist()
                                                                                                     .contains(e.getConnection()
                                                                                                                .getUniqueId()
                                                                                                                .toString()) && !ProxyServer
                    .getInstance()
                    .getPluginManager()
                    .callEvent(permissionCheckEvent)
                    .hasPermission()) {
                    e.setCancelled(true);
                    e.setCancelReason(ChatColor.translateAlternateColorCodes('&',
                                                                             CloudAPI.getInstance()
                                                                                     .getCloudNetwork()
                                                                                     .getMessages()
                                                                                     .getString("kick-maintenance")));
                    return;
                }
            }
        }

        ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();

        if (proxyGroup != null) {
            if (proxyGroup.getProxyConfig().isEnabled()) {
                if (CloudAPI.getInstance().getOnlineCount() >= CloudProxy.getInstance().getProxyGroup().getProxyConfig().getMaxPlayers()) {
                    PermissionCheckEvent permissionCheckEvent = new PermissionCheckEvent(cloudCommandSender, "cloudnet.fulljoin", false);

                    if (!ProxyServer.getInstance().getPluginManager().callEvent(permissionCheckEvent).hasPermission()) {
                        e.setCancelled(true);
                        e.setCancelReason(ChatColor.translateAlternateColorCodes('&',
                                                                                 CloudAPI.getInstance()
                                                                                         .getCloudNetwork()
                                                                                         .getMessages()
                                                                                         .getString("full-join")));
                        return;
                    }
                }
            }
        }

        CloudProxy.getInstance().getCloudPlayers().put(cloudPlayer.getUniqueId(), cloudPlayer);
    }

    @EventHandler
    public void handlePost(PostLoginEvent e) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handlePost",
                                                String.format("Handling post login event: %s", e));

        CloudProxy.getInstance().update();
        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutLoginSuccess(e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void handleChat(ChatEvent e) {
        if (e.getMessage().startsWith(NetworkUtils.SLASH_STRING)) {
            if (e.getSender() instanceof ProxiedPlayer) {
                CloudAPI.getInstance()
                        .getNetworkConnection()
                        .sendPacket(new PacketOutCommandExecute(new PlayerCommandExecution(((ProxiedPlayer) e.getSender()).getName(),
                                                                                           e.getMessage())));
            }
        }
    }

    @EventHandler
    public void handlePermissionCheck(PermissionCheckEvent e) {
        if (CloudAPI.getInstance().getPermissionPool() == null || !CloudAPI.getInstance().getPermissionPool().isAvailable()) {
            return;
        }

        if (e.getSender() instanceof ProxiedPlayer) {
            if (CloudProxy.getInstance().getCloudPlayers().containsKey(((ProxiedPlayer) e.getSender()).getUniqueId())) {
                e.setHasPermission(CloudProxy.getInstance()
                                             .getCloudPlayers()
                                             .get(((ProxiedPlayer) e.getSender()).getUniqueId())
                                             .getPermissionEntity()
                                             .hasPermission(CloudAPI.getInstance().getPermissionPool(),
                                                            e.getPermission(),
                                                            CloudAPI.getInstance().getGroup()));
            }
        } else if (e.getSender() instanceof CloudPlayerCommandSender) {
            e.setHasPermission(((CloudPlayerCommandSender) e.getSender()).getCloudPlayer()
                                                                         .getPermissionEntity()
                                                                         .hasPermission(CloudAPI.getInstance().getPermissionPool(),
                                                                                        e.getPermission(),
                                                                                        CloudAPI.getInstance().getGroup()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleDisconnect(PlayerDisconnectEvent e) {
        CloudPlayer cloudPlayer = CloudProxy.getInstance().getCloudPlayers().get(e.getPlayer().getUniqueId());
        if (cloudPlayer != null) {
            CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutLogoutPlayer(cloudPlayer, e.getPlayer().getUniqueId()));
        } else {
            CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutLogoutPlayer(null, e.getPlayer().getUniqueId()));
        }
        CloudProxy.getInstance().getCloudPlayers().remove(e.getPlayer().getUniqueId());
        ProxyServer.getInstance().getScheduler().schedule(CloudProxy.getInstance().getPlugin(), new Runnable() {
            @Override
            public void run() {
                CloudProxy.getInstance().update();
            }
        }, 250, TimeUnit.MILLISECONDS);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleServerConnect(ServerConnectEvent event) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handleServerConnect",
                                                String.format("Handling server connect event: %s", event));
        if (event.getPlayer().getServer() == null) {
            String fallback = CloudProxy.getInstance().fallback(event.getPlayer());
            ProxiedPlayerFallbackEvent proxiedPlayerFallbackEvent = new ProxiedPlayerFallbackEvent(event.getPlayer(),
                                                                                                   CloudAPI.getInstance()
                                                                                                           .getOnlinePlayer(event.getPlayer()
                                                                                                                                 .getUniqueId()),
                                                                                                   ProxiedPlayerFallbackEvent.FallbackType.SERVER_KICK,
                                                                                                   fallback);

            ProxyServer.getInstance().getPluginManager().callEvent(proxiedPlayerFallbackEvent);
            fallback = proxiedPlayerFallbackEvent.getFallback();

            if (fallback != null) {
                event.setTarget(ProxyServer.getInstance().getServerInfo(fallback));

                CloudAPI.getInstance()
                        .getNetworkConnection()
                        .getChannel()
                        .writeAndFlush(new PacketOutCustomSubChannelMessage(DefaultType.BUKKIT,
                                                                            event.getTarget().getName(),
                                                                            "cloudnet_internal",
                                                                            "server_connect_request",
                                                                            new Document("uniqueId", event.getPlayer().getUniqueId())));
                NetworkUtils.sleepUninterruptedly(25);
            } else {
                event.setCancelled(true);
            }
        } else {
            CloudAPI.getInstance()
                    .getNetworkConnection()
                    .getChannel()
                    .writeAndFlush(new PacketOutCustomSubChannelMessage(DefaultType.BUKKIT,
                                                                        event.getTarget().getName(),
                                                                        "cloudnet_internal",
                                                                        "server_connect_request",
                                                                        new Document("uniqueId", event.getPlayer().getUniqueId())));
            NetworkUtils.sleepUninterruptedly(25);
        }
    }

    @EventHandler
    public void handleServerKick(ServerKickEvent e) {
        if (e.getCancelServer() != null) {
            ServerInfo serverInfo = CloudProxy.getInstance().getCachedServers().get(e.getKickedFrom().getName());
            String fallback;
            if (CloudAPI.getInstance().getServerGroupData(serverInfo.getServiceId().getGroup()) != null && CloudAPI.getInstance()
                                                                                                                   .getServerGroupData(
                                                                                                                       serverInfo.getServiceId()
                                                                                                                                 .getGroup())
                                                                                                                   .isKickedForceFallback()) {
                fallback = CloudProxy.getInstance().fallbackOnEnabledKick(e.getPlayer(),
                                                                          serverInfo.getServiceId().getGroup(),
                                                                          e.getKickedFrom().getName());
            } else {
                fallback = CloudProxy.getInstance().fallback(e.getPlayer(), e.getKickedFrom().getName());
            }

            ProxiedPlayerFallbackEvent proxiedPlayerFallbackEvent = new ProxiedPlayerFallbackEvent(e.getPlayer(),
                                                                                                   CloudAPI.getInstance()
                                                                                                           .getOnlinePlayer(e.getPlayer()
                                                                                                                             .getUniqueId()),
                                                                                                   ProxiedPlayerFallbackEvent.FallbackType.SERVER_KICK,
                                                                                                   fallback);

            ProxyServer.getInstance().getPluginManager().callEvent(proxiedPlayerFallbackEvent);
            fallback = proxiedPlayerFallbackEvent.getFallback();

            if (fallback != null) {
                e.setCancelled(true);
                e.setCancelServer((ProxyServer.getInstance().getServerInfo(fallback)));
                e.getPlayer().sendMessage(e.getKickReasonComponent());
            }
        }
    }

    @EventHandler
    public void handleOnlineCountUpdate(ProxiedOnlineCountUpdateEvent e) {
        ProxyServer.getInstance().getScheduler().runAsync(CloudProxy.getInstance().getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (CloudProxy.getInstance().getProxyGroup() != null && CloudProxy.getInstance()
                                                                                  .getProxyGroup()
                                                                                  .getProxyConfig()
                                                                                  .isEnabled() && CloudProxy.getInstance()
                                                                                                            .getProxyGroup()
                                                                                                            .getProxyConfig()
                                                                                                            .getTabList()
                                                                                                            .isEnabled()) {
                    for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                        initTabHeaderFooter(proxiedPlayer);
                    }
                }
            }
        });
    }

    @EventHandler
    public void handleChannel(PluginMessageEvent pluginMessageEvent) {
        if (!(pluginMessageEvent.getReceiver() instanceof ProxiedPlayer)) {
            return;
        }
        if (pluginMessageEvent.getTag().equalsIgnoreCase("cloudnet:main")) {
            ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(pluginMessageEvent.getData());
            switch (byteArrayDataInput.readUTF().toLowerCase()) {
                case "connect":
                    List<String> servers = CloudProxy.getInstance().getServers(byteArrayDataInput.readUTF());
                    if (servers.size() == 0) {
                        return;
                    }
                    ((ProxiedPlayer) pluginMessageEvent.getReceiver()).connect(ProxyServer.getInstance()
                                                                                          .getServerInfo(servers.get(NetworkUtils.RANDOM.nextInt(
                                                                                              servers.size()))));
                    break;
                case "fallback":
                    ((ProxiedPlayer) pluginMessageEvent.getReceiver()).connect(ProxyServer.getInstance()
                                                                                          .getServerInfo(CloudProxy.getInstance()
                                                                                                                   .fallback(((ProxiedPlayer) pluginMessageEvent
                                                                                                                       .getReceiver()))));
                    break;
                case "command":
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(((ProxiedPlayer) pluginMessageEvent.getReceiver()),
                                                                                 byteArrayDataInput.readUTF());
                    break;
            }
        }
    }

}
