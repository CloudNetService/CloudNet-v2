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

package eu.cloudnetservice.cloudnet.v2.master.network.packet.dbsync;

import eu.cloudnetservice.cloudnet.v2.lib.database.DatabaseDocument;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.packet.api.sync.PacketAPIIO;

import java.util.Map;

public class PacketDBInGetDocument implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!packet.getData().contains("name")) {
            Map<String, DatabaseDocument> docs = CloudNet.getInstance()
                                                         .getDatabaseManager()
                                                         .getDatabase(packet.getData().getString("db"))
                                                         .loadDocuments().getDocuments();
            packetSender.sendPacket(getResult(packet, new Document("docs", docs)));
        } else {
            String name = packet.getData().getString("name");
            String db = packet.getData().getString("db");
            DatabaseDocument document = CloudNet.getInstance().getDatabaseManager().getDatabase(db).getDocument(name);
            packetSender.sendPacket(getResult(packet, new Document("result", document)));
        }
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.DB, value);
    }
}
