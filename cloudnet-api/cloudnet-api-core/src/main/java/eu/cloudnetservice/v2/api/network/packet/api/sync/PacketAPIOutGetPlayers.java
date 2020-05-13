package eu.cloudnetservice.v2.api.network.packet.api.sync;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 17.08.2017.
 */
public class PacketAPIOutGetPlayers extends Packet {

    public PacketAPIOutGetPlayers() {
        super(PacketRC.API + 2, new Document());
    }
}
