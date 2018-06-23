package de.dytanic.cloudnet.bridge.internal.listener.bukkit;

import de.dytanic.cloudnet.api.CloudAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ReloadListener implements Listener {
    private final String[] blockedCmd = {"/reload", "/rl", "/bukkit:rl", "/bukkit:reload"};

    @EventHandler
    public void onDispatch(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (isCmdBlocked(event.getMessage())) {
            player.sendMessage(CloudAPI.getInstance().getPrefix() + "The command was blocked for bug avoidance");
            event.setCancelled(true);
        }
    }

    private boolean isCmdBlocked(String commandLine) {
        for (String blockedCmd : blockedCmd) {
            if (commandLine.toLowerCase().startsWith(blockedCmd)) {
                return true;
            }
        }
        return false;
    }
}
