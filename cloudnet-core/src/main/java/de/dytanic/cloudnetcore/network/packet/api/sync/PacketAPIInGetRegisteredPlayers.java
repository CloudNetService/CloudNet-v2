package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

@Deprecated
public final class PacketAPIInGetRegisteredPlayers extends PacketAPIIO {

    public PacketAPIInGetRegisteredPlayers() {
    }

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        packetSender.sendPacket(getResult(new Document().append("players",
                                                                CloudNet.getInstance()
                                                                        .getDbHandlers()
                                                                        .getPlayerDatabase()
                                                                        .getRegisteredPlayers())));
    }

    @Override
    protected Packet getResult(Document value) {
        return new Packet(packetUniqueId, -5426, value);
    }
}
