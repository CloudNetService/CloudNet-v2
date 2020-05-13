package eu.cloudnetservice.v2.bridge.event.proxied;

import eu.cloudnetservice.v2.lib.player.CloudPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * This event is called when the proxy requests or searches for a new server for
 * a given player to connect to.
 * This event can be used to set a custom or specific server.
 */
public class ProxiedPlayerFallbackEvent extends ProxiedCloudEvent {

    private ProxiedPlayer proxiedPlayer;

    private CloudPlayer cloudPlayer;

    private FallbackType fallbackType;

    private String fallback;

    public ProxiedPlayerFallbackEvent(ProxiedPlayer proxiedPlayer, CloudPlayer cloudPlayer, FallbackType fallbackType, String fallback) {
        this.proxiedPlayer = proxiedPlayer;
        this.cloudPlayer = cloudPlayer;
        this.fallbackType = fallbackType;
        this.fallback = fallback;
    }

    /**
     * @return the server-id of the server the player should be connected to.
     */
    public String getFallback() {
        return fallback;
    }

    /**
     * @param fallback the server-id of the server the player should connect to;
     *                 set to {@code null} to prevent a player to be connected to any server.
     */
    public void setFallback(String fallback) {
        this.fallback = fallback;
    }

    /**
     * @return the player connected and registered in CloudNet that a fallback server is requested for.
     */
    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    /**
     * @return the cause of this fallback request.
     */
    public FallbackType getFallbackType() {
        return fallbackType;
    }

    /**
     * @return the player connected to this proxy server.
     */
    public ProxiedPlayer getProxiedPlayer() {
        return proxiedPlayer;
    }

    /**
     * Enumeration of causes for a fallback request event.
     */
    public enum FallbackType {

        /**
         * The player has issued the /hub command.
         */
        HUB_COMMAND,
        /**
         * The player has been kicked from the last server they were connected to.
         */
        SERVER_KICK,
        /**
         * The player has connected and has yet to be connected to a server.
         */
        SERVER_CONNECT,
        /**
         * A custom cause for a fallback request.
         */
        CUSTOM

    }

}
