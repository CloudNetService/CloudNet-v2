/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.bridge;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.api.config.CloudConfigLoader;
import eu.cloudnetservice.cloudnet.v2.api.config.ConfigTypeLoader;
import eu.cloudnetservice.cloudnet.v2.bridge.internal.chat.DocumentRegistry;
import eu.cloudnetservice.cloudnet.v2.bridge.internal.command.bukkit.CommandResource;
import eu.cloudnetservice.cloudnet.v2.bridge.internal.listener.bukkit.BukkitListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.logging.Level;

public final class BukkitBootstrap extends JavaPlugin {

    /**
     * The cloud server instance that is constructed by this bootstrapping plugin.
     */
    private CloudServer cloudServer;
    private CloudAPI api;

    @Override
    public void onLoad() {
        try {
            api = new CloudAPI(new CloudConfigLoader(Paths.get("CLOUD", "connection.json"),
                                                     Paths.get("CLOUD", "config.json"),
                                                     ConfigTypeLoader.INTERNAL), getLogger());
        } catch (UnknownHostException exception) {
            this.getLogger().log(Level.SEVERE, "Exception instantiating CloudAPI, this is a bug!", exception);
        }
    }

    @Override
    public void onDisable() {

        getServer().getMessenger().unregisterOutgoingPluginChannel(this);

        if (api != null) {
            this.cloudServer.updateDisable();
            api.shutdown();
            api.getNetworkHandlerProvider().clear();
        }

        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        cloudServer = new CloudServer(this, api);

        api.bootstrap();
        DocumentRegistry.fire();

        getServer().getPluginManager().registerEvents(new BukkitListener(), this);

        this.cloudServer.registerCommand(new CommandResource());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "cloudnet:main");

        startUpdateTask();
        loadPlayers();
    }

    /**
     * Starts the update task for this server.
     */
    private void startUpdateTask() {
        final ServerListPingEvent serverListPingEvent = new ServerListPingEvent(
            new InetSocketAddress(0).getAddress(),
            this.cloudServer.getMotd(),
            Bukkit.getOnlinePlayers().size(),
            this.cloudServer.getMaxPlayers());
        if (serverListPingEvent.isAsynchronous()) {
            getServer().getScheduler().runTaskTimerAsynchronously(this, updateServer(serverListPingEvent), 0, 5);
        } else {
            getServer().getScheduler().runTaskTimer(this, updateServer(serverListPingEvent), 0, 5);
        }
    }

    /**
     * If players are joined on the Bukkit server prior to this plugin being enabled (ie. a reload just happened),
     * loads all players from the cloud into the cache.
     */
    private void loadPlayers() {
        for (Player all : getServer().getOnlinePlayers()) {
            api.getOnlinePlayer(all.getUniqueId());
        }
    }

    /**
     * Returns the runnable to be executed when updating the server instance.
     *
     * @param serverListPingEvent the server ping event, that will be called and used for updating
     *                            the server instance.
     *
     * @return the runnable task for a task timer.
     */
    private Runnable updateServer(final ServerListPingEvent serverListPingEvent) {
        return () -> {
            try {
                Bukkit.getPluginManager().callEvent(serverListPingEvent);
                if (!serverListPingEvent.getMotd().equalsIgnoreCase(this.cloudServer.getMotd()) ||
                    serverListPingEvent.getMaxPlayers() != this.cloudServer.getMaxPlayers()) {
                    this.cloudServer.setMotd(serverListPingEvent.getMotd());
                    this.cloudServer.setMaxPlayers(serverListPingEvent.getMaxPlayers());
                    if (serverListPingEvent.getMotd().toLowerCase().contains("running") ||
                        serverListPingEvent.getMotd().toLowerCase().contains("ingame")) {
                        this.cloudServer.changeToIngame();
                    } else {
                        this.cloudServer.update();
                    }
                }
            } catch (Exception exception) {
                this.getLogger().log(Level.SEVERE, "Error updating server! This is a bug!", exception);
            }
        };
    }

}
