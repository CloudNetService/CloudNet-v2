package eu.cloudnetservice.cloudnet.v2.master.api.event.player;

import eu.cloudnetservice.cloudnet.v2.event.Cancelable;
import eu.cloudnetservice.cloudnet.v2.event.Event;
import eu.cloudnetservice.cloudnet.v2.lib.player.PlayerConnection;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;

/**
 * Created by Tareko on 27.07.2017.
 */
public class LoginRequestEvent extends Event implements Cancelable {

    private final PlayerConnection cloudPlayerConnection;

    private final ProxyServer proxyServer;

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
