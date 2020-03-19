package de.dytanic.cloudnetcore.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.process.ProxyProcessData;
import de.dytanic.cloudnetcore.process.CoreProxyProcessBuilder;

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
