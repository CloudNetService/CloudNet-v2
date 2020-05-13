package eu.cloudnetservice.v2.master.network.packet.dbsync;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.master.CloudNet;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketDBInDeleteDocument implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        String name = packet.getData().getString("name");
        CloudNet.getInstance().getDatabaseManager().getDatabase(packet.getData().getString("db")).delete(name);
    }
}
