/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.out;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.screen.ScreenInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Collection;

public class PacketOutSendScreenLine extends Packet {

    public PacketOutSendScreenLine(Collection<ScreenInfo> screenInfo) {
        super(PacketRC.CN_WRAPPER + 6, new Document("screenInfo", screenInfo));
    }
}
