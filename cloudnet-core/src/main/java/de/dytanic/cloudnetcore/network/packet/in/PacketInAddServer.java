package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ServerProcessMeta;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.components.priority.PriorityStopTask;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 20.07.2017.
 */
public class PacketInAddServer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }

        Wrapper wrapper = (Wrapper) packetSender;
        ServerInfo serverInfo = packet.getData().getObject("serverInfo", ServerInfo.TYPE);
        ServerProcessMeta serverProcessMeta = packet.getData().getObject("serverProcess", ServerProcessMeta.TYPE);
        MinecraftServer minecraftServer = new MinecraftServer(serverProcessMeta,
                                                              wrapper,
                                                              CloudNet.getInstance()
                                                                      .getServerGroups()
                                                                      .get(serverInfo.getServiceId().getGroup()),
                                                              serverInfo);
        wrapper.getServers().put(serverInfo.getServiceId().getServerId(), minecraftServer);
        wrapper.getWaitingServices().remove(minecraftServer.getServerId());

        if (serverProcessMeta.isPriorityStop()) {
            final PriorityStopTask stopTask = new PriorityStopTask(minecraftServer,
                                                                   minecraftServer.getGroup().getPriorityService().getStopTimeInSeconds());
            Future<?> future = CloudNet.getExecutor().schedule(stopTask, 1, TimeUnit.SECONDS);
            stopTask.setFuture(future);
        }

        CloudNet.getInstance().getNetworkManager().handleServerAdd(minecraftServer);
    }
}
