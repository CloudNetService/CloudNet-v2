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

package eu.cloudnetservice.cloudnet.v2.master.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.lib.DefaultType;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.cloudnet.v2.master.network.packet.out.PacketOutCustomSubChannelMessage;

public class PacketInCustomSubChannelMessage implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        DefaultType defaultType = packet.getData().getObject("defaultType", DefaultType.class);
        String channel = packet.getData().getString("channel");
        String message = packet.getData().getString("message");
        Document document = packet.getData().getDocument("value");
        if (defaultType.equals(DefaultType.BUKKIT)) {
            if (packet.getData().contains("serverId")) {
                MinecraftServer minecraftServer = CloudNet.getInstance().getServer(packet.getData().getString("serverId"));
                if (minecraftServer != null) {
                    minecraftServer.sendPacket(new PacketOutCustomSubChannelMessage(channel, message, document));
                }
            } else {
                CloudNet.getInstance().getNetworkManager().sendAll(new PacketOutCustomSubChannelMessage(channel, message, document),
                                                                   MinecraftServer.class::isInstance);
            }
        } else {
            if (packet.getData().contains("serverId")) {
                ProxyServer proxyServer = CloudNet.getInstance().getProxy(packet.getData().getString("serverId"));
                if (proxyServer != null) {
                    proxyServer.sendPacket(new PacketOutCustomSubChannelMessage(channel, message, document));
                }
            } else {
                CloudNet.getInstance().getNetworkManager().sendToProxy(new PacketOutCustomSubChannelMessage(channel, message, document));
            }
        }
    }
}
