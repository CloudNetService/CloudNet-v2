/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.listener.bukkit;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitSubChannelMessageEvent;
import de.dytanic.cloudnet.bridge.internal.util.ReflectionUtil;
import de.dytanic.cloudnet.bridge.internal.util.CloudPermissble;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Tareko on 17.08.2017.
 */
public class BukkitListener implements Listener {

    private final Collection<UUID> requests = new CopyOnWriteArrayList<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(AsyncPlayerPreLoginEvent e)
    {
        CloudServer.getInstance().getPlayerAndCache(e.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle0(AsyncPlayerPreLoginEvent e)
    {
        for(Player all : Bukkit.getOnlinePlayers())
            if(all.getUniqueId().equals(e.getUniqueId()))
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "§cYou have to connect from a internal proxy server!");
    }

    @EventHandler
    public void handle(BukkitSubChannelMessageEvent e)
    {
        if(e.getChannel().equalsIgnoreCase("cloudnet_internal") ||
                e.getMessage().equalsIgnoreCase("server_connect_request"))
        {
            UUID uniqueId = e.getDocument().getObject("uniqueId", UUID.class);
            if(uniqueId != null)
            {
                requests.add(uniqueId);
                Bukkit.getScheduler().runTaskLater(CloudServer.getInstance().getPlugin(), new Runnable() {
                    @Override
                    public void run()
                    {
                        requests.remove(uniqueId);
                    }
                }, 20L);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(PlayerLoginEvent e)
    {
        if (CloudServer.getInstance().getCloudPlayers().containsKey(e.getPlayer().getUniqueId()) && requests.contains(e.getPlayer().getUniqueId()))
        {
            requests.remove(e.getPlayer().getUniqueId());
            if (CloudAPI.getInstance().getPermissionPool() != null && CloudAPI.getInstance().getPermissionPool().isAvailable())
                try
                {
                    Field field;
                    Class<?> clazz = ReflectionUtil.reflectCraftClazz(".entity.CraftHumanEntity");

                    if(clazz != null)
                    {
                        field = clazz.getDeclaredField("perm");
                    }
                    else
                    {
                        field = Class.forName("net.glowstone.entity.GlowHumanEntity").getDeclaredField("permissions");
                    }
                    field.setAccessible(true);
                    field.set(e.getPlayer(), new CloudPermissble(e.getPlayer()));
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
        } else
        {
            e.getPlayer().kickPlayer("§cYou have to connect from a internal proxy server!");
            e.disallow(PlayerLoginEvent.Result.KICK_BANNED, "§cYou have to connect from a internal proxy server!");
            return;
        }

        if(CloudServer.getInstance().getGroupData() != null)
        {
            if (CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup()).isMaintenance())
            {
                if (!e.getPlayer().hasPermission("cloudnet.group.maintenance"))
                {
                    e.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('&', CloudAPI.getInstance().getCloudNetwork().getMessages().getString("server-group-maintenance-kick")));
                    return;
                }
            }

            if (CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup()).getJoinPower() > 0 && (!CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup()).getMode().equals(ServerGroupMode.LOBBY) || !CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup()).getMode().equals(ServerGroupMode.STATIC_LOBBY)))
            {
                CloudPlayer cloudPlayer = CloudServer.getInstance().getCloudPlayers().get(e.getPlayer().getUniqueId());
                int joinPower = CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup()).getJoinPower();
                boolean acceptLogin = false;
                for (GroupEntityData entityData : cloudPlayer.getPermissionEntity().getGroups())
                {
                    if (CloudAPI.getInstance().getPermissionGroup(entityData.getGroup()).getJoinPower() >= joinPower)
                    {
                        acceptLogin = true;
                    }
                    if (e.getPlayer().hasPermission("cloudnet.joinpower." + CloudAPI.getInstance().getPermissionGroup(entityData.getGroup()).getJoinPower()))
                    {
                        acceptLogin = true;
                    }
                }
                if (!acceptLogin)
                {
                    CloudServer.getInstance().getCloudPlayers().remove(e.getPlayer().getUniqueId());
                    e.disallow(PlayerLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('&', CloudAPI.getInstance().getCloudNetwork().getMessages().getString("joinpower-deny")));
                }
            }
        }
    }

    @EventHandler
    public void handle(PlayerJoinEvent event)
    {
        CloudServer.getInstance().update();

        if(CloudServer.getInstance().getGroupData() == null) return;

        if (CloudServer.getInstance().getPercentOfPlayerNowOnline() >= CloudServer.getInstance().getGroupData().getPercentForNewServerAutomatically() &&
                CloudServer.getInstance().getServerProcessMeta().getCustomServerDownload() == null && !CloudServer.getInstance().getGroupData().getMode().equals(ServerGroupMode.STATIC) &&
                CloudServer.getInstance().isAllowAutoStart() && CloudServer.getInstance().getGroupData().getPercentForNewServerAutomatically() > 0)
        {
            CloudAPI.getInstance().startGameServer(CloudServer.getInstance().getGroupData(), new ServerConfig(false, "null", new Document(), System.currentTimeMillis()), true, CloudServer.getInstance().getTemplate());
            CloudServer.getInstance().setAllowAutoStart(false);

            Bukkit.getScheduler().runTaskLater(CloudServer.getInstance().getPlugin(), new Runnable() {
                @Override
                public void run()
                {
                    CloudServer.getInstance().setAllowAutoStart(true);
                }
            }, 6000);
        }

    }

    @EventHandler
    public void handle(PlayerKickEvent e)
    {
        CloudServer.getInstance().getCloudPlayers().remove(e.getPlayer().getUniqueId());
        CloudServer.getInstance().updateAsync();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(PlayerQuitEvent e)
    {
        CloudServer.getInstance().getCloudPlayers().remove(e.getPlayer().getUniqueId());
        CloudServer.getInstance().updateAsync();
    }
}