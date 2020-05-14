package eu.cloudnetservice.cloudnet.v2.master.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import net.md_5.bungee.config.Configuration;

/**
 * Created by Tareko on 31.08.2017.
 */
public class PacketOutUpdateWrapperProperties extends Packet {

    public PacketOutUpdateWrapperProperties(Configuration properties) {
        super(PacketRC.CN_CORE + 12, new Document("properties", properties));
    }
}
