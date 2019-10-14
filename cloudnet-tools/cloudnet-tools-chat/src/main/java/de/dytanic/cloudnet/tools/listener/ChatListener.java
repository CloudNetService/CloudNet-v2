/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.tools.listener;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.tools.ChatPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by Tareko on 26.08.2017.
 */
public final class ChatListener implements Listener {

    private final boolean permissionService = Bukkit.getPluginManager().isPluginEnabled("CloudNetAPI");

    @EventHandler
    public void handle(AsyncPlayerChatEvent e) {
        PermissionGroup permissionGroup = permissionService ? CloudServer.getInstance()
                                                                         .getCachedPlayer(e.getPlayer().getUniqueId())
                                                                         .getPermissionEntity()
                                                                         .getHighestPermissionGroup(CloudAPI.getInstance()
                                                                                                            .getPermissionPool()) : null;

        if (permissionGroup == null) {
            return;
        }

        e.setFormat(ChatColor.translateAlternateColorCodes('&',
                                                           ChatPlugin.getPlugin(ChatPlugin.class)
                                                                     .getConfig()
                                                                     .getString("format")
                                                                     .replace("%display%",
                                                                              ChatColor.translateAlternateColorCodes('&',
                                                                                                                     permissionGroup.getDisplay()))
                                                                     .replace("%prefix%",
                                                                              ChatColor.translateAlternateColorCodes('&',
                                                                                                                     permissionGroup.getPrefix()))
                                                                     .replace("%suffix%",
                                                                              ChatColor.translateAlternateColorCodes('&',
                                                                                                                     permissionGroup.getSuffix()))
                                                                     .replace("%group%", permissionGroup.getName())
                                                                     .replace("%player%", e.getPlayer().getName())
                                                                     .replace("%message%",
                                                                              e.getPlayer().hasPermission("cloudnet.chat.color") ? ChatColor
                                                                                  .translateAlternateColorCodes('&',
                                                                                                                e.getMessage()
                                                                                                                 .replace("%",
                                                                                                                          "%%")) : ChatColor
                                                                                  .stripColor(ChatColor.translateAlternateColorCodes('&',
                                                                                                                                     e.getMessage()
                                                                                                                                      .replace(
                                                                                                                                          "%",
                                                                                                                                          "%%"))))));
    }
}
