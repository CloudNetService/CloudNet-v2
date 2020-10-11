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

package eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;

public class PacketInOnlineServer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        /*
        ServerInfo serverInfo = data.getObject("serverInfo", ServerInfo.TYPE);
        GameServer gameServer = CloudNetWrapper.getInstance().getServers().get(serverInfo.getServiceId().getServerId());
        if(gameServer != null)
        CloudNetWrapper.getInstance().getServerProcessQueue().getStartups().remove(gameServer);
        */
    }
}
