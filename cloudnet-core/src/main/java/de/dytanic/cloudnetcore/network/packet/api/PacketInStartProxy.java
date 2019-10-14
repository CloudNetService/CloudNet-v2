/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

import java.util.Collection;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInStartProxy extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (!data.contains("wrapper")) {
            CloudNet.getInstance().startProxy(CloudNet.getInstance().getProxyGroups().get(data.getString("group")),
                                              data.getInt("memory"),
                                              data.getObject("processParameters", new TypeToken<String[]>() {}.getType()),
                                              data.getString("url"),
                                              data.getObject("plugins", new TypeToken<Collection<ServerInstallablePlugin>>() {}.getType()),
                                              data.getDocument("properties"));
        } else {
            CloudNet.getInstance().startProxy(CloudNet.getInstance().getWrappers().get(data.getString("wrapper")),
                                              CloudNet.getInstance().getProxyGroups().get(data.getString("group")),
                                              data.getInt("memory"),
                                              data.getObject("processParameters", new TypeToken<String[]>() {}.getType()),
                                              data.getString("url"),
                                              data.getObject("plugins", new TypeToken<Collection<ServerInstallablePlugin>>() {}.getType()),
                                              data.getDocument("properties"));
        }
    }
}
