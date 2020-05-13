package eu.cloudnetservice.v2.lib.network.protocol.packet;

/**
 * Created by Tareko on 18.07.2017.
 */
public interface PacketInHandler {
    void handleInput(Packet packet, PacketSender packetSender);
}
