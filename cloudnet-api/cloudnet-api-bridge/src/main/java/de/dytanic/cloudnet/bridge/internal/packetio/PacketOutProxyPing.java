package de.dytanic.cloudnet.bridge.internal.packetio;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;

public final class PacketOutProxyPing extends Packet {

    public PacketOutProxyPing()
    {
        super(PacketRC.INTERNAL - 512, new Document());
    }
}