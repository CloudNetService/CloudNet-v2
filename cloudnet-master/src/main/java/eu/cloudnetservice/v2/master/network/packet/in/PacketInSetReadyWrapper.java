package eu.cloudnetservice.v2.master.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.master.network.components.Wrapper;

public class PacketInSetReadyWrapper implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }

        boolean ready = packet.getData().getBoolean("ready");
        ((Wrapper) packetSender).setReady(ready);
    }
}