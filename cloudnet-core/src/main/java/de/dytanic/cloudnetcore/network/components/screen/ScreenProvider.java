/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.components.screen;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.service.ServiceId;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;

import java.util.Map;


/**
 * Created by Tareko on 20.08.2017.
 */
public class ScreenProvider {

    private Map<String, EnabledScreen> screens = NetworkUtils.newConcurrentHashMap();

    private ServiceId mainServiceId;

    public void handleEnableScreen(ServiceId serviceId, Wrapper wrapper) {
        screens.put(serviceId.getServerId(), new EnabledScreen(serviceId, wrapper));
    }

    public void handleDisableScreen(ServiceId serviceId) {
        screens.remove(serviceId.getServerId());
    }

    public void disableScreen(String server) {
        MinecraftServer minecraftServer = CloudNet.getInstance().getServer(server);
        if (minecraftServer != null) {
            minecraftServer.getWrapper().disableScreen(minecraftServer.getServerInfo());
            return;
        }

        ProxyServer proxyServer = CloudNet.getInstance().getProxy(server);
        if (proxyServer != null) {
            proxyServer.getWrapper().disableScreen(proxyServer.getProxyInfo());
        }
    }

    public Map<String, EnabledScreen> getScreens() {
        return screens;
    }

    public ServiceId getMainServiceId() {
        return mainServiceId;
    }

    public void setMainServiceId(ServiceId mainServiceId) {
        this.mainServiceId = mainServiceId;
    }
}
