package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

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
