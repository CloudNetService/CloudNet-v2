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

package eu.cloudnetservice.cloudnet.v2.bridge.internal.listener.bukkit;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.api.builders.ApiServerProcessBuilder;
import eu.cloudnetservice.cloudnet.v2.bridge.CloudServer;
import eu.cloudnetservice.cloudnet.v2.bridge.event.bukkit.BukkitCloudNetworkUpdateEvent;
import eu.cloudnetservice.cloudnet.v2.bridge.event.bukkit.BukkitCloudServerInitEvent;
import eu.cloudnetservice.cloudnet.v2.bridge.event.bukkit.BukkitSubChannelMessageEvent;
import eu.cloudnetservice.cloudnet.v2.bridge.internal.command.bukkit.CommandCloudServer;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerConfig;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerGroupMode;
import eu.cloudnetservice.cloudnet.v2.lib.server.SimpleServerGroup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public final class BukkitListener implements Listener {

    private final List<UUID> requests = new ArrayList<>();
    private final Set<UUID> kicks = new HashSet<>();
    private boolean enabledGameServerFunctions;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle0(AsyncPlayerPreLoginEvent event) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handle0",
                                                String.format("Handling async player pre login event: %s%n", event));
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all.getUniqueId().equals(event.getUniqueId())) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                               ChatColor.translateAlternateColorCodes('&',
                                                                      CloudAPI.getInstance()
                                                                              .getCloudNetwork()
                                                                              .getMessages()
                                                                              .getString("server-kick-proxy-disallow")));
                return;
            }
        }
        CloudServer.getInstance().getCloudPlayers().put(event.getUniqueId(), CloudAPI.getInstance().getOnlinePlayer(event.getUniqueId()));
    }

    @EventHandler
    public void handle(BukkitSubChannelMessageEvent event) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handle",
                                                String.format("Handling Bukkit sub channel message event: %s%n", event));
        if (event.getChannel().equalsIgnoreCase("cloudnet_internal") || event.getMessage().equalsIgnoreCase("server_connect_request")) {
            UUID uniqueId = event.getDocument().getObject("uniqueId", UUID.class);
            if (uniqueId != null) {
                requests.add(uniqueId);
                Bukkit.getScheduler().runTaskLater(CloudServer.getInstance().getPlugin(), () -> requests.remove(uniqueId), 20L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleLast(PlayerLoginEvent event) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handleLast",
                                                String.format("Handling player login event: %s%n", event));
        if (this.kicks.contains(event.getPlayer().getUniqueId())) {
            this.kicks.remove(event.getPlayer().getUniqueId());

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                           ChatColor.translateAlternateColorCodes(
                               '&', CloudAPI.getInstance().getCloudNetwork().getMessages().getString("server-kick-proxy-disallow")));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleFirst(PlayerLoginEvent event) {
        CloudAPI.getInstance().getLogger().logp(Level.FINEST,
                                                this.getClass().getSimpleName(),
                                                "handleFirst",
                                                String.format("Handling player login event: %s%n", event));
        if (CloudServer.getInstance().getCloudPlayers().containsKey(event.getPlayer().getUniqueId()) &&
            requests.contains(event.getPlayer().getUniqueId())) {
            requests.remove(event.getPlayer().getUniqueId());

        } else {
            this.kicks.add(event.getPlayer().getUniqueId());
            return;
        }

        if (CloudServer.getInstance().getGroupData() != null) {
            final SimpleServerGroup serverGroupData = CloudAPI.getInstance().getServerGroupData(CloudAPI.getInstance().getGroup());

            if (serverGroupData.isMaintenance()) {
                if (!event.getPlayer().hasPermission("cloudnet.group.maintenance")) {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                                   ChatColor.translateAlternateColorCodes('&',
                                                                          CloudAPI.getInstance()
                                                                                  .getCloudNetwork()
                                                                                  .getMessages()
                                                                                  .getString("server-group-maintenance-kick")));
                    return;
                }
            }

            if (serverGroupData.getJoinPower() > 0 &&
                !serverGroupData.getMode().equals(ServerGroupMode.LOBBY) &&
                !serverGroupData.getMode().equals(ServerGroupMode.STATIC_LOBBY)) {
                int joinPower = serverGroupData.getJoinPower();

                boolean acceptLogin = false;

                if (event.getPlayer().hasPermission("cloudnet.joinpower." + joinPower)) {
                    acceptLogin = true;
                }

                if (!acceptLogin) {
                    CloudServer.getInstance().getCloudPlayers().remove(event.getPlayer().getUniqueId());
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                                   ChatColor.translateAlternateColorCodes('&',
                                                                          CloudAPI.getInstance()
                                                                                  .getCloudNetwork()
                                                                                  .getMessages()
                                                                                  .getString("joinpower-deny")));
                }
            }
        }
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        CloudServer.getInstance().update();

        if (CloudServer.getInstance().getGroupData() == null) {
            return;
        }

        if (CloudServer.getInstance().getPercentOfPlayerNowOnline() >= CloudServer.getInstance()
                                                                                  .getGroupData()
                                                                                  .getPercentForNewServerAutomatically() &&
            !CloudServer.getInstance().getGroupData().getMode().equals(ServerGroupMode.STATIC) &&
            CloudServer.getInstance().isAllowAutoStart() &&
            CloudServer.getInstance().getGroupData().getPercentForNewServerAutomatically() > 0) {
            ApiServerProcessBuilder.create(CloudAPI.getInstance().getGroup())
                                   .serverConfig(new ServerConfig())
                                   .template(CloudServer.getInstance().getTemplate())
                                   .startServer();
            CloudServer.getInstance().setAllowAutoStart(false);

            Bukkit.getScheduler().runTaskLater(CloudServer.getInstance().getPlugin(),
                                               () -> CloudServer.getInstance().setAllowAutoStart(true), 6000);
        }

    }

    @EventHandler
    public void handle(PlayerKickEvent e) {
        CloudServer.getInstance().getCloudPlayers().remove(e.getPlayer().getUniqueId());
        CloudServer.getInstance().updateAsync();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(PlayerQuitEvent e) {
        CloudServer.getInstance().getCloudPlayers().remove(e.getPlayer().getUniqueId());
        CloudServer.getInstance().updateAsync();
    }

    /**
     * Initializes and launches required tasks for Bukkit.
     * This method also disables auto-save on all worlds, if configured.
     *
     * @param event the event containing the cloud network necessary for making decisions.
     */
    @EventHandler
    public void enableGameServerFunctions(BukkitCloudNetworkUpdateEvent event) {

        if (this.enabledGameServerFunctions) {
            return;
        }

        CloudServer cloudServer = CloudServer.getInstance();

        final SimpleServerGroup serverGroup = event.getCloudNetwork().getServerGroups().get(CloudAPI.getInstance().getGroup());
        if (serverGroup != null) {
            CommandCloudServer commandCloudServer = new CommandCloudServer();

            final JavaPlugin plugin = CloudServer.getInstance().getPlugin();
            plugin.getCommand("cloudserver").setExecutor(commandCloudServer);
            plugin.getCommand("cloudserver").setTabCompleter(commandCloudServer);
            plugin.getCommand("cloudserver").setPermission("cloudnet.command.cloudserver");

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.getPluginManager().callEvent(new BukkitCloudServerInitEvent(cloudServer));
                cloudServer.update();
            }, 1);

            if (serverGroup.getAdvancedServerConfig().isDisableAutoSavingForWorlds()) {
                for (World world : Bukkit.getWorlds()) {
                    world.setAutoSave(false);
                }
            }
            this.enabledGameServerFunctions = true;
        }
    }
}
