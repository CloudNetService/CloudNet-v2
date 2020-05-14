package eu.cloudnetservice.cloudnet.v2.master.network.packet.api.sync;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.database.StatisticManager;

public final class PacketAPIInGetStatistic implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        packetSender.sendPacket(getResult(
            packet, StatisticManager.getInstance().getStatistics().toDocument()));
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), -643, value);
    }

}
