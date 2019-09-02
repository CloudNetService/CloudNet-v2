package de.dytanic.cloudnet.api.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Unsafe
 */
@Deprecated
public final class PacketAPIOutGetRegisteredPlayers extends Packet {

    public PacketAPIOutGetRegisteredPlayers() {
        super(PacketRC.API + 11, new Document());
    }
}
