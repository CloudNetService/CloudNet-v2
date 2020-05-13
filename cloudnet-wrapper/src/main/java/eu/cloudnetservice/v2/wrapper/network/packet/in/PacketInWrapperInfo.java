package eu.cloudnetservice.v2.wrapper.network.packet.in;

import de.dytanic.cloudnet.lib.network.WrapperExternal;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.wrapper.CloudNetWrapper;

public class PacketInWrapperInfo implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        WrapperExternal wrapperExternal = packet.getData().getObject("wrapper", WrapperExternal.TYPE);
        CloudNetWrapper.getInstance().setSimpledUser(wrapperExternal.getUser());
        CloudNetWrapper.getInstance().getServerGroups().clear();
        CloudNetWrapper.getInstance().getServerGroups().putAll(wrapperExternal.getServerGroups());
        CloudNetWrapper.getInstance().getServerGroups().forEach(
            (name, serverGroup) -> System.out.println(String.format("Importing server group [%s] from CloudNet-Master%n", name)));

        CloudNetWrapper.getInstance().getProxyGroups().clear();
        CloudNetWrapper.getInstance().getProxyGroups().putAll(wrapperExternal.getProxyGroups());
        CloudNetWrapper.getInstance().getProxyGroups().forEach(
            (name, serverGroup) -> System.out.println(String.format("Importing proxy group [%s] from CloudNet-Master%n", name)));
    }
}
