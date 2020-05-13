package eu.cloudnetservice.v2.master.network.packet.out;

import eu.cloudnetservice.v2.lib.CloudNetwork;
import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.v2.lib.utility.document.Document;

public class PacketOutCloudNetwork extends Packet {

    public PacketOutCloudNetwork(CloudNetwork cloudNetwork) {
        super(PacketRC.SERVER_HANDLE + 1, new Document("cloudnetwork", cloudNetwork));
    }
}
