package eu.cloudnetservice.v2.bridge.event.bukkit;

import eu.cloudnetservice.v2.lib.serverselectors.sign.SignLayoutConfig;
import org.bukkit.event.HandlerList;

/**
 * This event is called when the sign layouts are updated.
 */
public class BukkitUpdateSignLayoutsEvent extends BukkitCloudEvent {

    private static HandlerList handlerList = new HandlerList();

    private SignLayoutConfig signLayoutConfig;

    public BukkitUpdateSignLayoutsEvent(SignLayoutConfig signLayoutConfig) {
        this.signLayoutConfig = signLayoutConfig;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the new sign layout.
     */
    public SignLayoutConfig getSignLayoutConfig() {
        return signLayoutConfig;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
