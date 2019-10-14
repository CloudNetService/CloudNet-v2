/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.tools.listener;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitPlayerUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SimpleNameTagsListener implements Listener {

    @EventHandler
    public void handleJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("SimpleNameTags"), new Runnable() {
            @Override
            public void run() {
                de.dytanic.cloudnet.bridge.CloudServer.getInstance().updateNameTags(e.getPlayer());
            }
        }, 3L);
    }

    @EventHandler
    public void handleUpdate(BukkitPlayerUpdateEvent e) {
        if (Bukkit.getPlayer(e.getCloudPlayer().getUniqueId()) != null && e.getCloudPlayer().getServer() != null && e.getCloudPlayer()
                                                                                                                     .getServer()
                                                                                                                     .equalsIgnoreCase(
                                                                                                                         CloudAPI.getInstance()
                                                                                                                                 .getServerId())) {
            de.dytanic.cloudnet.bridge.CloudServer.getInstance().updateNameTags(Bukkit.getPlayer(e.getCloudPlayer().getUniqueId()));
        }
    }
}
