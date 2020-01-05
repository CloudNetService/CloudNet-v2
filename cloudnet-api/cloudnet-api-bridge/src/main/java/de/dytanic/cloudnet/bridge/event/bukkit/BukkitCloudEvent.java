package de.dytanic.cloudnet.bridge.event.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

/**
 * This class defines an abstract Bukkit CloudNet Event.
 * All events called in context of CloudNet are derived from this class.
 */
public abstract class BukkitCloudEvent extends Event {

    /**
     * Creates a new event.
     * This event's asynchronous property is determined by whether or not is it constructed
     * on the main thread.
     */
    public BukkitCloudEvent() {
        super(!Bukkit.isPrimaryThread());
    }

}
