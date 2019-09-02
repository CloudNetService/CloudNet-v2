/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

import java.util.Collection;
import java.util.Properties;

/**
 * Created by Tareko on 24.10.2017.
 */
public class PacketInStartCloudServer extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (!data.contains("wrapperInfo")) {
            CloudNet.getInstance().startCloudServer(data.getString("serverName"),
                                                    data.getObject("serverConfig", new TypeToken<ServerConfig>() {}.getType()),
                                                    data.getInt("memory"),
                                                    data.getBoolean("priorityStop"),
                                                    data.getObject("processPreParameters", new TypeToken<String[]>() {}.getType()),
                                                    data.getObject("plugins",
                                                                   new TypeToken<Collection<ServerInstallablePlugin>>() {}.getType()),
                                                    data.getObject("properties", new TypeToken<Properties>() {}.getType()),
                                                    data.getObject("serverGroupType", ServerGroupType.class));
        } else {
            CloudNet.getInstance().startCloudServer(CloudNet.getInstance().getWrappers().get(((WrapperInfo) data.getObject("wrapperInfo",
                                                                                                                           new TypeToken<WrapperInfo>() {}
                                                                                                                               .getType())).getServerId()),
                                                    data.getString("serverName"),
                                                    data.getObject("serverConfig", new TypeToken<ServerConfig>() {}.getType()),
                                                    data.getInt("memory"),
                                                    data.getBoolean("priorityStop"),
                                                    data.getObject("processPreParameters", new TypeToken<String[]>() {}.getType()),
                                                    data.getObject("plugins",
                                                                   new TypeToken<Collection<ServerInstallablePlugin>>() {}.getType()),
                                                    data.getObject("properties", new TypeToken<Properties>() {}.getType()),
                                                    data.getObject("serverGroupType", ServerGroupType.class));
        }
    }
}
