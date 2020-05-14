package eu.cloudnetservice.cloudnet.v2.lib.network;

import eu.cloudnetservice.cloudnet.v2.lib.NetworkUtils;
import eu.cloudnetservice.cloudnet.v2.lib.network.protocol.packet.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.channels.ClosedChannelException;

public class NetDispatcher extends SimpleChannelInboundHandler<Packet> {

    private final NetworkConnection networkConnection;

    private final boolean shutdownOnInactive;

    public NetDispatcher(NetworkConnection networkConnection, boolean shutdownOnInactive) {
        this.networkConnection = networkConnection;
        this.shutdownOnInactive = shutdownOnInactive;
    }

    public NetworkConnection getNetworkConnection() {
        return networkConnection;
    }

    public boolean isShutdownOnInactive() {
        return shutdownOnInactive;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if ((!ctx.channel().isActive() || !ctx.channel().isOpen() || !ctx.channel().isWritable())) {
            networkConnection.setChannel(null);
            ctx.channel().close().syncUninterruptibly();
            if (networkConnection.getTask() != null) {
                networkConnection.getTask().run();
            }
            if (shutdownOnInactive) {
                System.exit(0);
            }
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
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        if (packet != null) {
            if (!NetworkUtils.getExecutor().isShutdown()) {
                NetworkUtils.getExecutor().submit(() -> {
                    networkConnection.getPacketManager().dispatchPacket(packet, networkConnection);
                });
            }
        }
    }
}
