package eu.cloudnetservice.v2.master.network.packet.dbsync;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.utility.document.Document;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.packet.api.sync.PacketAPIIO;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketDBInGetSize implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        String db = packet.getData().getString("name");
        packetSender.sendPacket(getResult(packet,
                                          new Document("size", CloudNet.getInstance().getDatabaseManager().getDatabase(db).size())));
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.DB, value);
    }
}
