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
 * Created by Tareko on 20.08.2017.
 */
public class PacketAPIInNameUUID extends PacketAPIIO {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (data.contains("uniqueId")) {
            UUID uniqueId = data.getObject("uniqueId", new TypeToken<UUID>() {}.getType());
            String name = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(uniqueId);
            packetSender.sendPacket(getResult(new Document("name", name)));
        } else {
            String name = data.getString("name");
            UUID uniqueId = CloudNet.getInstance().getDbHandlers().getNameToUUIDDatabase().get(name);
            packetSender.sendPacket(getResult(new Document("uniqueId", uniqueId)));
        }
    }

    @Override
    protected Packet getResult(Document value) {
        return new Packet(packetUniqueId, PacketRC.PLAYER_HANDLE, value);
    }
}
