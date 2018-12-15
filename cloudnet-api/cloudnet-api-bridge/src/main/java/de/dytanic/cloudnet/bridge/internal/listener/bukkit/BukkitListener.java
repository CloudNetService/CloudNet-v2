/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.listener.bukkit;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitSubChannelMessageEvent;
import de.dytanic.cloudnet.bridge.internal.util.CloudPermissible;
import de.dytanic.cloudnet.bridge.internal.util.ReflectionUtil;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.utility.document.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Tareko on 17.08.2017.
 */
public final class BukkitListener implements Listener {

    private final Collection<UUID> requests = new CopyOnWriteArrayList<>(), kicks = new HashSet<>(256);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(final AsyncPlayerPreLoginEvent asyncPlayerPreLoginEvent)
    {
        for (Player all : Bukkit.getOnlinePlayers())
            if (all.getUniqueId().equals(asyncPlayerPreLoginEvent.getUniqueId()))
            {
                asyncPlayerPreLoginEvent.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('&', CloudAPI.getInstance().getCloudNetwork().getMessages().getString("server-kick-proxy-disallow")));
                return;
            }

        CloudServer.getInstance().getPlayerAndCache(asyncPlayerPreLoginEvent.getUniqueId());
    }

    @EventHandler
    public void handle(final BukkitSubChannelMessageEvent bukkitSubChannelMessageEvent)
    {
        if (bukkitSubChannelMessageEvent.getChannel().equalsIgnoreCase("cloudnet_internal") ||
                bukkitSubChannelMessageEvent.getMessage().equalsIgnoreCase("server_connect_request"))
        {
            UUID uniqueId = bukkitSubChannelMessageEvent.getDocument().getObject("uniqueId", UUID.class);
            if (uniqueId != null)
            {
                requests.add(uniqueId);
                Bukkit.getScheduler().runTaskLater(CloudServer.getInstance().getPlugin(), () -> {
                    requests.remove(uniqueId);
                }, 20L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleLast(final PlayerLoginEvent playerLoginEvent)
    {
        if (this.kicks.contains(playerLoginEvent.getPlayer().getUniqueId()))
        {
            this.kicks.remove(playerLoginEvent.getPlayer().getUniqueId());
            playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('&', CloudAPI.getInstance().getCloudNetwork().getMessages().getString("server-kick-proxy-disallow")));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(final PlayerLoginEvent playerLoginEvent)
    {
        if (CloudServer.getInstance().getCloudPlayers().containsKey(playerLoginEvent.getPlayer().getUniqueId()) && requests.contains(playerLoginEvent.getPlayer().getUniqueId()))
        {
            requests.remove(playerLoginEvent.getPlayer().getUniqueId());
            if (CloudAPI.getInstance().getPermissionPool() != null && CloudAPI.getInstance().getPermissionPool().isAvailable())
                try
                {
                    Field field;
                    Class<?> clazz = ReflectionUtil.reflectCraftClazz(".entity.CraftHumanEntity");

                    if (clazz != null) field = clazz.getDeclaredField("perm");
                    else field = Class.forName("net.glowstone.entity.GlowHumanEntity").getDeclaredField("permissions");

                    field.setAccessible(true);
                    final CloudPermissible cloudPermissible = new CloudPermissible(playerLoginEvent.getPlayer());
                    field.set(playerLoginEvent.getPlayer(), cloudPermissible);
                } catch (final Throwable throwable)
                {
                    throwable.printStackTrace();
                }

        } else
        {
            this.kicks.add(playerLoginEvent.getPlayer().getUniqueId());
            return;
        }

        if (CloudServer.getInstance().getGroupData() != null)
        {
            if (CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup()).isMaintenance())
            {
                if (!playerLoginEvent.getPlayer().hasPermission("cloudnet.group.maintenance"))
                {
                    playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('&', CloudAPI.getInstance().getCloudNetwork().getMessages().getString("server-group-maintenance-kick")));
                    return;
                }
            }

            if (CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup()).getJoinPower() > 0 && (!CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup()).getMode().equals(ServerGroupMode.LOBBY) || !CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup()).getMode().equals(ServerGroupMode.STATIC_LOBBY)))
            {
                CloudPlayer cloudPlayer = CloudServer.getInstance().getCloudPlayers().get(playerLoginEvent.getPlayer().getUniqueId());
                int joinPower = CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup()).getJoinPower();

                boolean acceptLogin = false;

                if (CloudAPI.getInstance().getPermissionPool() != null)
                {
                    for (GroupEntityData entityData : cloudPlayer.getPermissionEntity().getGroups())
                    {
                        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionGroup(entityData.getGroup());

                        if (permissionGroup != null)
                        {
                            if (permissionGroup.getJoinPower() >= joinPower)
                                acceptLogin = true;
                        }
                    }
                }

                if (playerLoginEvent.getPlayer().hasPermission("cloudnet.joinpower." + joinPower))
                    acceptLogin = true;

                if (!acceptLogin)
                {
                    CloudServer.getInstance().getCloudPlayers().remove(playerLoginEvent.getPlayer().getUniqueId());
                    playerLoginEvent.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('&', CloudAPI.getInstance().getCloudNetwork().getMessages().getString("joinpower-deny")));
                }
            }
        }
    }

    @EventHandler
    public void handle(final PlayerJoinEvent playerJoinEvent)
    {
        CloudServer.getInstance().update();

        if (CloudServer.getInstance().getGroupData() == null)
            return;

        if (CloudServer.getInstance().getPercentOfPlayerNowOnline() >= CloudServer.getInstance().getGroupData().getPercentForNewServerAutomatically() &&
                CloudServer.getInstance().getServerProcessMeta().getCustomServerDownload() == null && !CloudServer.getInstance().getGroupData().getMode().equals(ServerGroupMode.STATIC) &&
                CloudServer.getInstance().isAllowAutoStart() && CloudServer.getInstance().getGroupData().getPercentForNewServerAutomatically() > 0)
        {
            CloudAPI.getInstance().startGameServer(CloudServer.getInstance().getGroupData(), new ServerConfig(false, "null", new Document(), System.currentTimeMillis()), true, CloudServer.getInstance().getTemplate());
            CloudServer.getInstance().setAllowAutoStart(false);

            Bukkit.getScheduler().runTaskLater(CloudServer.getInstance().getPlugin(), () -> {
                CloudServer.getInstance().setAllowAutoStart(true);
            }, 6000);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(final PlayerQuitEvent playerQuitEvent)
    {
        CloudServer.getInstance().getCloudPlayers().remove(playerQuitEvent.getPlayer().getUniqueId());
        CloudServer.getInstance().updateAsync();
    }
}
