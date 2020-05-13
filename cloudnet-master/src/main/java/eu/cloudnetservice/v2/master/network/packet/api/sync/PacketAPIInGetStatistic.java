package eu.cloudnetservice.v2.master.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.cloudnetservice.v2.master.database.StatisticManager;

public final class PacketAPIInGetStatistic implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        packetSender.sendPacket(getResult(
            packet, StatisticManager.getInstance().getStatistics().toDocument()));
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), -643, value);
    }

}
