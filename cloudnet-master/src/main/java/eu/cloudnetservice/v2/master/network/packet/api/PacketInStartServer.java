package eu.cloudnetservice.v2.master.network.packet.api;

import eu.cloudnetservice.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.v2.lib.process.ServerProcessData;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.process.CoreServerProcessBuilder;

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
