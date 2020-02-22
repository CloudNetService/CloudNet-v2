package de.dytanic.cloudnet.bridge.internal.listener.bukkit;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.builders.ServerProcessBuilder;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitCloudNetworkUpdateEvent;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitCloudServerInitEvent;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitSubChannelMessageEvent;
import de.dytanic.cloudnet.bridge.internal.command.bukkit.CommandCloudServer;
import de.dytanic.cloudnet.bridge.internal.util.CloudPermissible;
import de.dytanic.cloudnet.bridge.internal.util.ReflectionUtil;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.player.permission.GroupEntityData;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import de.dytanic.cloudnet.lib.server.SimpleServerGroup;
import de.dytanic.cloudnet.lib.utility.document.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Tareko on 17.08.2017.
 */
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
            if (CloudAPI.getInstance().getPermissionPool() != null && CloudAPI.getInstance().getPermissionPool().isAvailable()) {
                try {
                    Field field;
                    Class<?> clazz = ReflectionUtil.reflectCraftClazz(".entity.CraftHumanEntity");

                    if (clazz != null) {
                        field = clazz.getDeclaredField("perm");
                    } else {
                        field = Class.forName("net.glowstone.entity.GlowHumanEntity").getDeclaredField("permissions");
                    }

                    field.setAccessible(true);
                    final CloudPermissible cloudPermissible = new CloudPermissible(event.getPlayer());
                    field.set(event.getPlayer(), cloudPermissible);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

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
                CloudPlayer cloudPlayer = CloudServer.getInstance().getCloudPlayers().get(event.getPlayer().getUniqueId());
                int joinPower = serverGroupData.getJoinPower();

                boolean acceptLogin = false;

                if (CloudAPI.getInstance().getPermissionPool() != null) {
                    for (GroupEntityData entityData : cloudPlayer.getPermissionEntity().getGroups()) {
                        PermissionGroup permissionGroup = CloudAPI.getInstance().getPermissionGroup(entityData.getGroup());

                        if (permissionGroup != null) {
                            if (permissionGroup.getJoinPower() >= joinPower) {
                                acceptLogin = true;
                            }
                        }
                    }
                }

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
            CloudServer.getInstance().getServerProcessMeta().getCustomServerDownload() == null &&
            !CloudServer.getInstance().getGroupData().getMode().equals(ServerGroupMode.STATIC) &&
            CloudServer.getInstance().isAllowAutoStart() &&
            CloudServer.getInstance().getGroupData().getPercentForNewServerAutomatically() > 0) {
            ServerProcessBuilder.create(CloudAPI.getInstance().getGroup())
                                .serverConfig(new ServerConfig(false, "null", new Document(), System.currentTimeMillis()))
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
