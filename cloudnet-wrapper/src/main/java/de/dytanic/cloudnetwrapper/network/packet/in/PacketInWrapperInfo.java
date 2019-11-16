/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetwrapper.network.packet.in;

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
        CloudNetWrapper.getInstance().getServerGroups().putAll(wrapperExternal.getServerGroups());
        CloudNetWrapper.getInstance().getServerGroups().forEach(
            (name, serverGroup) -> System.out.printf("Importing server group [%s] from CloudNet-Master%n", name));

        CloudNetWrapper.getInstance().getProxyGroups().clear();
        CloudNetWrapper.getInstance().getProxyGroups().putAll(wrapperExternal.getProxyGroups());
        CloudNetWrapper.getInstance().getProxyGroups().forEach(
            (name, serverGroup) -> System.out.printf("Importing proxy group [%s] from CloudNet-Master%n", name));
    }
}
