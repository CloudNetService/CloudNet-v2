/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

import java.util.UUID;

/**
 * Created by Tareko on 20.07.2017.
 */
public class PacketInLogoutPlayer extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        CloudPlayer cloudPlayer = data.getObject("player", new TypeToken<CloudPlayer>() {}.getType());
        if (cloudPlayer != null) {
            CloudNet.getInstance().getNetworkManager().handlePlayerLogout(cloudPlayer);
        } else if (packetSender instanceof ProxyServer) {
            CloudNet.getInstance().getNetworkManager().handlePlayerLogout(data.getObject("uniqueId", new TypeToken<UUID>() {}.getType()),
                                                                          ((ProxyServer) packetSender));
        }
    }
}
