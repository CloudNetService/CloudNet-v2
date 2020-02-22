package de.dytanic.cloudnetcore.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.CommonTypes;
import de.dytanic.cloudnetcore.CloudNet;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInStartProxy implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!packet.getData().contains("wrapper") || packet.getData().get("wrapper ").isJsonNull()) {
            CloudNet.getInstance().startProxy(CloudNet.getInstance().getProxyGroups().get(packet.getData().getString("group")),
                                              packet.getData().getInt("memory"),
                                              packet.getData().getObject("javaProcessParameters", CommonTypes.LIST_STRING_TYPE),
                                              packet.getData().getString("url"),
                                              packet.getData().getObject("plugins", ServerInstallablePlugin.SET_TYPE),
                                              packet.getData().getDocument("properties"));
        } else {
            CloudNet.getInstance().startProxy(CloudNet.getInstance().getWrappers().get(packet.getData().getString("wrapper")),
                                              CloudNet.getInstance().getProxyGroups().get(packet.getData().getString("group")),
                                              packet.getData().getInt("memory"),
                                              packet.getData().getObject("javaProcessParameters", CommonTypes.LIST_STRING_TYPE),
                                              packet.getData().getString("url"),
                                              packet.getData().getObject("plugins", ServerInstallablePlugin.SET_TYPE),
                                              packet.getData().getDocument("properties"));
        }
    }
}
