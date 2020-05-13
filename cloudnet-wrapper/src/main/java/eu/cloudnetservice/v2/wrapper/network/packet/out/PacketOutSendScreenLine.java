package eu.cloudnetservice.v2.wrapper.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.server.screen.ScreenInfo;
import eu.cloudnetservice.v2.lib.utility.document.Document;

import java.util.Collection;

public class PacketOutSendScreenLine extends Packet {

    public PacketOutSendScreenLine(Collection<ScreenInfo> screenInfo) {
        super(PacketRC.CN_WRAPPER + 6, new Document("screenInfo", screenInfo));
    }
}
