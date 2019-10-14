package de.dytanic.cloudnet.bridge.event.bukkit;

import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import org.bukkit.event.HandlerList;

public class BukkitOfflinePlayerUpdateEvent extends BukkitCloudEvent {

    private static final HandlerList handlerList = new HandlerList();

    private OfflinePlayer offlinePlayer;

    public BukkitOfflinePlayerUpdateEvent(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
