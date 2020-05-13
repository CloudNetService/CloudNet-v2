package eu.cloudnetservice.v2.master.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.v2.master.network.components.Wrapper;

public final class PacketInRemoveProxy implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }

        Wrapper wrapper = (Wrapper) packetSender;
        ProxyInfo proxyInfo = packet.getData().getObject("proxyInfo", ProxyInfo.TYPE);

        if (wrapper.getProxies().containsKey(proxyInfo.getServiceId().getServerId())) {
            ProxyServer minecraftServer = wrapper.getProxies().get(proxyInfo.getServiceId().getServerId());
            if (minecraftServer.getChannel() != null) {
                minecraftServer.getChannel().close();
            }

            wrapper.getProxies().remove(proxyInfo.getServiceId().getServerId());
            CloudNet.getInstance().getNetworkManager().handleProxyRemove(minecraftServer);
            CloudNet.getInstance().getScreenProvider().handleDisableScreen(proxyInfo.getServiceId());
        }
    }
}