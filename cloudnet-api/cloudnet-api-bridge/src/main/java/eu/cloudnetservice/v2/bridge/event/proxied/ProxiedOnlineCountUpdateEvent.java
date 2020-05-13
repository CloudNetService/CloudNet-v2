package eu.cloudnetservice.v2.bridge.event.proxied;

import net.md_5.bungee.api.plugin.Event;

/**
 * The event is called whenever the amount of players currently online changes.
 * This change can be due to a login, logout or any other connection state change.
 */
public class ProxiedOnlineCountUpdateEvent extends Event {

    private int onlineCount;

    public ProxiedOnlineCountUpdateEvent(int onlineCount) {
        this.onlineCount = onlineCount;
    }

    /**
     * @return the most recent online count.
     */
    public int getOnlineCount() {
        return onlineCount;
    }
}