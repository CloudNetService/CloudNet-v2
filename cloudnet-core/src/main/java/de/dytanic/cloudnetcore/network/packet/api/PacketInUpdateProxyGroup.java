/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInUpdateProxyGroup implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ProxyGroup proxyGroup = packet.getData().getObject("group", ProxyGroup.TYPE);
        CloudNet.getInstance().getConfig().createGroup(proxyGroup);

        try {
            CloudNet.getInstance().getConfig().load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        CloudNet.getInstance().getServerGroups().clear();
        CloudNet.getInstance().getProxyGroups().clear();

        NetworkUtils.addAll(CloudNet.getInstance().getServerGroups(),
                            CloudNet.getInstance().getConfig().getServerGroups(),
                            value -> {
                                System.out.println("Loading server group: " + value.getName());
                                return true;
                            });

        NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(),
                            CloudNet.getInstance().getConfig().getProxyGroups(),
                            value -> {
                                System.out.println("Loading proxy group: " + value.getName());
                                return true;
                            });

        CloudNet.getInstance().getNetworkManager().reload();
        CloudNet.getInstance().getNetworkManager().updateAll0();
        CloudNet.getInstance().getWrappers().values().forEach(Wrapper::updateWrapper);
    }
}
