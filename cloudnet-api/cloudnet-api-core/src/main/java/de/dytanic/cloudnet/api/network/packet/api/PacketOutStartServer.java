/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnet.api.network.packet.api;

import de.dytanic.cloudnet.lib.network.WrapperInfo;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketRC;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnet.lib.utility.document.Document;

import java.util.Collection;
import java.util.Properties;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketOutStartServer extends Packet {

    public PacketOutStartServer(String group,
                                int memory,
                                ServerConfig serverConfig,
                                Properties properties,
                                boolean priorityStop,
                                String[] processParameters,
                                Template template,
                                String customServr,
                                boolean onlineMode,
                                Collection<ServerInstallablePlugin> plugins,
                                String urlTemplate) {
        super(PacketRC.SERVER_HANDLE + 4, new Document("group", group).append("memory", memory).append("priorityStop", priorityStop).append(
            "serverConfig",
            serverConfig).append("properties", properties).append("processParameters", processParameters).append("customServer",
                                                                                                                 customServr).append(
            "onlineMode",
            onlineMode).append("plugins", plugins).append("url", urlTemplate));
        if (template != null) {
            data.append("template", template);
        }
    }

    public PacketOutStartServer(String group,
                                int memory,
                                ServerConfig serverConfig,
                                Properties properties,
                                boolean priorityStop,
                                String[] processParameters,
                                Template template,
                                String customServr,
                                boolean onlineMode,
                                Collection<ServerInstallablePlugin> plugins,
                                String urlTemplate,
                                String serverId) {
        super(PacketRC.SERVER_HANDLE + 4, new Document("group", group).append("memory", memory).append("priorityStop", priorityStop).append(
            "serverConfig",
            serverConfig).append("properties", properties).append("processParameters", processParameters).append("customServer",
                                                                                                                 customServr).append(
            "onlineMode",
            onlineMode).append("plugins", plugins).append("url", urlTemplate).append("serviceId", serverId));
        if (template != null) {
            data.append("template", template);
        }
    }

    public PacketOutStartServer(WrapperInfo wrapper,
                                String group,
                                int memory,
                                ServerConfig serverConfig,
                                Properties properties,
                                boolean priorityStop,
                                String[] processParameters,
                                Template template,
                                String customServr,
                                boolean onlineMode,
                                Collection<ServerInstallablePlugin> plugins,
                                String urlTemplate) {
        super(PacketRC.SERVER_HANDLE + 4, new Document("group", group).append("wrapper", wrapper.getServerId())
                                                                      .append("memory", memory)
                                                                      .append("priorityStop", priorityStop)
                                                                      .append("serverConfig", serverConfig)
                                                                      .append("properties", properties)
                                                                      .append("processParameters", processParameters)
                                                                      .append("customServer", customServr)
                                                                      .append("onlineMode", onlineMode)
                                                                      .append("plugins", plugins)
                                                                      .append("url", urlTemplate));
        if (template != null) {
            data.append("template", template);
        }
    }

    public PacketOutStartServer(WrapperInfo wrapperInfo,
                                String group,
                                String serviceId,
                                int memory,
                                ServerConfig serverConfig,
                                Properties properties,
                                boolean priorityStop,
                                String[] processParameters,
                                Template template,
                                String customServr,
                                boolean onlineMode,
                                Collection<ServerInstallablePlugin> plugins,
                                String urlTemplate) {
        super(PacketRC.SERVER_HANDLE + 4, new Document("group", group).append("wrapper", wrapperInfo.getServerId())
                                                                      .append("priorityStop",
                                                                              priorityStop)
                                                                      .append("serviceId", serviceId)
                                                                      .append("memory", memory)
                                                                      .append("serverConfig", serverConfig)
                                                                      .append("properties", properties)
                                                                      .append("processParameters", processParameters)
                                                                      .append("customServer", customServr)
                                                                      .append("onlineMode", onlineMode)
                                                                      .append("plugins", plugins)
                                                                      .append("url", urlTemplate));
        if (template != null) {
            data.append("template", template);
        }
    }
}
