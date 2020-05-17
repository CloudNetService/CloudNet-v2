package eu.cloudnetservice.cloudnet.v2.api.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.serverselectors.mob.MobConfig;
import eu.cloudnetservice.cloudnet.v2.lib.serverselectors.sign.Sign;
import eu.cloudnetservice.cloudnet.v2.lib.serverselectors.sign.SignLayoutConfig;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutInitSignsAndMobs extends Packet {

    public PacketOutInitSignsAndMobs(SignLayoutConfig signLayoutConfig, MobConfig mobConfig, Map<UUID, Sign> signs) {
        super(PacketRC.CN_CORE + 1, new Document("signLayout", signLayoutConfig).append("mobConfig", mobConfig).append("signs", signs));
    }
}
