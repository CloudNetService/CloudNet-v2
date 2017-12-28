/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.tools.listener;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Tareko on 26.08.2017.
 */
public class ChatListener implements Listener {

    private JavaPlugin plugin;

    public ChatListener(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleChat(AsyncPlayerChatEvent e)
    {
        PermissionGroup permissionGroup = CloudServer.getInstance().getCloudPlayers().get(e.getPlayer().getUniqueId()).getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
        String prefix = ChatColor.translateAlternateColorCodes('&',permissionGroup.getPrefix());
        String suffix = ChatColor.translateAlternateColorCodes('&', permissionGroup.getSuffix());
        String display = ChatColor.translateAlternateColorCodes('&', permissionGroup.getDisplay());

        if(e.getPlayer().hasPermission("cloudnet.chat.color"))
        {
            e.setFormat((ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("format")
                    .replace("%prefix%", prefix).replace("%player%", e.getPlayer().getName()).replace("%suffix%", suffix)
                    .replace("%display%", display).replace("%message%", e.getMessage())
                    .replace("%group%", permissionGroup.getName())
            )).replace("%", "%%"));
        }
        else
        {
            e.setFormat((plugin.getConfig().getString("format")
                    .replace("%prefix%", prefix).replace("%player%", e.getPlayer().getName()).replace("%suffix%", suffix)
                    .replace("%display%", display).replace("%message%", e.getMessage())
                    .replace("%group%", permissionGroup.getName())
            ).replace("%", "%%"));
        }
    }
}