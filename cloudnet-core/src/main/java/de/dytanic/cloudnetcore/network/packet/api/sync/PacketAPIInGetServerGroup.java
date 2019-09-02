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
public class PacketAPIInGetServerGroup extends PacketAPIIO {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        String name = data.getString("serverGroup");
        packetSender.sendPacket(getResult(new Document("serverGroup", CloudNet.getInstance().getServerGroups().get(name))));
    }

    @Override
    protected Packet getResult(Document value) {
        return new Packet(packetUniqueId, PacketRC.SERVER_HANDLE, value);
    }
}
