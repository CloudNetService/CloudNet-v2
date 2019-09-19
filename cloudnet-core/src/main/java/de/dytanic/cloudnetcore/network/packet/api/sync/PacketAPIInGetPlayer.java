/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api.sync;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

import java.util.UUID;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketAPIInGetPlayer extends PacketAPIIO {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        UUID uniqueId = data.getObject("uniqueId", new TypeToken<UUID>() {}.getType());
        if (uniqueId != null && CloudNet.getInstance().getNetworkManager().getOnlinePlayers().containsKey(uniqueId)) {
            packetSender.sendPacket(getResult(new Document("player",
                                                           CloudNet.getInstance().getNetworkManager().getOnlinePlayers().get(uniqueId))));
        } else {
            packetSender.sendPacket(getResult(new Document()));
        }
    }

    @Override
    protected Packet getResult(Document value) {
        return new Packet(packetUniqueId, PacketRC.PLAYER_HANDLE, value);
    }
}
