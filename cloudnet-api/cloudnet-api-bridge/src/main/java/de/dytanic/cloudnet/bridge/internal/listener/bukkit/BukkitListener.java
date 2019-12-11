/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge.internal.listener.bukkit;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitSubChannelMessageEvent;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
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
import java.util.logging.Level;

/**
 * Created by Tareko on 17.08.2017.
 */
public final class BukkitListener implements Listener {

    private final Collection<UUID> requests = new CopyOnWriteArrayList<>(), kicks = new HashSet<>(256);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle0(AsyncPlayerPreLoginEvent e) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handle0",
                                                String.format("Handling async player pre login event: %s%n", e));
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all.getUniqueId().equals(e.getUniqueId())) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                           ChatColor.translateAlternateColorCodes('&',
                                                                  CloudAPI.getInstance()
                                                                          .getCloudNetwork()
                                                                          .getMessages()
                                                                          .getString("server-kick-proxy-disallow")));
                return;
            }
        }

        CloudAPI.getInstance().getOnlinePlayer(e.getUniqueId());
    }

    @EventHandler
    public void handle(BukkitSubChannelMessageEvent event) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handle",
                                                String.format("Handling Bukkit sub channel message event: %s%n", event));
        if (event.getChannel().equalsIgnoreCase("cloudnet_internal") || event.getMessage().equalsIgnoreCase("server_connect_request")) {
            UUID uniqueId = event.getDocument().getObject("uniqueId", UUID.class);
            if (uniqueId != null) {
                requests.add(uniqueId);
                Bukkit.getScheduler().runTaskLater(CloudServer.getInstance().getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        requests.remove(uniqueId);
                    }
                }, 20L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleLast(PlayerLoginEvent event) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handleLast",
                                                String.format("Handling player login event: %s%n", event));
        if (this.kicks.contains(event.getPlayer().getUniqueId())) {
            this.kicks.remove(event.getPlayer().getUniqueId());

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                           ChatColor.translateAlternateColorCodes('&',
                                                                  CloudAPI.getInstance()
                                                                          .getCloudNetwork()
                                                                          .getMessages()
                                                                          .getString("server-kick-proxy-disallow")));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleFirst(PlayerLoginEvent event) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handleFirst",
                                                String.format("Handling player login event: %s%n", event));
        if (CloudServer.getInstance().getCloudPlayers().containsKey(event.getPlayer().getUniqueId()) && requests.contains(event.getPlayer()
                                                                                                                               .getUniqueId())) {
            requests.remove(event.getPlayer().getUniqueId());


        } else {
            this.kicks.add(event.getPlayer().getUniqueId());
            return;
        }

        if (CloudServer.getInstance().getGroupData() != null) {
            if (CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup()).isMaintenance()) {
                if (!event.getPlayer().hasPermission("cloudnet.group.maintenance")) {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                                   ChatColor.translateAlternateColorCodes('&',
                                                                          CloudAPI.getInstance()
                                                                                  .getCloudNetwork()
                                                                                  .getMessages()
                                                                                  .getString("server-group-maintenance-kick")));
                    return;
                }
            }

            if (CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup()).getJoinPower() > 0 && (!CloudAPI.getInstance()
                                                                                                                             .getServerGroupData(
                                                                                                                                 CloudAPI.getInstance()
                                                                                                                                         .getGroup())
                                                                                                                             .getMode()
                                                                                                                             .equals(
                                                                                                                                 ServerGroupMode.LOBBY) || !CloudAPI
                .getInstance()
                .getServerGroupData(CloudAPI.getInstance().getGroup())
                .getMode()
                .equals(ServerGroupMode.STATIC_LOBBY))) {
                CloudPlayer cloudPlayer = CloudServer.getInstance().getCloudPlayers().get(event.getPlayer().getUniqueId());
                int joinPower = CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup()).getJoinPower();

                boolean acceptLogin = false;



                if (event.getPlayer().hasPermission("cloudnet.joinpower." + joinPower)) {
                    acceptLogin = true;
                }

                if (!acceptLogin) {
                    CloudServer.getInstance().getCloudPlayers().remove(event.getPlayer().getUniqueId());
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                                   ChatColor.translateAlternateColorCodes('&',
                                                                          CloudAPI.getInstance()
                                                                                  .getCloudNetwork()
                                                                                  .getMessages()
                                                                                  .getString("joinpower-deny")));
                }
            }
        }
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        CloudServer.getInstance().update();

        if (CloudServer.getInstance().getGroupData() == null) {
            return;
        }

        if (CloudServer.getInstance().getPercentOfPlayerNowOnline() >= CloudServer.getInstance()
                                                                                  .getGroupData()
                                                                                  .getPercentForNewServerAutomatically() && CloudServer.getInstance()
                                                                                                                                       .getServerProcessMeta()
                                                                                                                                       .getCustomServerDownload() == null && !CloudServer
            .getInstance()
            .getGroupData()
            .getMode()
            .equals(ServerGroupMode.STATIC) && CloudServer.getInstance().isAllowAutoStart() && CloudServer.getInstance()
                                                                                                          .getGroupData()
                                                                                                          .getPercentForNewServerAutomatically() > 0) {
            CloudAPI.getInstance().startGameServer(CloudServer.getInstance().getGroupData(),
                                                   new ServerConfig(false, "null", new Document(), System.currentTimeMillis()),
                                                   true,
                                                   CloudServer.getInstance().getTemplate());
            CloudServer.getInstance().setAllowAutoStart(false);

            Bukkit.getScheduler().runTaskLater(CloudServer.getInstance().getPlugin(), new Runnable() {
                @Override
                public void run() {
                    CloudServer.getInstance().setAllowAutoStart(true);
                }
            }, 6000);
        }

    }

    @EventHandler
    public void handle(PlayerKickEvent e) {
        CloudServer.getInstance().getCloudPlayers().remove(e.getPlayer().getUniqueId());
        CloudServer.getInstance().updateAsync();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(PlayerQuitEvent e) {
        CloudServer.getInstance().getCloudPlayers().remove(e.getPlayer().getUniqueId());
        CloudServer.getInstance().updateAsync();
    }
}
