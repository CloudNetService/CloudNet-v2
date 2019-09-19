/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketInDispatchConsoleMessage extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        System.out.println(data.getString("output"));
    }
}
