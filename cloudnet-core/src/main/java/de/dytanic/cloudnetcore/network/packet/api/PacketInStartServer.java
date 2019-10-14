/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.network.packet.api;

import com.google.gson.reflect.TypeToken;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;
import de.dytanic.cloudnetcore.CloudNet;

import java.util.Collection;
import java.util.Properties;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInStartServer extends PacketInHandler {

    @Override
    public void handleInput(Document data, PacketSender packetSender) {
        CloudNet.getInstance().getScheduler().runTaskSync(new Runnable() {
            @Override
            public void run() {
                if (data.contains("customServer")) {
                    CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroups().get(data.getString("group")),
                                                           data.getObject("serverConfig", new TypeToken<ServerConfig>() {}.getType()),
                                                           data.getInt("memory"),
                                                           data.getBoolean("priorityStop"),
                                                           data.getString("url"),
                                                           data.getObject("processParameters", new TypeToken<String[]>() {}.getType()),
                                                           data.getBoolean("onlinemode"),
                                                           data.getObject("plugins",
                                                                          new TypeToken<Collection<ServerInstallablePlugin>>() {}.getType()),
                                                           data.getString("customServer"),
                                                           data.getObject("properties", new TypeToken<Properties>() {}.getType()));
                } else {
                    if (data.contains("template")) {
                        if (data.contains("wrapper")) {
                            CloudNet.getInstance().startGameServer(CloudNet.getInstance().getWrappers().get(data.getString("wrapper")),
                                                                   CloudNet.getInstance().getServerGroups().get(data.getString("group")),
                                                                   data.getObject("serverConfig",
                                                                                  new TypeToken<ServerConfig>() {}.getType()),
                                                                   data.getObject("template", new TypeToken<Template>() {}.getType()),
                                                                   data.getInt("memory"),
                                                                   data.getBoolean("priorityStop"),
                                                                   data.getString("url"),
                                                                   data.getObject("processParameters",
                                                                                  new TypeToken<String[]>() {}.getType()),
                                                                   data.getBoolean("onlinemode"),
                                                                   data.getObject("plugins",
                                                                                  new TypeToken<Collection<ServerInstallablePlugin>>() {}.getType()),
                                                                   data.getString("customServer"),
                                                                   data.getObject("properties", new TypeToken<Properties>() {}.getType()));
                        } else {
                            CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroups().get(data.getString("group")),
                                                                   (ServerConfig) data.getObject("serverConfig",
                                                                                                 new TypeToken<ServerConfig>() {}.getType()),
                                                                   data.getObject("template", new TypeToken<Template>() {}.getType()),
                                                                   data.getInt("memory"),
                                                                   data.getBoolean("priorityStop"),
                                                                   data.getString("url"),
                                                                   data.getObject("processParameters",
                                                                                  new TypeToken<String[]>() {}.getType()),
                                                                   data.getBoolean("onlinemode"),
                                                                   data.getObject("plugins",
                                                                                  new TypeToken<Collection<ServerInstallablePlugin>>() {}.getType()),
                                                                   data.getString("customServer"),
                                                                   data.getObject("properties", new TypeToken<Properties>() {}.getType()));
                        }
                    } else {
                        if (data.contains("wrapper")) {
                            CloudNet.getInstance().startGameServer(CloudNet.getInstance().getWrappers().get(data.getString("wrapper")),
                                                                   CloudNet.getInstance().getServerGroups().get(data.getString("group")),
                                                                   data.getObject("serverConfig",
                                                                                  new TypeToken<ServerConfig>() {}.getType()),
                                                                   data.getInt("memory"),
                                                                   data.getBoolean("priorityStop"),
                                                                   data.getString("url"),
                                                                   data.getObject("processParameters",
                                                                                  new TypeToken<String[]>() {}.getType()),
                                                                   data.getBoolean("onlinemode"),
                                                                   data.getObject("plugins",
                                                                                  new TypeToken<Collection<ServerInstallablePlugin>>() {}.getType()),
                                                                   data.getString("customServer"),
                                                                   data.getObject("properties", new TypeToken<Properties>() {}.getType()));
                        } else {
                            CloudNet.getInstance().startGameServer(CloudNet.getInstance().getServerGroups().get(data.getString("group")),
                                                                   data.getObject("serverConfig",
                                                                                  new TypeToken<ServerConfig>() {}.getType()),
                                                                   data.getInt("memory"),
                                                                   data.getBoolean("priorityStop"),
                                                                   data.getString("url"),
                                                                   data.getObject("processParameters",
                                                                                  new TypeToken<String[]>() {}.getType()),
                                                                   data.getBoolean("onlinemode"),
                                                                   data.getObject("plugins",
                                                                                  new TypeToken<Collection<ServerInstallablePlugin>>() {}.getType()),
                                                                   data.getString("customServer"),
                                                                   data.getObject("properties", new TypeToken<Properties>() {}.getType()));
                        }
                    }
                }
            }
        });
    }
}
