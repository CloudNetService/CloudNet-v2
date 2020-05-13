package eu.cloudnetservice.v2.bridge.event.bukkit;

import org.bukkit.event.HandlerList;

/**
 * The event is called whenever the amount of players currently online changes.
 * This change can be due to a login, logout or any other connection state change.
 */
public class BukkitOnlineCountUpdateEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private int onlineCount;

    public BukkitOnlineCountUpdateEvent(int onlineCount) {
        this.onlineCount = onlineCount;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the most recent online count.
     */
    public int getOnlineCount() {
        return onlineCount;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
