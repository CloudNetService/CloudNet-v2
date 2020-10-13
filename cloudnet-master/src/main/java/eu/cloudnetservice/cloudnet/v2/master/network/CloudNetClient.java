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

package eu.cloudnetservice.cloudnet.v2.master.network;

import eu.cloudnetservice.cloudnet.v2.lib.CloudNetwork;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import eu.cloudnetservice.cloudnet.v2.master.CloudNet;
import eu.cloudnetservice.cloudnet.v2.master.api.event.network.ChannelInitEvent;
import eu.cloudnetservice.cloudnet.v2.master.api.event.network.WrapperChannelDisconnectEvent;
import eu.cloudnetservice.cloudnet.v2.master.api.event.network.WrapperChannelInitEvent;
import eu.cloudnetservice.cloudnet.v2.master.database.StatisticManager;
import eu.cloudnetservice.cloudnet.v2.master.network.components.INetworkComponent;
import eu.cloudnetservice.cloudnet.v2.master.network.components.MinecraftServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.ProxyServer;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import eu.cloudnetservice.cloudnet.v2.master.network.packet.out.PacketOutCloudNetwork;
import eu.cloudnetservice.cloudnet.v2.master.network.packet.out.PacketOutOnlineServer;
import eu.cloudnetservice.cloudnet.v2.master.network.wrapper.WrapperSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.channels.ClosedChannelException;
import java.util.UUID;

/**
 * This is the SimpleChannelInboundHandler of netty handled for a networkComponent
 */
public class CloudNetClient extends SimpleChannelInboundHandler<Packet> {

    private Channel channel;
    private final INetworkComponent networkComponent;

    public CloudNetClient(INetworkComponent iNetworkComponent, Channel channel) {
        this.networkComponent = iNetworkComponent;
        this.networkComponent.setChannel(channel);
        this.channel = channel;

        System.out.println("Channel connected [" + channel.remoteAddress() + "/serverId=" + networkComponent.getServerId() + ']');

        if (networkComponent instanceof Wrapper) {
            StatisticManager.getInstance().wrapperConnections();
            System.out.println("Wrapper [" + networkComponent.getServerId() + "] is connected.");
            CloudNet.getInstance().getEventManager().callEvent(new WrapperChannelInitEvent((Wrapper) networkComponent, channel));
            CloudNet.getInstance().getDbHandlers().getWrapperSessionDatabase().addSession(
                new WrapperSession(UUID.randomUUID(), ((Wrapper) networkComponent).getNetworkInfo(), System.currentTimeMillis()));
        }

        CloudNetwork cloudNetwork = CloudNet.getInstance().getNetworkManager().newCloudNetwork();
        channel.writeAndFlush(new PacketOutCloudNetwork(cloudNetwork));

        if (networkComponent instanceof MinecraftServer) {
            ((MinecraftServer) networkComponent).setChannelLostTime(0L);
            networkComponent.getWrapper().sendPacket(new PacketOutOnlineServer(((MinecraftServer) networkComponent).getServerInfo()));
        }
        if (networkComponent instanceof ProxyServer) {
            ((ProxyServer) networkComponent).setChannelLostTime(0L);
        }
        CloudNet.getInstance().getEventManager().callEvent(new ChannelInitEvent(channel, networkComponent));
        init(cloudNetwork);
    }

    public static void init(CloudNetwork cloudNetwork) {
        CloudNet.getExecutor().submit(() -> {
            CloudNet.getInstance().getNetworkManager().sendAll(new PacketOutCloudNetwork(cloudNetwork));
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        this.networkComponent.setChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if ((!channel.isActive() || !channel.isOpen() || !channel.isWritable())) {
            System.out.println("Channel disconnected [" + channel.remoteAddress() + "/serverId=" + networkComponent.getServerId() + ']');
            ctx.close().syncUninterruptibly();
            if (networkComponent instanceof MinecraftServer) {
                ((MinecraftServer) networkComponent).setChannelLostTime(System.currentTimeMillis());
            }
            if (networkComponent instanceof ProxyServer) {
                ((ProxyServer) networkComponent).setChannelLostTime(System.currentTimeMillis());
            }
            if (networkComponent instanceof Wrapper) {
                try {
                    ((Wrapper) networkComponent).disconnect();
                } catch (Exception ex) {
                    ((Wrapper) networkComponent).getServers().clear();
                    ((Wrapper) networkComponent).getProxies().clear();
                }

                CloudNet.getInstance().getEventManager().callEvent(new WrapperChannelDisconnectEvent(((Wrapper) networkComponent)));

            }
            networkComponent.setChannel(null);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!(cause instanceof ClosedChannelException)) {
            cause.printStackTrace();
            CloudNet.getInstance().getWrappers().forEach((name, wrapper) -> {
                if (wrapper.getChannel() == ctx.channel()) {
                    wrapper.setChannel(null);
                }
            });
        }
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        if (packet == null) {
            return;
        }
        CloudNet.getLogger().finest(String.format("Receiving packet %s from %s%n", packet, networkComponent.getServerId()));
        CloudNet.getInstance().getPacketManager().dispatchPacket(packet, networkComponent);
    }

}
