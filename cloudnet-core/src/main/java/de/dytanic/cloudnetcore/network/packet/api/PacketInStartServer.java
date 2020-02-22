package de.dytanic.cloudnetcore.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.process.ServerProcessData;
import de.dytanic.cloudnet.lib.server.ServerConfig;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.dytanic.cloudnet.lib.service.plugin.ServerInstallablePlugin;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.Wrapper;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInStartServer implements PacketInHandler {

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public void handleInput(Packet packet, PacketSender packetSender) {
        // TODO
        ServerProcessData processData = packet.getData().getObject("serverProcess", ServerProcessData.TYPE);
        CloudNet.getExecutor().submit(() -> {
            final ServerGroup group = CloudNet.getInstance().getServerGroups().get(processData.getServerGroupName());
            final ServerConfig serverConfig = processData.getServerConfig();
            final int memory = processData.getMemory();
            final String templateUrl = processData.getTemplateUrl();
            final List<String> javaProcessParameters = processData.getJavaProcessParameters();
            final Set<ServerInstallablePlugin> plugins = processData.getPlugins();
            final Properties properties = processData.getProperties();
            final Wrapper wrapper = CloudNet.getInstance().getWrappers().get(processData.getWrapperName());
            final Template template = processData.getTemplate();
            if (template != null) {
                if (wrapper != null) {
                    CloudNet.getInstance().startGameServer(wrapper,
                                                           group,
                                                           serverConfig,
                                                           template,
                                                           memory,
                                                           false,
                                                           templateUrl,
                                                           javaProcessParameters.toArray(EMPTY_STRING_ARRAY),
                                                           false,
                                                           plugins,
                                                           null,
                                                           properties);
                } else {
                    CloudNet.getInstance().startGameServer(group,
                                                           serverConfig,
                                                           template,
                                                           memory,
                                                           false,
                                                           templateUrl,
                                                           javaProcessParameters.toArray(EMPTY_STRING_ARRAY),
                                                           false,
                                                           plugins,
                                                           null,
                                                           properties);
                }
            } else if (wrapper != null) {
                CloudNet.getInstance().startGameServer(wrapper,
                                                       group,
                                                       serverConfig,
                                                       memory,
                                                       false,
                                                       templateUrl,
                                                       javaProcessParameters.toArray(EMPTY_STRING_ARRAY),
                                                       false,
                                                       plugins,
                                                       null,
                                                       properties);
            }
        });
    }

}
