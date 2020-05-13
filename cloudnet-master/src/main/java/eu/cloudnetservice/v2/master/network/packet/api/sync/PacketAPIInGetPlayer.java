package eu.cloudnetservice.v2.master.network.packet.api.sync;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.utility.document.Document;
import eu.cloudnetservice.v2.master.CloudNet;

import java.util.UUID;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketAPIInGetPlayer implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        UUID uniqueId = packet.getData().getObject("uniqueId", UUID.class);
        if (uniqueId != null && CloudNet.getInstance().getNetworkManager().getOnlinePlayers().containsKey(uniqueId)) {
            packetSender.sendPacket(getResult(
                packet, new Document("player",
                                     CloudNet.getInstance().getNetworkManager().getOnlinePlayers().get(uniqueId))));
        } else {
            packetSender.sendPacket(getResult(packet, new Document()));
        }
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.PLAYER_HANDLE, value);
    }
}
