package eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.service.wrapper.WrapperScreen;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;

/**
 * Created by Tareko on 23.09.2017.
 */
public class PacketOutWrapperScreen extends Packet {

    public PacketOutWrapperScreen(String line) {
        super(PacketRC.CN_WRAPPER + 12,
              new Document("screen", new WrapperScreen(CloudNetWrapper.getInstance().getWrapperConfig().getWrapperId(), line)));
    }
}
