package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.database.StatisticManager;

public final class PacketAPIInGetStatistic extends PacketAPIIO {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        Packet packet = getResult(StatisticManager.getInstance().getStatistics());
        packetSender.sendPacket(packet);
    }

    @Override
    protected Packet getResult(Document value) {
        return new Packet(packetUniqueId, -643, value);
    }

}
