/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.config.CloudConfigLoader;
import de.dytanic.cloudnet.api.config.ConfigTypeLoader;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitCloudServerInitEvent;
import de.dytanic.cloudnet.bridge.internal.command.bukkit.CommandCloudServer;
import de.dytanic.cloudnet.bridge.internal.command.bukkit.CommandResource;
import de.dytanic.cloudnet.bridge.internal.listener.bukkit.BukkitListener;
import de.dytanic.cloudnet.bridge.internal.serverselectors.MobSelector;
import de.dytanic.cloudnet.bridge.internal.serverselectors.SignSelector;
import de.dytanic.cloudnet.bridge.internal.serverselectors.packet.in.PacketInMobSelector;
import de.dytanic.cloudnet.bridge.internal.serverselectors.packet.in.PacketInSignSelector;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.ServerGroupMode;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
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
            SignSelector.getInstance().getWorker().stop();
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
        checkRegistryAccess();

        try {
            Field field = Class.forName("org.spigotmc.AsyncCatcher").getDeclaredField("enabled");
            field.setAccessible(true);
            field.set(null, false);
        } catch (Exception ex) {
        }

        getServer().getPluginManager().registerEvents(new BukkitListener(), this);

        CloudServer.getInstance().registerCommand(new CommandResource());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "cloudnet:main");

        enableTasks();
        loadPlayers();
    }

    private void checkRegistryAccess() {
        try {
            Class.forName("net.md_5.bungee.api.chat.BaseComponent");
            Class.forName("de.dytanic.cloudnet.bridge.internal.chat.DocumentRegistry").getMethod("fire").invoke(null);
        } catch (Exception ignored) {
        }
    }

    private void enableTasks() {
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                if (CloudServer.getInstance().getGroupData() != null) {
                    if (CloudAPI.getInstance()
                                .getServerGroupData(CloudAPI.getInstance().getGroup())
                                .getMode()
                                .equals(ServerGroupMode.LOBBY) || CloudAPI.getInstance()
                                                                          .getServerGroupData(CloudAPI.getInstance()
                                                                                                      .getGroup())
                                                                          .getMode()
                                                                          .equals(ServerGroupMode.STATIC_LOBBY)) {
                        CommandCloudServer server = new CommandCloudServer();

                        getCommand("cloudserver").setExecutor(server);
                        getCommand("cloudserver").setPermission("cloudnet.command.cloudserver");
                        getCommand("cloudserver").setTabCompleter(server);
                    }

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
                    getServer().getScheduler().runTaskTimer(BukkitBootstrap.this, new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ServerListPingEvent serverListPingEvent = new ServerListPingEvent(new InetSocketAddress("127.0.0.1",
                                                                                                                        53345).getAddress(),
                                                                                                  CloudServer.getInstance().getMotd(),
                                                                                                  Bukkit.getOnlinePlayers().size(),
                                                                                                  CloudServer.getInstance()
                                                                                                             .getMaxPlayers());
                                Bukkit.getPluginManager().callEvent(serverListPingEvent);
                                if (!serverListPingEvent.getMotd().equalsIgnoreCase(CloudServer.getInstance()
                                                                                               .getMotd()) || serverListPingEvent.getMaxPlayers() != CloudServer
                                    .getInstance()
                                    .getMaxPlayers()) {
                                    CloudServer.getInstance().setMotd(serverListPingEvent.getMotd());
                                    CloudServer.getInstance().setMaxPlayers(serverListPingEvent.getMaxPlayers());
                                    if (serverListPingEvent.getMotd().toLowerCase().contains("running") || serverListPingEvent.getMotd()
                                                                                                                              .toLowerCase()
                                                                                                                              .contains(
                                                                                                                                  "ingame")) {
                                        CloudServer.getInstance().changeToIngame();
                                    } else {
                                        CloudServer.getInstance().update();
                                    }
                                }
                            } catch (Exception ex) {
                            }
                        }
                    }, 0, 5);
                }

                if (CloudAPI.getInstance().getPermissionPool() != null && (getServer().getPluginManager()
                                                                                      .isPluginEnabled("VaultAPI") || getServer().getPluginManager()
                                                                                                                                 .isPluginEnabled(
                                                                                                                                     "Vault"))) {
                    try {
                        Class.forName("de.dytanic.cloudnet.bridge.vault.VaultInvoker").getMethod("invoke").invoke(null);
                    } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void loadPlayers() {
        for (Player all : getServer().getOnlinePlayers()) {
            CloudServer.getInstance().getPlayerAndCache(all.getUniqueId());
        }
    }

    @Deprecated
    @Override
    public void run() {
        getServer().getPluginManager().disablePlugin(this);
        Bukkit.shutdown();
    }

}
