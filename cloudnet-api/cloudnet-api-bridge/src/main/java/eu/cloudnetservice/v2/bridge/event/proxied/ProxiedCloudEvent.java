package eu.cloudnetservice.v2.bridge.event.proxied;

import net.md_5.bungee.api.plugin.Event;

/**
 * This class defines an abstract Bungeecord CloudNet Event.
 * All events called in context of CloudNet are derived from this class.
 * These events are only ever called on proxy servers.
 * <p>
 * All events are called synchronously.
 */
public abstract class ProxiedCloudEvent extends Event {
}
