/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.api;

import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroupType;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Collection;
import java.util.Properties;

/**
 * Created by Tareko on 24.10.2017.
 */
public class PacketOutStartCloudServer extends Packet {

    public PacketOutStartCloudServer(String serverName,
                                     ServerConfig serverConfig,
                                     int memory,
                                     boolean priorityStop,
                                     String[] processPreParameters,
                                     Collection<ServerInstallablePlugin> plugins,
                                     Properties properties,
                                     ServerGroupType serverGroupType) {
        super(PacketRC.SERVER_HANDLE + 9, new Document("serverName", serverName).append("serverConfig", serverConfig)
                                                                                .append("memory",
                                                                                        memory)
                                                                                .append("priorityStop",
                                                                                        priorityStop)
                                                                                .append("processPreParameters", processPreParameters)
                                                                                .append("plugins", plugins)
                                                                                .append("properties", properties)
                                                                                .append("serverGroupType", serverGroupType));
    }

    public PacketOutStartCloudServer(WrapperInfo wrapperInfo,
                                     String serverName,
                                     ServerConfig serverConfig,
                                     int memory,
                                     boolean priorityStop,
                                     String[] processPreParameters,
                                     Collection<ServerInstallablePlugin> plugins,
                                     Properties properties,
                                     ServerGroupType serverGroupType) {
        super(PacketRC.SERVER_HANDLE + 9, new Document("serverName", serverName).append("wrapperInfo", wrapperInfo)
                                                                                .append("serverConfig",
                                                                                        serverConfig)
                                                                                .append("memory", memory)
                                                                                .append("priorityStop", priorityStop)
                                                                                .append("processPreParameters", processPreParameters)
                                                                                .append("plugins", plugins)
                                                                                .append("properties", properties)
                                                                                .append("serverGroupType", serverGroupType));
    }
}
