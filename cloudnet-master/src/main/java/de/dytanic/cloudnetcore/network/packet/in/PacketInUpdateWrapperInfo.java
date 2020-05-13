package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnetcore.network.components.Wrapper;

public final class PacketInUpdateWrapperInfo implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }

        WrapperInfo wrapperInfo = packet.getData().getObject("wrapperInfo", WrapperInfo.TYPE);

        Wrapper wrapper = (Wrapper) packetSender;
        if (wrapper.getWrapperInfo() != null) {
            wrapper.setWrapperInfo(wrapperInfo);
            wrapper.setMaxMemory(wrapperInfo.getMemory());
            System.out.println("Wrapper [" + wrapper.getServerId() + "] is ready with C" + wrapperInfo.getAvailableProcessors() +
                                   " and " + wrapperInfo.getMemory() + "MB");
        } else {
            wrapper.setWrapperInfo(wrapperInfo);
            wrapper.setMaxMemory(wrapperInfo.getMemory());
            wrapper.updateWrapper();
            System.out.println("Wrapper [" + wrapper.getServerId() + "] is ready with C" + wrapperInfo.getAvailableProcessors() +
                                   " and " + wrapperInfo.getMemory() + "MB");

            if (wrapperInfo.getVersion() != null && !wrapperInfo.getVersion().equals(
                NetworkUtils.class.getPackage().getImplementationVersion())) {
                System.err.println("Wrapper [" + wrapper.getServerId() + "] does not use the same version as this CloudNet Master [Master:"
                                       + NetworkUtils.class.getPackage().getImplementationVersion() +
                                       "/Wrapper:" + wrapperInfo.getVersion() + "], please update");
            }
        }
    }
}
