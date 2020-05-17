package eu.cloudnetservice.cloudnet.v2.master.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;

/**
 * Created by Tareko on 28.08.2017.
 */
public class PacketInUpdateCPUUsage implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }
        double cpuUsage = packet.getData().getDouble("cpuUsage");
        ((Wrapper) packetSender).setCpuUsage(cpuUsage);
    }
}
