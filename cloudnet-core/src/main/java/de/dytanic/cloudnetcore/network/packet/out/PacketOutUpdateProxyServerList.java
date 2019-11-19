/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.server.info.SimpleServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.HashMap;
import java.util.Map;

public class PacketOutUpdateProxyServerList extends Packet {

    public PacketOutUpdateProxyServerList(Map<String, ServerInfo> stringServerInfoMap) {
        super(PacketRC.SERVER_HANDLE + 1, new Document("servers", getSimpleServerMap(stringServerInfoMap)));
    }

    private static Object getSimpleServerMap(final Map<String, ServerInfo> stringServerInfoMap) {
        final Map<String, SimpleServerInfo> simpleServerMap = new HashMap<>();
        stringServerInfoMap.forEach(
            (serverId, serverInfo) -> simpleServerMap.put(serverId, serverInfo.toSimple()));
        return simpleServerMap;
    }
}
