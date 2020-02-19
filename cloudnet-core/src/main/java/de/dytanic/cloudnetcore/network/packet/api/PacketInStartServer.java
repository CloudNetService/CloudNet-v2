package de.dytanic.cloudnetcore.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.process.ServerProcessData;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.CommonTypes;
import de.dytanic.cloudnetcore.CloudNet;

import java.util.Properties;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInStartServer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        // TODO
        ServerProcessData processData = packet.getData().getObject("serverProcess", ServerProcessData.TYPE);
        CloudNet.getExecutor().submit(() -> {
            if (packet.getData().contains("customServer")) {
                startCustomServer(packet);
            } else {
                if (packet.getData().contains("template")) {
                    if (packet.getData().contains("wrapper")) {
                        CloudNet.getInstance().startGameServer(CloudNet.getInstance()
                                                                       .getWrappers()
                                                                       .get(packet.getData().getString("wrapper")),
                                                               CloudNet.getInstance()
                                                                       .getServerGroups()
                                                                       .get(packet.getData().getString("group")),
                                                               packet.getData().getObject("serverConfig", ServerConfig.TYPE),
                                                               packet.getData().getObject("template", Template.TYPE),
                                                               packet.getData().getInt("memory"),
                                                               packet.getData().getBoolean("priorityStop"),
                                                               packet.getData().getString("url"),
                                                               packet.getData()
                                                                     .getObject("processParameters", CommonTypes.LIST_STRING_TYPE),
                                                               packet.getData().getBoolean("onlinemode"),
                                                               packet.getData()
                                                                     .getObject("plugins", ServerInstallablePlugin.SET_TYPE),
                                                               packet.getData().getString("customServer"),
                                                               packet.getData().getObject("properties", Properties.class));
                    } else {
                        CloudNet.getInstance().startGameServer(CloudNet.getInstance()
                                                                       .getServerGroups()
                                                                       .get(packet.getData().getString("group")),
                                                               (ServerConfig) packet.getData().getObject("serverConfig", ServerConfig.TYPE),
                                                               packet.getData().getObject("template", Template.TYPE),
                                                               packet.getData().getInt("memory"),
                                                               packet.getData().getBoolean("priorityStop"),
                                                               packet.getData().getString("url"),
                                                               packet.getData()
                                                                     .getObject("processParameters", CommonTypes.LIST_STRING_TYPE),
                                                               packet.getData().getBoolean("onlinemode"),
                                                               packet.getData()
                                                                     .getObject("plugins", ServerInstallablePlugin.SET_TYPE),
                                                               packet.getData().getString("customServer"),
                                                               packet.getData().getObject("properties", Properties.class));
                    }
                } else {
                    if (packet.getData().contains("wrapper")) {
                        CloudNet.getInstance().startGameServer(CloudNet.getInstance()
                                                                       .getWrappers().get(packet.getData().getString("wrapper")),
                                                               CloudNet.getInstance()
                                                                       .getServerGroups().get(packet.getData().getString("group")),
                                                               packet.getData().getObject("serverConfig", ServerConfig.TYPE),
                                                               packet.getData().getInt("memory"),
                                                               packet.getData().getBoolean("priorityStop"),
                                                               packet.getData().getString("url"),
                                                               packet.getData()
                                                                     .getObject("processParameters", CommonTypes.LIST_STRING_TYPE),
                                                               packet.getData().getBoolean("onlinemode"),
                                                               packet.getData()
                                                                     .getObject("plugins", ServerInstallablePlugin.SET_TYPE),
                                                               packet.getData().getString("customServer"),
                                                               packet.getData().getObject("properties", Properties.class));
                    } else {
                        startCustomServer(packet);
                    }
                }
            }
        });
    }

    private static void startCustomServer(final Packet packet) {
        CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroups().get(packet.getData().getString("group")),
                                               packet.getData().getObject("serverConfig", ServerConfig.TYPE),
                                               packet.getData().getInt("memory"),
                                               packet.getData().getBoolean("priorityStop"),
                                               packet.getData().getString("url"),
                                               packet.getData().getObject("processParameters", CommonTypes.LIST_STRING_TYPE),
                                               packet.getData().getBoolean("onlinemode"),
                                               packet.getData().getObject("plugins", ServerInstallablePlugin.SET_TYPE),
                                               packet.getData().getString("customServer"),
                                               packet.getData().getObject("properties", Properties.class));
    }
}
