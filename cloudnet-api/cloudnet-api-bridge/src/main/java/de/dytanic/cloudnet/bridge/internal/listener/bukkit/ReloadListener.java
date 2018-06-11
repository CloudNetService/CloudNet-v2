package de.dytanic.cloudnet.bridge.internal.listener.bukkit;

import de.dytanic.cloudnet.api.CloudAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ReloadListener implements Listener {


        @EventHandler
        public void onDispatch(PlayerCommandPreprocessEvent e) {
            Player p = e.getPlayer();
            if (e.getMessage().equalsIgnoreCase("/reload")) {
                p.sendMessage(CloudAPI.getInstance().getPrefix() + "The command was blocked for bug avoidance");
                e.setCancelled(true);
            } else if (e.getMessage().equalsIgnoreCase("/rl")) {
                p.sendMessage(CloudAPI.getInstance().getPrefix() + "The command was blocked for bug avoidance");
                e.setCancelled(true);
            } else if (e.getMessage().equalsIgnoreCase("/bukkit:rl")) {
                p.sendMessage(CloudAPI.getInstance().getPrefix() + "The command was blocked for bug avoidance");
                e.setCancelled(true);
            } else if (e.getMessage().equalsIgnoreCase("bukkit:reload")) {
                p.sendMessage(CloudAPI.getInstance().getPrefix() + "The command was blocked for bug avoidance");
                e.setCancelled(true);
            } else {
                e.setCancelled(false);
            }
        }
    }
