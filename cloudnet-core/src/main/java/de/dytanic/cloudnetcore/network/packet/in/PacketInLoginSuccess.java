/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

import java.util.UUID;

/**
 * Created by Tareko on 05.10.2017.
 */
public class PacketInLoginSuccess extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        UUID unique = data.getObject("uniqueId", UUID.class);
        CloudPlayer cloudPlayer = CloudNet.getInstance().getNetworkManager().getWaitingPlayers().get(unique);
        if (cloudPlayer != null) {
            CloudNet.getInstance().getNetworkManager().getWaitingPlayers().remove(unique);
            CloudNet.getInstance().getNetworkManager().getOnlinePlayers().put(cloudPlayer.getUniqueId(), cloudPlayer);
        }
    }
}
