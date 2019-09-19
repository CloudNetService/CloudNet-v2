package de.dytanic.cloudnet.bridge.event.proxied;

import de.dytanic.cloudnet.lib.player.CloudPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by Tareko on 23.01.2018.
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

    public String getFallback() {
        return fallback;
    }

    public void setFallback(String fallback) {
        this.fallback = fallback;
    }

    public CloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    public FallbackType getFallbackType() {
        return fallbackType;
    }

    public ProxiedPlayer getProxiedPlayer() {
        return proxiedPlayer;
    }

    public enum FallbackType {

        HUB_COMMAND,
        SERVER_KICK,
        SERVER_CONNECT,
        CUSTOM

    }

}
