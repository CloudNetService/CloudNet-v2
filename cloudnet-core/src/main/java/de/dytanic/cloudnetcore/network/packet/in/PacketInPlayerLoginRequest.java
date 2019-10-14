/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.ProxyServer;

public final class PacketInPlayerLoginRequest extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (!(packetSender instanceof ProxyServer) && packetUniqueId != null) {
            return;
        }

        PlayerConnection playerConnection = data.getObject("playerConnection", new TypeToken<PlayerConnection>() {}.getType());
        CloudNet.getInstance().getNetworkManager().handlePlayerLoginRequest(((ProxyServer) packetSender), playerConnection, packetUniqueId);
    }
}
