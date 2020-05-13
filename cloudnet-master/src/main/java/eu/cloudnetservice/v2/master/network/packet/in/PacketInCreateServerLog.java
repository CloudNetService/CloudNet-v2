package eu.cloudnetservice.v2.master.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.master.CloudNet;

/**
 * Created by Tareko on 04.10.2017.
 */
public class PacketInCreateServerLog implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        CloudNet.getInstance().getServerLogManager().append(packet.getData().getString("random"), packet.getData().getString("serverId"));
    }

}
