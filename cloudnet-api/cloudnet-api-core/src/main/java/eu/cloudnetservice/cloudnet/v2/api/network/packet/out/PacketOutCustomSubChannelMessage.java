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

package eu.cloudnetservice.cloudnet.v2.api.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.DefaultType;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

public class PacketOutCustomSubChannelMessage extends Packet {

    public PacketOutCustomSubChannelMessage(DefaultType defaultType, String channel, String message, Document value) {
        super(PacketRC.SERVER_HANDLE + 8, new Document("defaultType", defaultType).append("channel", channel)
                                                                                  .append("message", message)
                                                                                  .append("value", value));
    }

    public PacketOutCustomSubChannelMessage(DefaultType defaultType, String serverId, String channel, String message, Document value) {
        super(PacketRC.SERVER_HANDLE + 8, new Document("defaultType", defaultType).append("serverId", serverId)
                                                                                  .append("channel", channel)
                                                                                  .append("message", message)
                                                                                  .append("value", value));
    }

}
