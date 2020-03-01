package de.dytanic.cloudnet;

import de.dytanic.cloudnet.api.builders.ApiServerProcessBuilder;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitServerAddEvent;
import de.dytanic.cloudnet.bridge.event.proxied.ProxiedServerAddEvent;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import net.md_5.bungee.api.plugin.Listener;
import org.bukkit.event.EventHandler;

/**
 * Created by Tareko on 16.12.2017.
 */
public class ServerGetExample implements Listener, org.bukkit.event.Listener {

    @net.md_5.bungee.event.EventHandler
    public void handle(ProxiedServerAddEvent e) {
        if (e.getServerInfo().getServerConfig().getProperties().contains("myUUID") && e.getServerInfo()
                                                                                       .getServerConfig()
                                                                                       .getProperties()
                                                                                       .getString("myUUID")
                                                                                       .equals("test")) {
            ServerInfo serverInfo = e.getServerInfo();
            /* ... */
        }

        if (e.getServerInfo().getServiceId().getServerId().equals("Lobby-1")) {
            ServerInfo serverInfo = e.getServerInfo();
            /* ... */
        }
    }

    @EventHandler
    public void handle(BukkitServerAddEvent e) {
        /* - // - */
    }

    public void start() {
        ApiServerProcessBuilder.create("Lobby")
                               .serverConfig(new ServerConfig(true, new Document("myUUID", "test"), System.currentTimeMillis()))
                               .startServer();
    }

}
