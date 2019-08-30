package de.dytanic.cloudnet.lib.player.permission;

/**
 * Created by Tareko on 06.07.2017.
 */
public class PermissionFallback {

    private boolean enabled;
    private String fallback;

    public PermissionFallback(boolean enabled, String fallback) {
        this.enabled = enabled;
        this.fallback = fallback;
    }

    public String getFallback() {
        return fallback;
    }

    public boolean isEnabled() {
        return enabled;
    }
}