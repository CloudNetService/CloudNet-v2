package eu.cloudnetservice.cloudnet.v2.master.network.packet.api.sync;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;

/**
 * Created by Tareko on 31.08.2017.
 */
public class PacketAPIInGetServer implements PacketAPIIO {

    public void handleInput(Packet packet, PacketSender packetSender) {
        MinecraftServer minecraftServer = CloudNet.getInstance().getServer(packet.getData().getString("server"));
        if (minecraftServer != null) {
            packetSender.sendPacket(getResult(packet, new Document("serverInfo", minecraftServer.getServerInfo())));
        } else {
            packetSender.sendPacket(getResult(packet, new Document()));
        }
    }

    public Packet getResult(Packet packet, Document value) {
        return new Packet(packet.getUniqueId(), PacketRC.API, value);
    }
}
