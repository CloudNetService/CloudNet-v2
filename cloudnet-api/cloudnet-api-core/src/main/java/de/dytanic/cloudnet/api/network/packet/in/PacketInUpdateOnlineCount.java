/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.in;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.handlers.NetworkHandler;
import de.dytanic.cloudnet.api.network.packet.PacketInHandlerDefault;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnet.lib.utility.threading.Runnabled;

/**
 * Created by Tareko on 18.08.2017.
 */
public class PacketInUpdateOnlineCount extends PacketInHandlerDefault {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        int online = data.getInt("onlineCount");
        CloudAPI.getInstance().getCloudNetwork().setOnlineCount(online);
        CloudAPI.getInstance().getNetworkHandlerProvider().iterator(new Runnabled<NetworkHandler>() {
            @Override
            public void run(NetworkHandler obj) {
                obj.onUpdateOnlineCount(online);
            }
        });
    }
}
