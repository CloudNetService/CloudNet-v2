package eu.cloudnetservice.cloudnet.v2.bridge;

import eu.cloudnetservice.cloudnet.v2.api.CloudAPI;
import eu.cloudnetservice.cloudnet.v2.api.config.CloudConfigLoader;
import eu.cloudnetservice.cloudnet.v2.api.config.ConfigTypeLoader;
import eu.cloudnetservice.cloudnet.v2.bridge.internal.chat.DocumentRegistry;
import eu.cloudnetservice.cloudnet.v2.bridge.internal.command.proxied.CommandCloud;
import eu.cloudnetservice.cloudnet.v2.bridge.internal.command.proxied.CommandHub;
import eu.cloudnetservice.cloudnet.v2.bridge.internal.command.proxied.defaults.CommandIp;
import eu.cloudnetservice.cloudnet.v2.bridge.internal.listener.proxied.ProxiedListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.nio.file.Paths;
import java.util.logging.Level;

/**
 * Bootstrapping class of the proxy service.
 * This acts as the entry point into the CloudNet API.
 */
public class ProxiedBootstrap extends Plugin {

    /**
     * The constructed CloudNet API instance.
     */
    private CloudAPI api;

    /**
     * The proxy instance that is using this plugin context.
     */
    private CloudProxy cloudProxy;

    @Override
    public void onLoad() {
        this.api = new CloudAPI(new CloudConfigLoader(Paths.get("CLOUD", "connection.json"),
                                                      Paths.get("CLOUD", "config.json"),
                                                      ConfigTypeLoader.INTERNAL), this.getLogger());
        getLogger().setLevel(Level.INFO);
    }

    @Override
    public void onEnable() {

        DocumentRegistry.fire();

        getProxy().registerChannel("cloudnet:main");
        this.api.bootstrap();

        ProxyServer.getInstance().getConfig().getListeners().forEach(listenerInfo -> listenerInfo.getServerPriority().clear());

        getProxy().getPluginManager().registerListener(this, new ProxiedListener());

        getProxy().getPluginManager().registerCommand(this, new CommandHub());
        getProxy().getPluginManager().registerCommand(this, new CommandCloud());

        //Alternate Commands
        getProxy().getPluginManager().registerCommand(this, new CommandIp());

        cloudProxy = new CloudProxy(this, api);
        cloudProxy.updateAsync();

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
