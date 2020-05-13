package eu.cloudnetservice.v2.master.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
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
