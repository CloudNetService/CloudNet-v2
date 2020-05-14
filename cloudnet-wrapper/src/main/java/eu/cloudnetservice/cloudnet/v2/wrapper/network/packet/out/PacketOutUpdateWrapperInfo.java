package eu.cloudnetservice.cloudnet.v2.wrapper.network.packet.out;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.network.WrapperInfo;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketRC;
import eu.cloudnetservice.cloudnet.v2.lib.utility.document.Document;
import eu.cloudnetservice.cloudnet.v2.wrapper.CloudNetWrapper;

public final class PacketOutUpdateWrapperInfo extends Packet {

    public PacketOutUpdateWrapperInfo() {
        super(PacketRC.CN_WRAPPER + 8,
              new Document("wrapperInfo",
                           new WrapperInfo(CloudNetWrapper.getInstance().getWrapperConfig().getWrapperId(),
                                           NetworkUtils.getHostName(),
                                           NetworkUtils.class.getPackage().getImplementationVersion(),
                                           true,
                                           Runtime.getRuntime().availableProcessors(),
                                           CloudNetWrapper.getInstance().getWrapperConfig().getStartPort(),
                                           CloudNetWrapper.getInstance().getWrapperConfig().getProcessQueueSize(),
                                           CloudNetWrapper.getInstance().getMaxMemory())));
    }
}
