/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;

import java.util.UUID;

/**
 * Created by Tareko on 18.08.2017.
 */
public final class PacketInLogoutPlayer extends PacketInHandlerDefault {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        CloudPlayer cloudPlayer = data.getObject("player", new TypeToken<CloudPlayer>() {}.getType());

        if (cloudPlayer != null) {
            if (CloudAPI.getInstance() != null) {
                CloudAPI.getInstance().getNetworkHandlerProvider().iterator(new Runnabled<NetworkHandler>() {
                    @Override
                    public void run(NetworkHandler obj) {
                        obj.onPlayerDisconnectNetwork(cloudPlayer);
                    }
                });
            }
        } else {
            UUID uuid = data.getObject("uniqueId", UUID.class);
            if (CloudAPI.getInstance() != null) {
                CloudAPI.getInstance().getNetworkHandlerProvider().iterator(new Runnabled<NetworkHandler>() {
                    @Override
                    public void run(NetworkHandler obj) {
                        obj.onPlayerDisconnectNetwork(uuid);
                    }
                });
            }
        }
    }
}
