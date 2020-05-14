package eu.cloudnetservice.cloudnet.v2.master.network.packet.api.sync;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketAPIInGetServerGroup implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        String name = packet.getData().getString("serverGroup");
        packetSender.sendPacket(getResult(packet, new Document("serverGroup", CloudNet.getInstance().getServerGroups().get(name))));
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.SERVER_HANDLE, value);
    }
}
