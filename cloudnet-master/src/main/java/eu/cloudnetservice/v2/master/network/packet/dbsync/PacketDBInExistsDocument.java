package eu.cloudnetservice.v2.master.network.packet.dbsync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.packet.api.sync.PacketAPIIO;

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
