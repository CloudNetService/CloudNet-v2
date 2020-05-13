package eu.cloudnetservice.v2.wrapper.network.packet.out;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 28.08.2017.
 */
public class PacketOutUpdateCPUUsage extends Packet {

    public PacketOutUpdateCPUUsage(double cpuUsage) {
        super(PacketRC.CN_WRAPPER + 11, new Document().append("cpuUsage", cpuUsage));
    }
}
