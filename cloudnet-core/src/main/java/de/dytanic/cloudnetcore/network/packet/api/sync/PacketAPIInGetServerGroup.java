package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

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
