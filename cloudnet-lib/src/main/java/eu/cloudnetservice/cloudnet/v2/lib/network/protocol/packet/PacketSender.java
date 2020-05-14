package eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet;

import eu.cloudnetservice.cloudnet.v2.lib.interfaces.Nameable;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.sender.IProtocolSender;

/**
 * Created by Tareko on 24.05.2017.
 */
public interface PacketSender extends Nameable, IProtocolSender {

    void sendPacket(Packet... packets);

    void sendPacket(Packet packet);

    void sendPacketSynchronized(Packet packet);

}
