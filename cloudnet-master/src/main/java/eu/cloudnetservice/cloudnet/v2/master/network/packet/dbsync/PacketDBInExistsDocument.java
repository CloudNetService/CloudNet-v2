package eu.cloudnetservice.cloudnet.v2.master.network.packet.dbsync;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.packet.api.sync.PacketAPIIO;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketDBInExistsDocument implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        boolean exists = CloudNet.getInstance()
                                 .getDatabaseManager()
                                 .getDatabase(packet.getData().getString("db"))
                                 .contains(packet.getData().getString("name"));
        packetSender.sendPacket(getResult(packet, new Document("exists", exists)));
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.DB, value);
    }
}
