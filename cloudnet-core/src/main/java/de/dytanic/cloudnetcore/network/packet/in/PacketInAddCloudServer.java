package de.dytanic.cloudnetcore.network.packet.in;

import de.dytanic.cloudnet.lib.cloudserver.CloudServerMeta;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketInHandler;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.network.components.CloudServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.components.priority.PriorityStopTask;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tareko on 23.10.2017.
 */
public class PacketInAddCloudServer implements PacketInHandler {

    public void handleInput(Packet packet, PacketSender packetSender) {
        if (!(packetSender instanceof Wrapper)) {
            return;
        }
        Wrapper wrapper = ((Wrapper) packetSender);
        ServerInfo nullServerInfo = packet.getData().getObject("serverInfo", ServerInfo.TYPE);
        CloudServerMeta serverProcessMeta = packet.getData().getObject("cloudServerMeta", CloudServerMeta.TYPE);
        CloudServer minecraftServer = new CloudServer(wrapper, nullServerInfo, serverProcessMeta);
        wrapper.getCloudServers().put(nullServerInfo.getServiceId().getServerId(), minecraftServer);
        wrapper.getWaitingServices().remove(minecraftServer.getServerId());

        {
            if (serverProcessMeta.isPriorityStop()) {
                ServerGroup serverGroup = CloudNet.getInstance().getServerGroups().get(serverProcessMeta.getServiceId().getGroup());
                if (serverGroup != null) {
                    PriorityStopTask priorityStopTask = new PriorityStopTask(wrapper,
                                                                             minecraftServer,
                                                                             serverGroup.getPriorityService().getStopTimeInSeconds());
                    ScheduledFuture<?> scheduledFuture = CloudNet.getExecutor().scheduleAtFixedRate(
                        priorityStopTask, 0, 1, TimeUnit.SECONDS);
                    priorityStopTask.setFuture(scheduledFuture);
                }
            }
        }

        CloudNet.getInstance().getNetworkManager().handleServerAdd(minecraftServer);
    }

}
