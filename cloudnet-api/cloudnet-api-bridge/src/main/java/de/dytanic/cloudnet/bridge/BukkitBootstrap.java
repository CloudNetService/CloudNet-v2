package de.dytanic.cloudnet.bridge;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.config.CloudConfigLoader;
import de.dytanic.cloudnet.api.config.ConfigTypeLoader;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitCloudServerInitEvent;
import de.dytanic.cloudnet.bridge.internal.chat.DocumentRegistry;
import de.dytanic.cloudnet.bridge.internal.command.bukkit.CommandCloudServer;
import de.dytanic.cloudnet.bridge.internal.command.bukkit.CommandResource;
import de.dytanic.cloudnet.bridge.internal.listener.bukkit.BukkitListener;
import de.dytanic.cloudnet.bridge.internal.serverselectors.MobSelector;
import de.dytanic.cloudnet.bridge.internal.serverselectors.SignSelector;
import de.dytanic.cloudnet.bridge.internal.serverselectors.packet.in.PacketInMobSelector;
import de.dytanic.cloudnet.bridge.internal.serverselectors.packet.in.PacketInSignSelector;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

/**
 * Created by Tareko on 17.08.2017.
 */
public final class BukkitBootstrap extends JavaPlugin {

    /**
     * The cloud server instance that is constructed by this bootstrapping plugin.
     */
    private CloudServer cloudServer;
    private CloudAPI api;

    @Override
    public void onLoad() {
        api = new CloudAPI(new CloudConfigLoader(Paths.get("CLOUD", "connection.json"),
                                                 Paths.get("CLOUD", "config.json"),
                                                 ConfigTypeLoader.INTERNAL));
        api.getNetworkConnection().getPacketManager().registerHandler(PacketRC.SERVER_SELECTORS + 1, PacketInSignSelector.class);
        api.getNetworkConnection().getPacketManager().registerHandler(PacketRC.SERVER_SELECTORS + 2, PacketInMobSelector.class);

        api.setLogger(getLogger());
    }

    @Override
    public void onDisable() {

        getServer().getMessenger().unregisterOutgoingPluginChannel(this);

        if (api != null) {
            this.cloudServer.updateDisable();
            api.shutdown();
            api.getNetworkHandlerProvider().clear();
        }


        if (SignSelector.getInstance() != null && SignSelector.getInstance().getWorker() != null) {
            SignSelector.getInstance().getWorker().interrupt();
        }

        if (MobSelector.getInstance() != null) {
            MobSelector.getInstance().shutdown();
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

        enableTasks();
        loadPlayers();
    }

    /**
     * Initializes and launches required tasks for Bukkit.
     * This method also disables auto-save on all worlds, if configured.
     * If Vault-API or Vault is present, this method also registers the Vault services implemented by CloudNet.
     */
    private void enableTasks() {
        if (this.cloudServer.getGroupData() != null) {
            CommandCloudServer commandCloudServer = new CommandCloudServer();

            getCommand("cloudserver").setExecutor(commandCloudServer);
            getCommand("cloudserver").setTabCompleter(commandCloudServer);
            getCommand("cloudserver").setPermission("cloudnet.command.cloudserver");

            Bukkit.getScheduler().runTaskLater(this, () -> {
                Bukkit.getPluginManager().callEvent(new BukkitCloudServerInitEvent(this.cloudServer));
                this.cloudServer.update();
            }, 1);

            if (api
                .getServerGroupData(api.getGroup())
                .getAdvancedServerConfig()
                .isDisableAutoSavingForWorlds()) {
                for (World world : Bukkit.getWorlds()) {
                    world.setAutoSave(false);
                }
            }
        }

        if (this.cloudServer.getGroupData() != null) {
            startUpdateTask();
        }

        if (api.getPermissionPool() != null &&
            (getServer().getPluginManager().isPluginEnabled("VaultAPI") ||
                getServer().getPluginManager().isPluginEnabled("Vault"))) {
            try {
                Class.forName("de.dytanic.cloudnet.bridge.vault.VaultInvoker").getMethod("invoke").invoke(null);
            } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
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
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
    }

}
