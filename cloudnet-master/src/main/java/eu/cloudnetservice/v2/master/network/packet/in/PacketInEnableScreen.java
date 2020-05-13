package eu.cloudnetservice.v2.master.network.packet.in;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.service.ServiceId;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.Wrapper;

/**
 * Created by Tareko on 25.08.2017.
 */
public class PacketInEnableScreen implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }
        ServiceId serviceId = packet.getData().getObject("serviceId", ServiceId.TYPE);
        CloudNet.getInstance().getScreenProvider().handleEnableScreen(serviceId, (Wrapper) packetSender);
    }
}
