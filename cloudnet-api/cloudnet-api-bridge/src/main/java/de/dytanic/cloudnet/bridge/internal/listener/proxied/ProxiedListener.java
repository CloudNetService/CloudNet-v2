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

/**
 * Created by Tareko on 18.08.2017.
 */
public class ProxiedListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(final ProxyPingEvent proxyPingEvent)
    {
        final ProxyGroup proxyGroup = CloudAPI.getInstance().getProxyGroupData(CloudAPI.getInstance().getGroup());
        if (proxyGroup != null && proxyGroup.getProxyConfig().isEnabled())
        {
            ProxyConfig proxyConfig = proxyGroup.getProxyConfig();

            if (!proxyConfig.isMaintenance())
            {
                Motd motd = proxyConfig.getMotdsLayouts().get(NetworkUtils.RANDOM.nextInt(proxyConfig.getMotdsLayouts().size()));
                proxyPingEvent.getResponse().setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', motd.getFirstLine() + "\n" + motd.getSecondLine()).replace("%proxy%", CloudAPI.getInstance().getServerId()).replace("%version%", CloudProxy.class.getPackage().getImplementationVersion()))));
            } else
                proxyPingEvent.getResponse().setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', proxyConfig.getMaintenanceMotdLayout().getFirstLine() + "\n" + proxyConfig.getMaintenanceMotdLayout().getSecondLine()).replace("%proxy%", CloudAPI.getInstance().getServerId()).replace("%version%", CloudProxy.class.getPackage().getImplementationVersion()))));

            int onlineCount = CloudAPI.getInstance().getOnlineCount();
            int max = (proxyConfig.getAutoSlot().isEnabled() ? onlineCount + proxyConfig.getAutoSlot().getDynamicSlotSize() : proxyConfig.getMaxPlayers());

            ServerPing.PlayerInfo[] playerInfos = new ServerPing.PlayerInfo[proxyConfig.getPlayerInfo().length];
            for (short i = 0; i < playerInfos.length; i++)
                playerInfos[i] = new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', proxyConfig.getPlayerInfo()[i]), UUID.randomUUID());

            proxyPingEvent.getResponse().setPlayers(new ServerPing.Players(max, onlineCount, playerInfos));

            if (proxyConfig.isMaintenance())
                proxyPingEvent.getResponse().setVersion(new ServerPing.Protocol(proxyConfig.getMaintenaceProtocol(), 1));
            proxyPingEvent.setResponse(proxyPingEvent.getResponse());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(final ServerSwitchEvent serverSwitchEvent)
    {
        CloudPlayer cloudPlayer = CloudProxy.getInstance().getCloudPlayers().get(serverSwitchEvent.getPlayer().getUniqueId());
        cloudPlayer.setServer(serverSwitchEvent.getPlayer().getServer().getInfo().getName());

        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutUpdateOnlinePlayer(cloudPlayer));

        CloudAPI.getInstance().sendCustomSubProxyMessage(
                "cloudnet_internal",
                "player_server_switch",
                new Document("player", cloudPlayer)
                        .append("server", serverSwitchEvent.getPlayer().getServer().getInfo().getName())
        );

        if (CloudProxy.getInstance().getProxyGroup() != null && CloudProxy.getInstance().getProxyGroup().getProxyConfig().isEnabled() &&
                CloudProxy.getInstance().getProxyGroup().getProxyConfig().getTabList().isEnabled())
            initTabHeaderFooter(serverSwitchEvent.getPlayer());
    }

    @EventHandler(priority = -128)
    public void handle(final LoginEvent loginEvent)
    {
        PlayerConnection playerConnection = new PlayerConnection(
                loginEvent.getConnection().getUniqueId(),
                loginEvent.getConnection().getName(), loginEvent.getConnection().getVersion(),
                loginEvent.getConnection().getAddress().getAddress().getHostAddress(),
                loginEvent.getConnection().getAddress().getPort(), loginEvent.getConnection().isOnlineMode(),
                loginEvent.getConnection().isLegacy()
        );

        Document result = CloudAPI.getInstance().getNetworkConnection().getPacketManager().sendQuery(new PacketOutPlayerLoginRequest(playerConnection),
                CloudAPI.getInstance().getNetworkConnection()).getResult();

        CloudPlayer cloudPlayer = result.getObject("player", new TypeToken<CloudPlayer>() {
        }.getType());

        if (cloudPlayer == null)
        {
            loginEvent.setCancelReason(TextComponent.fromLegacyText("§cUnverified login. Reason: §e" + (result.contains("reason") ? result.getString("reason") : "no reason defined")));
            loginEvent.setCancelled(true);
            return;
        }

        CommandSender cloudCommandSender = new CloudPlayerCommandSender(cloudPlayer);

        if (CloudProxy.getInstance().getProxyGroup() != null)
        {
            ProxyConfig proxyConfig = CloudProxy.getInstance().getProxyGroup().getProxyConfig();
            if ((proxyConfig.isEnabled() && proxyConfig.isMaintenance()))
            {
                PermissionCheckEvent permissionCheckEvent = new PermissionCheckEvent(cloudCommandSender, "cloudnet.maintenance", false);

                if (!proxyConfig.getWhitelist().contains(loginEvent.getConnection().getName()) &&
                        !proxyConfig.getWhitelist().contains(loginEvent.getConnection().getUniqueId().toString())
                        &&
                        !ProxyServer.getInstance().getPluginManager().callEvent(permissionCheckEvent).hasPermission())
                {
                    loginEvent.setCancelled(true);
                    loginEvent.setCancelReason(ChatColor.translateAlternateColorCodes('&', CloudAPI.getInstance().getCloudNetwork().getMessages().getString("kick-maintenance")));
                    return;
                }
            }
        }

        ProxyGroup proxyGroup = CloudProxy.getInstance().getProxyGroup();

        if (proxyGroup != null)
            if (proxyGroup.getProxyConfig().isEnabled())
                if (CloudAPI.getInstance().getOnlineCount() >= CloudProxy.getInstance().getProxyGroup().getProxyConfig().getMaxPlayers())
                {
                    PermissionCheckEvent permissionCheckEvent = new PermissionCheckEvent(cloudCommandSender, "cloudnet.fulljoin", false);

                    if (!ProxyServer.getInstance().getPluginManager().callEvent(permissionCheckEvent).hasPermission())
                    {
                        loginEvent.setCancelled(true);
                        loginEvent.setCancelReason(ChatColor.translateAlternateColorCodes('&', CloudAPI.getInstance().getCloudNetwork().getMessages().getString("full-join")));
                        return;
                    }
                }

        CloudProxy.getInstance().getCloudPlayers().put(cloudPlayer.getUniqueId(), cloudPlayer);
    }

    @EventHandler
    public void handle(final PostLoginEvent postLoginEvent)
    {
        CloudProxy.getInstance().update();
        CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutLoginSuccess(postLoginEvent.getPlayer().getUniqueId()));

        if (CloudProxy.getInstance().getProxyGroup().getProxyConfig().isFastConnect())
        {
            try
            {

                Field channelWrapper;
                try
                {
                    channelWrapper = UserConnection.class.getDeclaredField("ch");
                    channelWrapper.setAccessible(true);
                } catch (Exception ex)
                {
                    channelWrapper = UserConnection.class.getField("ch");
                    channelWrapper.setAccessible(true);
                }

                Field field;
                try
                {
                    field = ChannelWrapper.class.getDeclaredField("ch");
                    field.setAccessible(true);
                } catch (Exception ex)
                {
                    field = ChannelWrapper.class.getField("ch");
                    field.setAccessible(true);
                }

                Channel channel = (Channel) field.get(channelWrapper.get(postLoginEvent.getPlayer()));
                channel.pipeline().addAfter("packet-encoder", "cloudConnection", new MessageToMessageEncoder<DefinedPacket>() {
                    @Override
                    protected void encode(ChannelHandlerContext channelHandlerContext, DefinedPacket definedPacket, List<Object> out) throws Exception
                    {
                        if (definedPacket instanceof Respawn)
                        {
                            if (((Respawn) definedPacket).getDimension() != ((UserConnection) postLoginEvent.getPlayer()).getDimension())
                                ((Respawn) definedPacket).setDimension(((UserConnection) postLoginEvent.getPlayer()).getDimension());
                        }
                        out.add(definedPacket);
                    }
                });
            } catch (IllegalAccessException | NoSuchFieldException e1)
            {
                e1.printStackTrace();
            }
        }

    }

    @EventHandler
    public void handle(final ChatEvent chatEvent)
    {
        if (chatEvent.getMessage().startsWith(NetworkUtils.SLASH_STRING))
        {
            if (chatEvent.getSender() instanceof ProxiedPlayer)
                CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutCommandExecute(new PlayerCommandExecution(((ProxiedPlayer) chatEvent.getSender()).getName(), chatEvent.getMessage())));
        }
    }

    @EventHandler
    public void handle(final PermissionCheckEvent permissionCheckEvent)
    {
        if (CloudAPI.getInstance().getPermissionPool() == null || !CloudAPI.getInstance().getPermissionPool().isAvailable())
            return;

        if (permissionCheckEvent.getSender() instanceof ProxiedPlayer)
        {
            if (CloudProxy.getInstance().getCloudPlayers().containsKey(((ProxiedPlayer) permissionCheckEvent.getSender()).getUniqueId()))
                permissionCheckEvent.setHasPermission(CloudProxy.getInstance().getCloudPlayers().get(((ProxiedPlayer) permissionCheckEvent.getSender()).getUniqueId())
                        .getPermissionEntity().hasPermission(CloudAPI.getInstance().getPermissionPool(), permissionCheckEvent.getPermission(), CloudAPI.getInstance().getGroup()));
        } else if (permissionCheckEvent.getSender() instanceof CloudPlayerCommandSender)
        {
            permissionCheckEvent.setHasPermission(((CloudPlayerCommandSender) permissionCheckEvent.getSender()).getCloudPlayer()
                    .getPermissionEntity().hasPermission(CloudAPI.getInstance().getPermissionPool(), permissionCheckEvent.getPermission(), CloudAPI.getInstance().getGroup()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(final PlayerDisconnectEvent playerDisconnectEvent)
    {
        CloudPlayer cloudPlayer = CloudProxy.getInstance().getCloudPlayers().get(playerDisconnectEvent.getPlayer().getUniqueId());
        if (cloudPlayer != null)
            CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutLogoutPlayer(cloudPlayer, playerDisconnectEvent.getPlayer().getUniqueId()));
        else
            CloudAPI.getInstance().getNetworkConnection().sendPacket(new PacketOutLogoutPlayer(null, playerDisconnectEvent.getPlayer().getUniqueId()));

        CloudProxy.getInstance().getCloudPlayers().remove(playerDisconnectEvent.getPlayer().getUniqueId());
        ProxyServer.getInstance().getScheduler().schedule(CloudProxy.getInstance().getPlugin(), () -> {
                CloudProxy.getInstance().update();
        }, 250, TimeUnit.MILLISECONDS);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(final ServerConnectEvent serverConnectEvent)
    {

        if (serverConnectEvent.getPlayer().getServer() == null)
        {
            String fallback = CloudProxy.getInstance().fallback(serverConnectEvent.getPlayer());
            ProxiedPlayerFallbackEvent proxiedPlayerFallbackEvent = new ProxiedPlayerFallbackEvent(
                    serverConnectEvent.getPlayer(),
                    CloudAPI.getInstance().getOnlinePlayer(serverConnectEvent.getPlayer().getUniqueId()),
                    ProxiedPlayerFallbackEvent.FallbackType.SERVER_KICK,
                    fallback
            );

            ProxyServer.getInstance().getPluginManager().callEvent(proxiedPlayerFallbackEvent);
            fallback = proxiedPlayerFallbackEvent.getFallback();

            if (fallback != null)
            {
                serverConnectEvent.setTarget(ProxyServer.getInstance().getServerInfo(fallback));

                CloudAPI.getInstance().getNetworkConnection().getChannel().writeAndFlush(new PacketOutCustomSubChannelMessage(DefaultType.BUKKIT, serverConnectEvent.getTarget().getName(), "cloudnet_internal", "server_connect_request", new Document("uniqueId", serverConnectEvent.getPlayer().getUniqueId())));
                NetworkUtils.sleepUninterruptedly(6);
            } else
                serverConnectEvent.setCancelled(true);
        } else
        {
            CloudAPI.getInstance().getNetworkConnection().getChannel().writeAndFlush(new PacketOutCustomSubChannelMessage(DefaultType.BUKKIT, serverConnectEvent.getTarget().getName(), "cloudnet_internal", "server_connect_request", new Document("uniqueId", serverConnectEvent.getPlayer().getUniqueId())));
            NetworkUtils.sleepUninterruptedly(6);
        }
    }

    @EventHandler
    public void handle(final ServerKickEvent serverKickEvent)
    {
        if (serverKickEvent.getCancelServer() != null)
        {
            ServerInfo serverInfo = CloudProxy.getInstance().getCachedServers().get(serverKickEvent.getKickedFrom().getName());
            String fallback;
            if (CloudAPI.getInstance().getServerGroupData(serverInfo.getServiceId().getGroup()) != null && CloudAPI.getInstance().getServerGroupData(serverInfo.getServiceId().getGroup()).isKickedForceFallback())
                fallback = CloudProxy.getInstance().fallbackOnEnabledKick(serverKickEvent.getPlayer(), serverInfo.getServiceId().getGroup(), serverKickEvent.getKickedFrom().getName());
            else
                fallback = CloudProxy.getInstance().fallback(serverKickEvent.getPlayer(), serverKickEvent.getKickedFrom().getName());

            ProxiedPlayerFallbackEvent proxiedPlayerFallbackEvent = new ProxiedPlayerFallbackEvent(
                    serverKickEvent.getPlayer(),
                    CloudAPI.getInstance().getOnlinePlayer(serverKickEvent.getPlayer().getUniqueId()),
                    ProxiedPlayerFallbackEvent.FallbackType.SERVER_KICK,
                    fallback
            );

            ProxyServer.getInstance().getPluginManager().callEvent(proxiedPlayerFallbackEvent);
            fallback = proxiedPlayerFallbackEvent.getFallback();

            if (fallback != null)
            {
                serverKickEvent.setCancelled(true);
                serverKickEvent.setCancelServer((ProxyServer.getInstance().getServerInfo(fallback)));
                serverKickEvent.getPlayer().sendMessage(serverKickEvent.getKickReasonComponent());
            }
        }
    }

    @EventHandler
    public void handle(final ProxiedOnlineCountUpdateEvent proxiedOnlineCountUpdateEvent)
    {
        ProxyServer.getInstance().getScheduler().runAsync(CloudProxy.getInstance().getPlugin(), () -> {
            if (CloudProxy.getInstance().getProxyGroup() != null && CloudProxy.getInstance().getProxyGroup().getProxyConfig().isEnabled() &&
                    CloudProxy.getInstance().getProxyGroup().getProxyConfig().getTabList().isEnabled())
            {
                for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers())
                    initTabHeaderFooter(proxiedPlayer);
            }
        });
    }

    @EventHandler
    public void handle(final PluginMessageEvent pluginMessageEvent)
    {
        if (!(pluginMessageEvent.getReceiver() instanceof ProxiedPlayer))
            return;

        if (pluginMessageEvent.getTag().equals("MC|BSign") || pluginMessageEvent.getTag().equals("MC|BEdit"))
            if (CloudProxy.getInstance().getProxyGroup() != null && CloudProxy.getInstance().getProxyGroup().getProxyConfig().getCustomPayloadFixer())
            {
                pluginMessageEvent.setCancelled(true);
                return;
            }

        if (pluginMessageEvent.getTag().equalsIgnoreCase("cloudnet:main"))
        {
            ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(pluginMessageEvent.getData());
            switch (byteArrayDataInput.readUTF().toLowerCase())
            {
                case "connect":
                    List<String> servers = CloudProxy.getInstance().getServers(byteArrayDataInput.readUTF());
                    if (servers.size() == 0) return;
                    ((ProxiedPlayer) pluginMessageEvent.getReceiver()).connect(ProxyServer.getInstance().getServerInfo(servers.get(NetworkUtils.RANDOM.nextInt(servers.size()))));
                    break;
                case "fallback":
                    ((ProxiedPlayer) pluginMessageEvent.getReceiver()).connect(ProxyServer.getInstance()
                            .getServerInfo(CloudProxy.getInstance()
                                    .fallback(((ProxiedPlayer) pluginMessageEvent.getReceiver()))));
                    break;
                case "command":
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(((ProxiedPlayer) pluginMessageEvent.getReceiver()), byteArrayDataInput.readUTF());
                    break;
            }
        }
    }

    private void initTabHeaderFooter(final ProxiedPlayer proxiedPlayer)
    {
        TabList tabList = CloudProxy.getInstance().getProxyGroup().getProxyConfig().getTabList();
        proxiedPlayer.setTabHeader(
                new TextComponent(ChatColor.translateAlternateColorCodes('&', tabList.getHeader()
                        .replace("%proxy%", CloudAPI.getInstance().getServerId())
                        .replace("%server%", (proxiedPlayer.getServer() != null ? proxiedPlayer.getServer().getInfo().getName() : CloudProxy.getInstance().getProxyGroup().getName()))
                        .replace("%online_players%", CloudAPI.getInstance().getOnlineCount() + NetworkUtils.EMPTY_STRING)
                        .replace("%max_players%", CloudProxy.getInstance().getProxyGroup().getProxyConfig().getMaxPlayers() + NetworkUtils.EMPTY_STRING)
                        .replace("%group%", (proxiedPlayer.getServer() != null && CloudProxy.getInstance().getCachedServers().containsKey(proxiedPlayer.getServer().getInfo().getName()) ? CloudProxy.getInstance().getCachedServers().get(proxiedPlayer.getServer().getInfo().getName()).getServiceId().getGroup() : "Hub"))
                        .replace("%proxy_group%", CloudProxy.getInstance().getProxyGroup().getName())
                )),
                new TextComponent(ChatColor.translateAlternateColorCodes('&', tabList.getFooter()
                        .replace("%proxy%", CloudAPI.getInstance().getServerId())
                        .replace("%server%", (proxiedPlayer.getServer() != null ? proxiedPlayer.getServer().getInfo().getName() : CloudProxy.getInstance().getProxyGroup().getName()))
                        .replace("%online_players%", CloudAPI.getInstance().getOnlineCount() + NetworkUtils.EMPTY_STRING)
                        .replace("%max_players%", CloudProxy.getInstance().getProxyGroup().getProxyConfig().getMaxPlayers() + NetworkUtils.EMPTY_STRING)
                        .replace("%group%", (proxiedPlayer.getServer() != null && CloudProxy.getInstance().getCachedServers().containsKey(proxiedPlayer.getServer().getInfo().getName()) ? CloudProxy.getInstance().getCachedServers().get(proxiedPlayer.getServer().getInfo().getName()).getServiceId().getGroup() : "Hub"))
                        .replace("%proxy_group%", CloudProxy.getInstance().getProxyGroup().getName())
                )));
    }

}