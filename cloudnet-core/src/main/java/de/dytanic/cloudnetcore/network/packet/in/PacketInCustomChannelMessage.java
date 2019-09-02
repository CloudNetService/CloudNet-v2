/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 19.08.2017.
 */
public class PacketInCustomChannelMessage extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        CloudNet.getInstance().getNetworkManager().handleCustomChannelMessage(data.getString("channel"),
                                                                              data.getString("message"),
                                                                              data.getDocument("value"),
                                                                              packetSender);
    }
}
