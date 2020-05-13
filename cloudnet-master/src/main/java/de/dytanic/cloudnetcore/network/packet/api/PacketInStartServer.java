package de.dytanic.cloudnetcore.network.packet.api;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.process.ServerProcessData;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.process.CoreServerProcessBuilder;

/**
 * Created by Tareko on 21.08.2017.
 */
public class PacketInStartServer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        ServerProcessData processData = packet.getData().getObject("serverProcess", ServerProcessData.TYPE);
        CloudNet.getExecutor().submit(() -> {
            CoreServerProcessBuilder.create(processData.getServerGroupName())
                                    .wrapperName(processData.getWrapperName())
                                    .memory(processData.getMemory())
                                    .javaProcessParameters(processData.getJavaProcessParameters())
                                    .serverProcessParameters(processData.getServerProcessParameters())
                                    .templateUrl(processData.getTemplateUrl())
                                    .plugins(processData.getPlugins())
                                    .properties(processData.getProperties())
                                    .template(processData.getTemplate())
                                    .serverConfig(processData.getServerConfig())
                                    .startServer();
        });
    }

}
