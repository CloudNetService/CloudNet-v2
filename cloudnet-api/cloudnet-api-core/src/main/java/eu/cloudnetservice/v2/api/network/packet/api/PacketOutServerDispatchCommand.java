package eu.cloudnetservice.v2.api.network.packet.api;

import eu.cloudnetservice.v2.lib.DefaultType;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutServerDispatchCommand extends Packet {

    public PacketOutServerDispatchCommand(DefaultType defaultType, String serverId, String commandLine) {
        super(PacketRC.CN_CORE + 5, new Document("defaultType", defaultType).append("serverId", serverId)
                                                                            .append("commandLine", commandLine));
    }
}
