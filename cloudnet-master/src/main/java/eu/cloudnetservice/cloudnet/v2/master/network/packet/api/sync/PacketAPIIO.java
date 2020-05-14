package eu.cloudnetservice.cloudnet.v2.master.network.packet.api.sync;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 18.08.2017.
 */
public interface PacketAPIIO extends PacketInHandler {
    Packet getResult(Packet packet, Document data);
}
