package eu.cloudnetservice.v2.api.network.packet.api.sync;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Unsafe
 */
@Deprecated
public final class PacketAPIOutGetRegisteredPlayers extends Packet {

    public PacketAPIOutGetRegisteredPlayers() {
        super(PacketRC.API + 11, new Document());
    }
}
