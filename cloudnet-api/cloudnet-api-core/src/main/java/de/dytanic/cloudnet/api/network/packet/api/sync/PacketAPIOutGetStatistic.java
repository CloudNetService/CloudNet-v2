package de.dytanic.cloudnet.api.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

public class PacketAPIOutGetStatistic extends Packet {

    public PacketAPIOutGetStatistic() {
        super(PacketRC.API + 10, new Document());
    }
}
