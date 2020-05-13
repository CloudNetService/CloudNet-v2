package eu.cloudnetservice.v2.api.network.packet.api.sync;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

public class PacketAPIOutGetStatistic extends Packet {

    public PacketAPIOutGetStatistic() {
        super(PacketRC.API + 10, new Document());
    }
}
