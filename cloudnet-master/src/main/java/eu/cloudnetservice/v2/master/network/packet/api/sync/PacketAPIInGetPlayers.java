package eu.cloudnetservice.v2.master.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.cloudnetservice.v2.master.CloudNet;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketAPIInGetPlayers implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        packetSender.sendPacket(getResult(
            packet,
            new Document("players", CloudNet.getInstance().getNetworkManager().getOnlinePlayers().values())));
    }

    public Packet getResult(Packet packet, Document result) {
        return new Packet(packet.getUniqueId(), PacketRC.PLAYER_HANDLE, result);
    }
}
