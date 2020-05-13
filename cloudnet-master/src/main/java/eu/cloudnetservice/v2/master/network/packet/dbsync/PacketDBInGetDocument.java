package eu.cloudnetservice.v2.master.network.packet.dbsync;

import eu.cloudnetservice.v2.lib.database.DatabaseDocument;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.utility.document.Document;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.packet.api.sync.PacketAPIIO;

import java.util.Map;

/**
 * Created by Tareko on 25.08.2017.
 */
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
