package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 04.10.2017.
 */
public class PacketInCreateServerLog implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        CloudNet.getInstance().getServerLogManager().append(packet.getData().getString("random"), packet.getData().getString("serverId"));
    }

}
