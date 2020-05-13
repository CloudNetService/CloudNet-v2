package eu.cloudnetservice.v2.master.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.master.network.components.Wrapper;

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
