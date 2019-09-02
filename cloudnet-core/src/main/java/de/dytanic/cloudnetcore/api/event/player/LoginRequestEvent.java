/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.player;

import de.dytanic.cloudnet.event.Cancelable;
import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

/**
 * Created by Tareko on 27.07.2017.
 */
public class LoginRequestEvent extends Event implements Cancelable {

    private PlayerConnection cloudPlayerConnection;

    private ProxyServer proxyServer;

    private boolean cancelled = false;

    public LoginRequestEvent(ProxyServer proxyServer, PlayerConnection cloudPlayerConnection) {
        this.cloudPlayerConnection = cloudPlayerConnection;
        this.proxyServer = proxyServer;

    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    public PlayerConnection getCloudPlayerConnection() {
        return cloudPlayerConnection;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
