/*
 * Copyright 2017 Tarek Hosni El Alaoui
 * Copyright 2020 CloudNetService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.cloudnetservice.cloudnet.v2.master.network.packet.in;

import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketInHandler;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.PacketSender;
import eu.cloudnetservice.cloudnet.v2.lib.server.ServerProcessMeta;
import eu.cloudnetservice.cloudnet.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import eu.cloudnetservice.cloudnet.v2.master.network.components.priority.PriorityStopTask;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
