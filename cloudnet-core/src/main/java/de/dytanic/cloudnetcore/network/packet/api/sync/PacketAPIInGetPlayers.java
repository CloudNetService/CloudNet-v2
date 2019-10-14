/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketAPIInGetPlayers extends PacketAPIIO {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        Packet packet = getResult(new Document().append("players", CloudNet.getInstance().getNetworkManager().getOnlinePlayers().values()));
        packetSender.sendPacket(packet);
    }

    @Override
    protected Packet getResult(Document result) {
        return new Packet(packetUniqueId, PacketRC.PLAYER_HANDLE, result);
    }
}
