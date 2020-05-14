package eu.cloudnetservice.cloudnet.v2.master.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 30.07.2017.
 */
public class PacketOutExecuteCommand extends Packet {
    public PacketOutExecuteCommand(String command) {
        super(PacketRC.CN_CORE + 9, new Document("commandLine", command));
    }
}
