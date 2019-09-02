/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.OfflinePlayer;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

import java.util.UUID;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketAPIInGetOfflinePlayer extends PacketAPIIO {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (data.contains("uniqueId")) {
            UUID uniqueId = data.getObject("uniqueId", UUID.class);

            OfflinePlayer offlinePlayer = CloudNet.getInstance()
                                                  .getNetworkManager()
                                                  .getOnlinePlayer(uniqueId); //use cache for offline player instance

            if (offlinePlayer == null) {
                offlinePlayer = CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getPlayer(uniqueId);
            }

            packetSender.sendPacket(getResult(new Document("player", offlinePlayer)));
        } else {
            String name = data.getString("name");

            OfflinePlayer offlinePlayer = CloudNet.getInstance()
                                                  .getNetworkManager()
                                                  .getPlayer(name); //use cache for offline player instance

            if (offlinePlayer == null) {
                offlinePlayer = CloudNet.getInstance().getDbHandlers().getPlayerDatabase().getPlayer(CloudNet.getInstance()
                                                                                                             .getDbHandlers()
                                                                                                             .getNameToUUIDDatabase()
                                                                                                             .get(name));
            }

            packetSender.sendPacket(getResult(new Document("player", offlinePlayer)));
        }
    }

    @Override
    protected Packet getResult(Document value) {
        return new Packet(packetUniqueId, PacketRC.PLAYER_HANDLE, value);
    }
}
