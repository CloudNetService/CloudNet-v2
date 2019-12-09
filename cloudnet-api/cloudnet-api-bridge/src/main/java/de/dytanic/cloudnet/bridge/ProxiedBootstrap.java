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
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.nio.file.Paths;
import java.util.logging.Level;

/**
 * Created by Tareko on 17.08.2017.
 */
public class ProxiedBootstrap extends Plugin {

    private CloudAPI api;
    private CloudProxy cloudProxy;

    @Override
    public void onLoad() {
        api = new CloudAPI(new CloudConfigLoader(Paths.get("CLOUD/connection.json"),
                                                 Paths.get("CLOUD/config.json"),
                                                 ConfigTypeLoader.INTERNAL),
                           () -> getProxy().stop("CloudNet-Stop!"));
        getLogger().setLevel(Level.INFO);
        api.setLogger(this.getLogger());
    }

    @Override
    public void onEnable() {

        DocumentRegistry.fire();

        getProxy().registerChannel("cloudnet:main");
        api.bootstrap();

        ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> listenerInfo.getServerPriority().clear());

        getProxy().getPluginManager().registerListener(this, new ProxiedListener());

        getProxy().getPluginManager().registerCommand(this, new CommandHub());
        getProxy().getPluginManager().registerCommand(this, new CommandCloud());

        //Alternate Commands
        getProxy().getPluginManager().registerCommand(this, new CommandIp());

        cloudProxy = new CloudProxy(this, api);
        cloudProxy.updateAsync();

        if (api.getPermissionPool() != null && api.getPermissionPool().isAvailable()) {
            getProxy().getPluginManager().registerCommand(this, new CommandPermissions());
        }

        if (cloudProxy.getProxyGroup() != null && cloudProxy.getProxyGroup().getProxyConfig().getCustomPayloadFixer()) {
            getProxy().registerChannel("MC|BSign");
            getProxy().registerChannel("MC|BEdit");
        }
    }

    @Override
    public void onDisable() {
        if (api != null) {
            api.shutdown();
        }
    }
}
