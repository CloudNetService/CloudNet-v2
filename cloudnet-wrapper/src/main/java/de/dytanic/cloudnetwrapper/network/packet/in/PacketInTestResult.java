/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 20.08.2017.
 */
public class PacketInTestResult extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        packetSender.sendPacket(new Packet(packetUniqueId,
                                           PacketRC.TEST,
                                           new Document("message",
                                                        "System.out.println(\"Hello World!\"); //Das ist perfekt für entwickler")));
    }
}
