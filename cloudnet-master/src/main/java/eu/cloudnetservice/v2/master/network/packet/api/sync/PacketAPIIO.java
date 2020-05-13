package eu.cloudnetservice.v2.master.network.packet.api.sync;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 18.08.2017.
 */
public interface PacketAPIIO extends PacketInHandler {
    Packet getResult(Packet packet, Document data);
}
