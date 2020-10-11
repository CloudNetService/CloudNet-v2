/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.master.network.packet.api.sync;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PacketAPIInGetServers implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (packet.getUniqueId() == null) {
            return;
        }
        Stream<MinecraftServer> servers = CloudNet.getInstance().getServers().values().stream();
        if (packet.getData().contains("group")) {
            servers = servers.filter(server -> server.getServiceId().getGroup().equals(packet.getData().getString("group")));
        }
        List<ServerInfo> serverInfos = servers
            .map(MinecraftServer::getServerInfo)
            .collect(Collectors.toList());
        packetSender.sendPacket(getResult(packet, new Document("serverInfos", serverInfos)));
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.SERVER_HANDLE, value);
    }
}
