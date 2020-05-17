package eu.cloudnetservice.cloudnet.v2.master.network.packet.api;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.process.ProxyProcessData;
import eu.cloudnetservice.cloudnet.v2.master.process.CoreProxyProcessBuilder;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInStartProxy implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        final ProxyProcessData proxyProcess = packet.getData().getObject("proxyProcess", ProxyProcessData.TYPE);
        CoreProxyProcessBuilder.create(proxyProcess.getProxyGroupName())
                               .wrapperName(proxyProcess.getWrapperName())
                               .memory(proxyProcess.getMemory())
                               .javaProcessParameters(proxyProcess.getJavaProcessParameters())
                               .proxyProcessParameters(proxyProcess.getProxyProcessParameters())
                               .templateUrl(proxyProcess.getTemplateUrl())
                               .plugins(proxyProcess.getPlugins())
                               .properties(proxyProcess.getProperties())
                               .startProxy();
    }
}
