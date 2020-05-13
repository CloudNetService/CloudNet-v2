package eu.cloudnetservice.v2.master.network.packet.in;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.server.ProxyProcessMeta;
import eu.cloudnetservice.v2.lib.server.info.ProxyInfo;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.v2.master.network.components.Wrapper;

public final class PacketInAddProxy implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }
        Wrapper wrapper = (Wrapper) packetSender;
        ProxyInfo nullServerInfo = packet.getData().getObject("proxyInfo", ProxyInfo.TYPE);
        ProxyProcessMeta proxyProcessMeta = packet.getData().getObject("proxyProcess", ProxyProcessMeta.TYPE);
        ProxyServer proxyServer = new ProxyServer(proxyProcessMeta, wrapper, nullServerInfo);
        wrapper.getProxies().put(proxyProcessMeta.getServiceId().getServerId(), proxyServer);
        wrapper.getWaitingServices().remove(proxyServer.getServerId());

        CloudNet.getInstance().getNetworkManager().handleProxyAdd(proxyServer);
    }
}
