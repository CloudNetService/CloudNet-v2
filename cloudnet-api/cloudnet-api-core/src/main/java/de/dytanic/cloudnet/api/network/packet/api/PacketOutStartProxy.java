package de.dytanic.cloudnet.api.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.process.ProxyProcessData;
import de.dytanic.cloudnet.lib.utility.document.Document;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutStartProxy extends Packet {

    public PacketOutStartProxy(final ProxyProcessData proxyProcessData) {
        super(PacketRC.SERVER_HANDLE + 6,
              new Document("group", proxyProcessData.getProxyGroupName())
                  .append("wrapper", proxyProcessData.getWrapperName())
                  .append("memory", proxyProcessData.getMemory())
                  .append("url", proxyProcessData.getTemplateUrl())
                  .append("javaProcessParameters", proxyProcessData.getJavaProcessParameters())
                  .append("proxyProcessParameters", proxyProcessData.getProxyProcessParameters())
                  .append("plugins", proxyProcessData.getPlugins())
                  .append("properties", proxyProcessData.getProperties()));
    }
}
