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

package eu.cloudnetservice.cloudnet.v2.bridge.internal.listener.proxied;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.api.network.packet.out.*;
import eu.cloudnetservice.cloudnet.v2.bridge.CloudProxy;
import eu.cloudnetservice.cloudnet.v2.bridge.event.proxied.ProxiedOnlineCountUpdateEvent;
import eu.cloudnetservice.cloudnet.v2.bridge.event.proxied.ProxiedPlayerFallbackEvent;
import eu.cloudnetservice.cloudnet.v2.bridge.internal.util.CloudPlayerCommandSender;
import eu.cloudnetservice.cloudnet.v2.lib.DefaultType;
import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.player.CloudPlayer;
import eu.cloudnetservice.cloudnet.v2.lib.player.PlayerCommandExecution;
import eu.cloudnetservice.cloudnet.v2.lib.player.PlayerConnection;
import eu.cloudnetservice.cloudnet.v2.lib.proxylayout.Motd;
import eu.cloudnetservice.cloudnet.v2.lib.proxylayout.ProxyConfig;
import eu.cloudnetservice.cloudnet.v2.lib.proxylayout.TabList;
import eu.cloudnetservice.cloudnet.v2.lib.server.ProxyGroup;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class ProxiedListener implements Listener {

    public static final String IMPLEMENTATION_VERSION = CloudProxy.class.getPackage().getImplementationVersion();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleProxyPing(ProxyPingEvent event) {
        ProxyGroup proxyGroup = CloudAPI.getInstance().getProxyGroupData(CloudAPI.getInstance().getGroup());
        if (proxyGroup != null && proxyGroup.getProxyConfig().isEnabled()) {
            ProxyConfig proxyConfig = proxyGroup.getProxyConfig();
            ServerPing serverPing = event.getResponse();

            if (!proxyConfig.isMaintenance()) {
                Motd motd = proxyConfig.getMotdsLayouts().get(NetworkUtils.RANDOM.nextInt(proxyConfig.getMotdsLayouts().size()));
                serverPing.setDescriptionComponent(
                    new TextComponent(TextComponent.fromLegacyText(
                        ChatColor.translateAlternateColorCodes('&', motd.getFirstLine() + '\n' + motd.getSecondLine())
                                 .replace("%proxy%", CloudAPI.getInstance().getServerId())
                                 .replace("%version%", IMPLEMENTATION_VERSION))));
            } else {
                serverPing.setDescriptionComponent(
                    new TextComponent(TextComponent.fromLegacyText(
                        ChatColor.translateAlternateColorCodes(
                            '&', proxyConfig.getMaintenanceMotdLayout().getFirstLine() + '\n' +
                                proxyConfig.getMaintenanceMotdLayout().getSecondLine())
                                 .replace("%proxy%", CloudAPI.getInstance().getServerId())
                                 .replace("%version%", IMPLEMENTATION_VERSION))));
            }

            int onlineCount = CloudAPI.getInstance().getOnlineCount();
            int max = (proxyConfig.getAutoSlot().isEnabled() ? onlineCount + proxyConfig.getAutoSlot()
                                                                                        .getDynamicSlotSize() : proxyConfig.getMaxPlayers());

            ServerPing.PlayerInfo[] playerInfos = new ServerPing.PlayerInfo[proxyConfig.getPlayerInfo().size()];
            for (short i = 0; i < playerInfos.length; i++) {
                playerInfos[i] = new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', proxyConfig.getPlayerInfo().get(i)),
                                                           UUID.randomUUID());
            }
            serverPing.setPlayers(new ServerPing.Players(max, onlineCount, playerInfos));

            if (proxyConfig.isMaintenance()) {
                serverPing.setVersion(new ServerPing.Protocol(proxyConfig.getMaintenanceProtocol(), 1));
            }
            event.setResponse(serverPing);
        }
    }

    @EventHandler
    public void handlePluginMessage(PluginMessageEvent e) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handlePluginMessage",
                                                String.format("Handling plugin message event: %s%n", e));
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
                                                String.format("Handling server switch event: %s%n", e));
        CloudPlayer cloudPlayer = CloudProxy.getInstance().getCloudPlayers().get(e.getPlayer().getUniqueId());
        cloudPlayer.setServer(e.getPlayer().getServer().getInfo().getName());

        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutUpdateOnlinePlayer(cloudPlayer));

        CloudAPI.getInstance().sendCustomSubProxyMessage("cloudnet_internal",
                                                         "player_server_switch",
                                                         new Document("player", cloudPlayer)
                                                             .append("server", e.getPlayer().getServer().getInfo().getName()));

        if (CloudProxy.getInstance().getProxyGroup() != null &&
            CloudProxy.getInstance().getProxyGroup().getProxyConfig().isEnabled() &&
            CloudProxy.getInstance().getProxyGroup().getProxyConfig().getTabList().isEnabled()) {
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
                                                                                                                     .getServers()
                                                                                                                     .containsKey(
                                                                                                                         proxiedPlayer.getServer()
                                                                                                                                      .getInfo()
                                                                                                                                      .getName()) ? CloudProxy
                                                                                                                     .getInstance()
                                                                                                                     .getServers()
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
                                                                                                                     .getServers()
                                                                                                                     .containsKey(
                                                                                                                         proxiedPlayer.getServer()
                                                                                                                                      .getInfo()
                                                                                                                                      .getName()) ? CloudProxy
                                                                                                                     .getInstance()
                                                                                                                     .getServers()
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
                                                String.format("Handling login event: %s%n", e));
        final SocketAddress socketAddress = e.getConnection().getSocketAddress();
        if (!(socketAddress instanceof InetSocketAddress)) {
            e.setCancelled(true);
            return;
        }
        PlayerConnection playerConnection = new PlayerConnection(e.getConnection().getUniqueId(),
                                                                 e.getConnection().getName(),
                                                                 e.getConnection().getVersion(),
                                                                 ((InetSocketAddress) socketAddress).getAddress().getHostAddress(),
                                                                 ((InetSocketAddress) socketAddress).getPort(),
                                                                 e.getConnection().isOnlineMode(),
                                                                 e.getConnection().isLegacy());

        Document result = CloudAPI.getInstance().getNetworkConnection().getPacketManager().sendQuery(
            new PacketOutPlayerLoginRequest(playerConnection), CloudAPI.getInstance().getNetworkConnection()).getResult();

        CloudPlayer cloudPlayer = result.getObject("player", CloudPlayer.TYPE);

        if (cloudPlayer == null) {
            CloudAPI.getInstance().getLogger().finest("cloudPlayer is null!");
            e.setCancelReason(TextComponent.fromLegacyText(
                String.format("§cUnverified login. Reason: §e%s",
                              result.contains("reason") ? result.getString("reason") : "no reason defined")));
            e.setCancelled(true);
            return;
        }

        CommandSender cloudCommandSender = new CloudPlayerCommandSender(cloudPlayer);

        if (CloudProxy.getInstance().getProxyGroup() != null) {
            ProxyConfig proxyConfig = CloudProxy.getInstance().getProxyGroup().getProxyConfig();
            if ((proxyConfig.isEnabled() && proxyConfig.isMaintenance())) {
                PermissionCheckEvent permissionCheckEvent = new PermissionCheckEvent(cloudCommandSender, "cloudnet.maintenance", false);

                if (!proxyConfig.getWhitelist().contains(e.getConnection().getName()) &&
                    !proxyConfig.getWhitelist().contains(e.getConnection().getUniqueId().toString()) &&
                    !ProxyServer.getInstance().getPluginManager().callEvent(permissionCheckEvent).hasPermission()) {
                    e.setCancelled(true);
                    e.setCancelReason(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes(
                        '&', CloudAPI.getInstance().getCloudNetwork().getMessages().getString("kick-maintenance"))));
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
                        e.setCancelReason(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes(
                            '&', CloudAPI.getInstance().getCloudNetwork().getMessages().getString("full-join"))));
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
                                                String.format("Handling post login event: %s%n", e));

        CloudProxy.getInstance().updateAsync();
        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutLoginSuccess(e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void handleChat(ChatEvent e) {
        if (e.getMessage().startsWith(NetworkUtils.SLASH_STRING)) {
            if (e.getSender() instanceof ProxiedPlayer) {
                CloudAPI.getInstance().getNetworkConnection().sendPacket(
                    new PacketOutCommandExecute(
                        new PlayerCommandExecution(((ProxiedPlayer) e.getSender()).getName(), e.getMessage())));
            }
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

        // Schedule in the future in order to let the bungee cord clean up the player
        // and remove it from the player list
        ProxyServer.getInstance().getScheduler().schedule(
            CloudProxy.getInstance().getPlugin(),
            () -> CloudProxy.getInstance().update(), 100, TimeUnit.MILLISECONDS);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleServerConnect(ServerConnectEvent event) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handleServerConnect",
                                                String.format("Handling server connect event: %s%n", event));
        if (event.getPlayer().getServer() == null) {
            String fallback = CloudProxy.getInstance().fallback(event.getPlayer());
            ProxiedPlayerFallbackEvent proxiedPlayerFallbackEvent = new ProxiedPlayerFallbackEvent(
                event.getPlayer(),
                CloudAPI.getInstance().getOnlinePlayer(event.getPlayer().getUniqueId()),
                ProxiedPlayerFallbackEvent.FallbackType.SERVER_CONNECT,
                fallback);

            ProxyServer.getInstance().getPluginManager().callEvent(proxiedPlayerFallbackEvent);
            fallback = proxiedPlayerFallbackEvent.getFallback();

            if (fallback != null) {
                event.setTarget(ProxyServer.getInstance().getServerInfo(fallback));

                CloudAPI.getInstance()
                        .getNetworkConnection()
                        .sendPacket(
                            new PacketOutCustomSubChannelMessage(
                                DefaultType.BUKKIT,
                                event.getTarget().getName(),
                                "cloudnet_internal",
                                "server_connect_request",
                                new Document("uniqueId", event.getPlayer().getUniqueId())));
            } else {
                event.setCancelled(true);
            }
        } else {
            CloudAPI.getInstance()
                    .getNetworkConnection()
                    .sendPacket(
                        new PacketOutCustomSubChannelMessage(
                            DefaultType.BUKKIT,
                            event.getTarget().getName(),
                            "cloudnet_internal",
                            "server_connect_request",
                            new Document("uniqueId", event.getPlayer().getUniqueId())));
        }
    }

    @EventHandler
    public void handleServerKick(ServerKickEvent e) {
        if (e.getCancelServer() != null) {
            ServerInfo serverInfo = CloudProxy.getInstance().getServers().get(e.getKickedFrom().getName());
            String fallback;
            if (CloudAPI.getInstance().getServerGroupData(serverInfo.getServiceId().getGroup()) != null &&
                CloudAPI.getInstance().getServerGroupData(serverInfo.getServiceId().getGroup()).isKickedForceFallback()) {
                fallback = CloudProxy.getInstance().fallbackOnEnabledKick(
                    e.getPlayer(), serverInfo.getServiceId().getGroup(), e.getKickedFrom().getName());
            } else {
                fallback = CloudProxy.getInstance().fallback(e.getPlayer(), e.getKickedFrom().getName());
            }

            ProxiedPlayerFallbackEvent proxiedPlayerFallbackEvent = new ProxiedPlayerFallbackEvent(
                e.getPlayer(),
                CloudAPI.getInstance().getOnlinePlayer(e.getPlayer().getUniqueId()),
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
        ProxyServer.getInstance().getScheduler().runAsync(CloudProxy.getInstance().getPlugin(), () -> {
            if (CloudProxy.getInstance().getProxyGroup() != null &&
                CloudProxy.getInstance().getProxyGroup().getProxyConfig().isEnabled() &&
                CloudProxy.getInstance().getProxyGroup().getProxyConfig().getTabList().isEnabled()) {
                for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                    initTabHeaderFooter(proxiedPlayer);
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
            final ProxiedPlayer player = (ProxiedPlayer) pluginMessageEvent.getReceiver();
            switch (byteArrayDataInput.readUTF().toLowerCase()) {
                case "connect":
                    List<String> servers = CloudProxy.getInstance().getServers(byteArrayDataInput.readUTF());
                    if (servers.size() == 0) {
                        return;
                    }
                    player.connect(ProxyServer.getInstance().getServerInfo(servers.get(NetworkUtils.RANDOM.nextInt(servers.size()))));
                    break;
                case "fallback":
                    player.connect(ProxyServer.getInstance().getServerInfo(CloudProxy.getInstance().fallback(player)));
                    break;
                case "command":
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(player, byteArrayDataInput.readUTF());
                    break;
            }
        }
    }

}
