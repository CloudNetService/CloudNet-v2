package eu.cloudnetservice.v2.master.network.packet.in;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.service.ServiceId;
import eu.cloudnetservice.v2.master.CloudNet;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketInDisableScreen implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ServiceId serviceId = packet.getData().getObject("serviceId", ServiceId.TYPE);
        CloudNet.getInstance().getScreenProvider().handleDisableScreen(serviceId);
    }
}
