/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.handler;

import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

/**
 * Created by Tareko on 30.08.2017.
 */
public class CloudStopCheckHandler implements ICloudHandler {

    @Override
    public void onHandle(CloudNet cloudNet) {
        for (MinecraftServer minecraftServer : cloudNet.getServers().values()) {
            if (minecraftServer.getChannelLostTime() != 0L && minecraftServer.getChannel() == null) {
                if ((minecraftServer.getChannelLostTime() + 5000L) < System.currentTimeMillis()) {
                    minecraftServer.getWrapper().stopServer(minecraftServer);
                }
            }
        }

        for (ProxyServer minecraftServer : cloudNet.getProxys().values()) {
            if (minecraftServer.getChannelLostTime() != 0L && minecraftServer.getChannel() == null) {
                if ((minecraftServer.getChannelLostTime() + 5000L) < System.currentTimeMillis()) {
                    minecraftServer.getWrapper().stopProxy(minecraftServer);
                }
            }
        }
    }

    @Override
    public int getTicks() {
        return 100;
    }
}
