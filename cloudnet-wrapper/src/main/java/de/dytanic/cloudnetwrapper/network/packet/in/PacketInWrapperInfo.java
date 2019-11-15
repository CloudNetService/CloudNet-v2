/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.WrapperExternal;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnetwrapper.CloudNetWrapper;

public class PacketInWrapperInfo implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        WrapperExternal wrapperExternal = packet.getData().getObject("wrapper", WrapperExternal.TYPE);
        CloudNetWrapper.getInstance().setSimpledUser(wrapperExternal.getUser());
        CloudNetWrapper.getInstance().getServerGroups().clear();
        NetworkUtils.addAll(CloudNetWrapper.getInstance().getServerGroups(), wrapperExternal.getServerGroups(), value -> {
            System.out.println("Importing server group [" + value.getName() + "] from CloudNet-Master");
            return true;
        });
        CloudNetWrapper.getInstance().getProxyGroups().clear();
        NetworkUtils.addAll(CloudNetWrapper.getInstance().getProxyGroups(), wrapperExternal.getProxyGroups(), value -> {
            System.out.println("Importing proxy group [" + value.getName() + "] from CloudNet-Master");
            return true;
        });
    }
}
