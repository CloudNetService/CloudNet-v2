/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.info.SimpleServerInfo;
import de.dytanic.cloudnet.lib.utility.MapWrapper;
import de.dytanic.cloudnet.lib.utility.document.Document;
import java.util.function.Function;

public class PacketOutUpdateProxyServerList extends Packet {

    public PacketOutUpdateProxyServerList(java.util.Map<String, ServerInfo> stringServerInfoMap)
    {
        super(PacketRC.SERVER_HANDLE + 1, new Document("servers", MapWrapper.transform(stringServerInfoMap, new Function<String, String>() {
            @Override
            public String apply(String key)
            {
                return key;
            }
        }, new Function<ServerInfo, SimpleServerInfo>() {
            @Override
            public SimpleServerInfo apply(ServerInfo key)
            {
                return key.toSimple();
            }
        })));
    }
}