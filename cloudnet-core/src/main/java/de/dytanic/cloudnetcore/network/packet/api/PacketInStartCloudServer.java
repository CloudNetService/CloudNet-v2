/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnetcore.CloudNet;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Properties;

/**
 * Created by Tareko on 24.10.2017.
 */
public class PacketInStartCloudServer implements PacketInHandler {

    private static final Type STRING_ARRAY_TYPE = TypeToken.getArray(String.class).getType();
    private static final Type COLLECTION_SERVER_INSTALLABLE_PLUGIN_TYPE = TypeToken.getParameterized(Collection.class,
                                                                                                     ServerInstallablePlugin.class)
                                                                                   .getType();
    private static final Type PROPERTIES_TYPE = TypeToken.get(Properties.class).getType();

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!packet.getData().contains("wrapperInfo")) {
            CloudNet.getInstance().startCloudServer(packet.getData().getString("serverName"),
                                                    packet.getData().getObject("serverConfig", ServerConfig.TYPE),
                                                    packet.getData().getInt("memory"),
                                                    packet.getData().getBoolean("priorityStop"),
                                                    packet.getData().getObject("processPreParameters", STRING_ARRAY_TYPE),
                                                    packet.getData().getObject("plugins", COLLECTION_SERVER_INSTALLABLE_PLUGIN_TYPE),
                                                    packet.getData().getObject("properties", PROPERTIES_TYPE),
                                                    packet.getData().getObject("serverGroupType", ServerGroupType.class));
        } else {
            CloudNet.getInstance().startCloudServer(
                CloudNet.getInstance()
                        .getWrappers()
                        .get(packet.getData().<WrapperInfo>getObject("wrapperInfo", WrapperInfo.TYPE).getServerId()),
                packet.getData().getString("serverName"),
                packet.getData().getObject("serverConfig", ServerConfig.TYPE),
                packet.getData().getInt("memory"),
                packet.getData().getBoolean("priorityStop"),
                packet.getData().getObject("processPreParameters", STRING_ARRAY_TYPE),
                packet.getData().getObject("plugins", COLLECTION_SERVER_INSTALLABLE_PLUGIN_TYPE),
                packet.getData().getObject("properties", PROPERTIES_TYPE),
                packet.getData().getObject("serverGroupType", ServerGroupType.class));
        }
    }
}
