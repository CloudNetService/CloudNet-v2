package de.dytanic.cloudnetcore.network;

import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.event.network.ChannelInitEvent;
import de.dytanic.cloudnetcore.api.event.network.WrapperChannelDisconnectEvent;
import de.dytanic.cloudnetcore.api.event.network.WrapperChannelInitEvent;
import de.dytanic.cloudnetcore.database.StatisticManager;
import de.dytanic.cloudnetcore.network.components.INetworkComponent;
import de.dytanic.cloudnetcore.network.components.MinecraftServer;
import de.dytanic.cloudnetcore.network.components.ProxyServer;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutCloudNetwork;
import de.dytanic.cloudnetcore.network.packet.out.PacketOutOnlineServer;
import de.dytanic.cloudnetcore.network.wrapper.WrapperSession;
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
    private INetworkComponent networkComponent;

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
