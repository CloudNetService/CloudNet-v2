/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.in;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.network.components.Wrapper;

public final class PacketInUpdateWrapperInfo extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }

        WrapperInfo wrapperInfo = data.getObject("wrapperInfo", new TypeToken<WrapperInfo>() {}.getType());

        if (((Wrapper) packetSender).getWrapperInfo() != null) {
            ((Wrapper) packetSender).setWrapperInfo(wrapperInfo);
            ((Wrapper) packetSender).setMaxMemory(wrapperInfo.getMemory());
            System.out.println("Wrapper [" + ((Wrapper) packetSender).getServerId() + "] is ready with C" + wrapperInfo.getAvailableProcessors() + " and " + wrapperInfo
                .getMemory() + "MB");
        } else {
            ((Wrapper) packetSender).setWrapperInfo(wrapperInfo);
            ((Wrapper) packetSender).setMaxMemory(wrapperInfo.getMemory());
            ((Wrapper) packetSender).updateWrapper();
            System.out.println("Wrapper [" + ((Wrapper) packetSender).getServerId() + "] is ready with C" + wrapperInfo.getAvailableProcessors() + " and " + wrapperInfo
                .getMemory() + "MB");

            if (wrapperInfo.getVersion() != null && !wrapperInfo.getVersion().equals(NetworkUtils.class.getPackage()
                                                                                                       .getImplementationVersion())) {
                System.err.println("Wrapper [" + ((Wrapper) packetSender).getServerId() + "] does not use the same version as this CloudNet Master [Master:" + NetworkUtils.class
                    .getPackage()
                    .getImplementationVersion() + "/Wrapper:" + wrapperInfo.getVersion() + "], please update");
            }
        }
    }
}
