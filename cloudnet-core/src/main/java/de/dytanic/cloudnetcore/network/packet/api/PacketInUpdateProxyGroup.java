/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ProxyGroup;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.utility.Acceptable;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;

import java.util.function.Consumer;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInUpdateProxyGroup extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        ProxyGroup proxyGroup = data.getObject("group", new TypeToken<ProxyGroup>() {}.getType());
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
                            new Acceptable<ServerGroup>() {
                                @Override
                                public boolean isAccepted(ServerGroup value) {
                                    System.out.println("Loading server group: " + value.getName());
                                    return true;
                                }
                            });

        NetworkUtils.addAll(CloudNet.getInstance().getProxyGroups(),
                            CloudNet.getInstance().getConfig().getProxyGroups(),
                            new Acceptable<ProxyGroup>() {
                                @Override
                                public boolean isAccepted(ProxyGroup value) {
                                    System.out.println("Loading proxy group: " + value.getName());
                                    return true;
                                }
                            });

        CloudNet.getInstance().getNetworkManager().reload();
        CloudNet.getInstance().getNetworkManager().updateAll0();
        CloudNet.getInstance().getWrappers().values().forEach(new Consumer<Wrapper>() {
            @Override
            public void accept(Wrapper wrapper) {
                wrapper.updateWrapper();
            }
        });

    }
}
