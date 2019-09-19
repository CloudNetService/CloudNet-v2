/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.out;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public final class PacketOutUpdateWrapperInfo extends Packet {

    public PacketOutUpdateWrapperInfo() {
        super(PacketRC.CN_WRAPPER + 8,
              new Document("wrapperInfo",
                           new WrapperInfo(CloudNetWrapper.getInstance().getWrapperConfig().getWrapperId(),
                                           NetworkUtils.getHostName(),
                                           NetworkUtils.class.getPackage().getImplementationVersion(),
                                           CloudNetWrapper.RUNNING,
                                           Runtime.getRuntime().availableProcessors(),
                                           CloudNetWrapper.getInstance().getWrapperConfig().getStartPort(),
                                           CloudNetWrapper.getInstance().getWrapperConfig().getProcessQueueSize(),
                                           CloudNetWrapper.getInstance().getMaxMemory())));
    }
}
