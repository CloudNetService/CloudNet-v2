package eu.cloudnetservice.v2.master.network.packet.api.sync;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.utility.document.Document;
import eu.cloudnetservice.v2.master.CloudNet;

public final class PacketAPIInGetRegisteredPlayers implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        packetSender.sendPacket(getResult(packet,
                                          new Document("players",
                                                       CloudNet.getInstance().getDbHandlers()
                                                               .getPlayerDatabase()
                                                               .getRegisteredPlayers())));
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), -5426, value);
    }
}
