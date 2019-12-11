/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

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
public final class BukkitBootstrap extends JavaPlugin implements Runnable {

    @Override
    public void onLoad() {
        CloudAPI cloudAPI = new CloudAPI(new CloudConfigLoader(Paths.get("CLOUD/connection.json"),
                                                               Paths.get("CLOUD/config.json"),
                                                               ConfigTypeLoader.INTERNAL), this);
        cloudAPI.getNetworkConnection().getPacketManager().registerHandler(PacketRC.SERVER_SELECTORS + 1, PacketInSignSelector.class);
        cloudAPI.getNetworkConnection().getPacketManager().registerHandler(PacketRC.SERVER_SELECTORS + 2, PacketInMobSelector.class);

        cloudAPI.setLogger(getLogger());
    }

    @Override
    public void onDisable() {

        getServer().getMessenger().unregisterOutgoingPluginChannel(this);

        if (CloudAPI.getInstance() != null) {
            CloudServer.getInstance().updateDisable();
            CloudAPI.getInstance().shutdown();
        }

        CloudAPI.getInstance().getNetworkHandlerProvider().clear();

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
        new CloudServer(this, CloudAPI.getInstance());

        CloudAPI.getInstance().bootstrap();
        DocumentRegistry.fire();

        getServer().getPluginManager().registerEvents(new BukkitListener(), this);

        CloudServer.getInstance().registerCommand(new CommandResource());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "cloudnet:main");

        enableTasks();
        loadPlayers();
    }

    private void enableTasks() {
        Bukkit.getScheduler().runTask(this, () -> {
            if (CloudServer.getInstance().getGroupData() != null) {
                CommandCloudServer commandCloudServer = new CommandCloudServer();

                getCommand("cloudserver").setExecutor(commandCloudServer);
                getCommand("cloudserver").setPermission("cloudnet.command.cloudserver");
                getCommand("cloudserver").setTabCompleter(commandCloudServer);

                Bukkit.getPluginManager().callEvent(new BukkitCloudServerInitEvent(CloudServer.getInstance()));
                CloudServer.getInstance().update();

                if (CloudAPI.getInstance()
                            .getServerGroupData(CloudAPI.getInstance().getGroup())
                            .getAdvancedServerConfig()
                            .isDisableAutoSavingForWorlds()) {
                    for (World world : Bukkit.getWorlds()) {
                        world.setAutoSave(false);
                    }
                }
            }

            if (CloudServer.getInstance().getGroupData() != null) {
                getServer().getScheduler().runTaskTimer(BukkitBootstrap.this, () -> {
                    try {
                        ServerListPingEvent serverListPingEvent = new ServerListPingEvent(
                            new InetSocketAddress(0).getAddress(),
                            CloudServer.getInstance().getMotd(),
                            Bukkit.getOnlinePlayers().size(),
                            CloudServer.getInstance().getMaxPlayers());

                        Bukkit.getPluginManager().callEvent(serverListPingEvent);
                        if (!serverListPingEvent.getMotd().equalsIgnoreCase(CloudServer.getInstance().getMotd()) ||
                            serverListPingEvent.getMaxPlayers() != CloudServer.getInstance().getMaxPlayers()) {
                            CloudServer.getInstance().setMotd(serverListPingEvent.getMotd());
                            CloudServer.getInstance().setMaxPlayers(serverListPingEvent.getMaxPlayers());
                            if (serverListPingEvent.getMotd().toLowerCase().contains("running") ||
                                serverListPingEvent.getMotd().toLowerCase().contains("ingame")) {
                                CloudServer.getInstance().changeToIngame();
                            } else {
                                CloudServer.getInstance().update();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }, 0, 5);
            }

            if (CloudAPI.getInstance().getPermissionPool() != null &&
                (getServer().getPluginManager().isPluginEnabled("VaultAPI") ||
                    getServer().getPluginManager().isPluginEnabled("Vault"))) {
                try {
                    Class.forName("de.dytanic.cloudnet.bridge.vault.VaultInvoker").getMethod("invoke").invoke(null);
                } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadPlayers() {
        for (Player all : getServer().getOnlinePlayers()) {
            CloudAPI.getInstance().getOnlinePlayer(all.getUniqueId());
        }
    }

    @Deprecated
    @Override
    public void run() {
        getServer().getPluginManager().disablePlugin(this);
        Bukkit.shutdown();
    }

}
