/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.bridge;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.config.CloudConfigLoader;
import de.dytanic.cloudnet.api.config.ConfigTypeLoader;
import de.dytanic.cloudnet.bridge.internal.chat.DocumentRegistry;
import de.dytanic.cloudnet.bridge.internal.command.proxied.CommandCloud;
import de.dytanic.cloudnet.bridge.internal.command.proxied.CommandHub;
import de.dytanic.cloudnet.bridge.internal.command.proxied.CommandPermissions;
import de.dytanic.cloudnet.bridge.internal.command.proxied.defaults.CommandIp;
import de.dytanic.cloudnet.bridge.internal.listener.proxied.ProxiedListener;
import de.dytanic.cloudnet.lib.utility.CollectionWrapper;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by Tareko on 17.08.2017.
 */
public class ProxiedBootstrap extends Plugin {

    @Override
    public void onLoad() {
        new CloudAPI(new CloudConfigLoader(Paths.get("CLOUD/connection.json"), Paths.get("CLOUD/config.json"), ConfigTypeLoader.INTERNAL),
                     new Runnable() {
                         @Override
                         public void run() {
                             getProxy().stop("CloudNet-Stop!");
                         }
                     });
        getLogger().setLevel(Level.INFO);
        CloudAPI.getInstance().setLogger(getLogger());
    }

    @Override
    public void onEnable() {

        DocumentRegistry.fire();

        getProxy().registerChannel("cloudnet:main");
        CloudAPI.getInstance().bootstrap();

        CollectionWrapper.iterator(ProxyServer.getInstance().getConfig().getListeners(), new Runnabled<ListenerInfo>() {
            @Override
            public void run(ListenerInfo obj) {
                obj.getServerPriority().clear();
            }
        });

        getProxy().getPluginManager().registerListener(this, new ProxiedListener());

        getProxy().getPluginManager().registerCommand(this, new CommandHub());
        getProxy().getPluginManager().registerCommand(this, new CommandCloud());

        //Alternate Commands
        getProxy().getPluginManager().registerCommand(this, new CommandIp());

        new CloudProxy(this, CloudAPI.getInstance());
        CloudProxy.getInstance().updateAsync();

        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                if (CloudAPI.getInstance().getPermissionPool() != null && CloudAPI.getInstance().getPermissionPool().isAvailable()) {
                    getProxy().getPluginManager().registerCommand(ProxiedBootstrap.this, new CommandPermissions());
                }

                if (CloudProxy.getInstance().getProxyGroup() != null && CloudProxy.getInstance()
                                                                                  .getProxyGroup()
                                                                                  .getProxyConfig()
                                                                                  .getCustomPayloadFixer()) {
                    getProxy().registerChannel("MC|BSign");
                    getProxy().registerChannel("MC|BEdit");
                }
            }
        }, 1, TimeUnit.SECONDS);

        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                CloudProxy.getInstance().update();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        if (CloudAPI.getInstance() != null) {
            CloudAPI.getInstance().shutdown();
        }
    }
}
