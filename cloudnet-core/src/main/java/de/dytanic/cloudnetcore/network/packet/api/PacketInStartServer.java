/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnetcore.CloudNet;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Properties;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInStartServer implements PacketInHandler {

    private static final Type STRING_ARRAY_TYPE = TypeToken.getArray(String.class).getType();
    private static final Type SERVER_INSTALLABLE_PLUGIN_COLLECTION_TYPE =
        TypeToken.getParameterized(Collection.class, ServerInstallablePlugin.class).getType();

    public void handleInput(Packet packet, PacketSender packetSender) {
        CloudNet.getInstance().getScheduler().runTaskSync(() -> {
            if (packet.getData().contains("customServer")) {
                CloudNet.getInstance().startGameServer(CloudNet.getInstance()
                                                               .getServerGroups()
                                                               .get(packet.getData().getString("group")),
                                                       packet.getData().getObject("serverConfig", ServerConfig.TYPE),
                                                       packet.getData().getInt("memory"),
                                                       packet.getData().getBoolean("priorityStop"),
                                                       packet.getData().getString("url"),
                                                       packet.getData().getObject("processParameters", STRING_ARRAY_TYPE),
                                                       packet.getData().getBoolean("onlinemode"),
                                                       packet.getData().getObject("plugins", SERVER_INSTALLABLE_PLUGIN_COLLECTION_TYPE),
                                                       packet.getData().getString("customServer"),
                                                       packet.getData().getObject("properties", Properties.class));
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
                                                               packet.getData().getObject("processParameters", STRING_ARRAY_TYPE),
                                                               packet.getData().getBoolean("onlinemode"),
                                                               packet.getData()
                                                                     .getObject("plugins", SERVER_INSTALLABLE_PLUGIN_COLLECTION_TYPE),
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
                                                               packet.getData().getObject("processParameters", STRING_ARRAY_TYPE),
                                                               packet.getData().getBoolean("onlinemode"),
                                                               packet.getData()
                                                                     .getObject("plugins", SERVER_INSTALLABLE_PLUGIN_COLLECTION_TYPE),
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
                                                               packet.getData().getObject("processParameters", STRING_ARRAY_TYPE),
                                                               packet.getData().getBoolean("onlinemode"),
                                                               packet.getData()
                                                                     .getObject("plugins", SERVER_INSTALLABLE_PLUGIN_COLLECTION_TYPE),
                                                               packet.getData().getString("customServer"),
                                                               packet.getData().getObject("properties", Properties.class));
                    } else {
                        CloudNet.getInstance().startGameServer(CloudNet.getInstance()
                                                                       .getServerGroups().get(packet.getData().getString("group")),
                                                               packet.getData().getObject("serverConfig", ServerConfig.TYPE),
                                                               packet.getData().getInt("memory"),
                                                               packet.getData().getBoolean("priorityStop"),
                                                               packet.getData().getString("url"),
                                                               packet.getData().getObject("processParameters", STRING_ARRAY_TYPE),
                                                               packet.getData().getBoolean("onlinemode"),
                                                               packet.getData()
                                                                     .getObject("plugins", SERVER_INSTALLABLE_PLUGIN_COLLECTION_TYPE),
                                                               packet.getData().getString("customServer"),
                                                               packet.getData().getObject("properties", Properties.class));
                    }
                }
            }
        });
    }
}
