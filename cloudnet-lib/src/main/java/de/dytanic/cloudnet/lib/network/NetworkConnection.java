package de.dytanic.cloudnet.lib.network;

import de.dytanic.cloudnet.lib.ConnectableAddress;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.network.auth.Auth;
import de.dytanic.cloudnet.lib.network.auth.packetio.PacketOutAuth;
import de.dytanic.cloudnet.lib.network.protocol.IProtocol;
import de.dytanic.cloudnet.lib.network.protocol.ProtocolRequest;
import de.dytanic.cloudnet.lib.network.protocol.packet.Packet;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketManager;
import de.dytanic.cloudnet.lib.network.protocol.packet.PacketSender;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;

import java.net.InetSocketAddress;

/**
 * Created by Tareko on 22.07.2017.
 */
public final class NetworkConnection implements PacketSender {

    private final PacketManager packetManager = new PacketManager();
    private final EventLoopGroup eventLoopGroup = NetworkUtils.eventLoopGroup(2);
    private final ConnectableAddress localAddress;
    private Channel channel;
    private ConnectableAddress connectableAddress;
    private long connectionTries = 0;
    private Runnable task;

    public NetworkConnection(ConnectableAddress connectableAddress, final ConnectableAddress localAddress) {
        this.connectableAddress = connectableAddress;
        this.localAddress = localAddress;
    }

    public NetworkConnection(ConnectableAddress connectableAddress,
                             final ConnectableAddress localAddress,
                             Runnable task) {
        this.connectableAddress = connectableAddress;
        this.localAddress = localAddress;
        this.task = task;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public Channel getChannel() {
        return channel;
    }

    protected void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ConnectableAddress getConnectableAddress() {
        return connectableAddress;
    }

    public long getConnectionTries() {
        return connectionTries;
    }

    public Runnable getTask() {
        return task;
    }

    @Override
    public String getName() {
        return "Network-Connector";
    }

    @Override
    public void sendPacket(Packet... packets) {

        if (channel == null) {
            return;
        }

        if (channel.eventLoop().inEventLoop()) {
            for (Packet packet : packets) {
                channel.writeAndFlush(packet);
            }
        } else {
            channel.eventLoop().execute(() -> {
                for (Packet packet : packets) {
                    channel.writeAndFlush(packet);
                }
            });
        }
    }

    public boolean tryDisconnect() {
        if (channel != null) {
            channel.close();
        }

        eventLoopGroup.shutdownGracefully();
        return false;
    }

    @Override
    public void send(Object object) {
        if (channel == null) {
            return;
        }

        if (channel.eventLoop().inEventLoop()) {
            channel.writeAndFlush(object);
        } else {
            channel.eventLoop().execute(() -> channel.writeAndFlush(object));
        }
    }

    @Override
    public void sendSynchronized(Object object) {
        channel.writeAndFlush(object).syncUninterruptibly();
    }

    @Override
    public void sendAsynchronized(Object object) {
        NetworkUtils.getExecutor().submit(() -> {
            channel.writeAndFlush(object);
        });
    }

    @Override
    public void send(IProtocol iProtocol, Object element) {
        send(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    public void send(int id, Object element) {
        send(new ProtocolRequest(id, element));
    }

    @Override
    public void sendAsynchronized(int id, Object element) {
        sendAsynchronized(new ProtocolRequest(id, element));
    }

    @Override
    public void sendAsynchronized(IProtocol iProtocol, Object element) {
        sendAsynchronized(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    public void sendSynchronized(int id, Object element) {
        sendSynchronized(new ProtocolRequest(id, element));
    }

    @Override
    public void sendSynchronized(IProtocol iProtocol, Object element) {
        sendSynchronized(new ProtocolRequest(iProtocol.getId(), element));
    }

    @Override
    public void sendPacket(Packet packet) {
        if (channel == null) {
            return;
        }

        if (channel.eventLoop().inEventLoop()) {
            channel.writeAndFlush(packet);
        } else {
            channel.eventLoop().execute(() -> channel.writeAndFlush(packet));
        }
    }

    public void tryConnect(final NetDispatcher netDispatcher, final Auth auth) {
        tryConnect(netDispatcher, auth, null);
    }

    @Override
    public void sendPacketSynchronized(Packet packet) {
        if (channel == null) {
            return;
        }

        if (channel.eventLoop().inEventLoop()) {
            channel.writeAndFlush(packet).syncUninterruptibly();
        } else {
            channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    channel.writeAndFlush(packet).syncUninterruptibly();
                }
            });
        }
    }

    public boolean isConnected() {
        return channel != null;
    }

    public boolean tryConnect(SimpleChannelInboundHandler<Packet> channelInboundHandler, Auth auth, Runnable cancelTask) {
        try {
            Bootstrap bootstrap = new Bootstrap().option(ChannelOption.AUTO_READ, true)
                                                 .group(eventLoopGroup)
                                                 .handler(new ChannelInitializer<Channel>() {
                                                     @Override
                                                     protected void initChannel(Channel channel) throws Exception {
                                                         NetworkUtils.initChannel(channel).pipeline().addLast(channelInboundHandler);
                                                     }
                                                 })
                                                 .channel(NetworkUtils.socketChannel());
            this.channel = bootstrap.connect(new InetSocketAddress(connectableAddress.getHostName(), connectableAddress.getPort()),
                                             new InetSocketAddress(localAddress.getHostName(), localAddress.getPort()))
                                    .sync()
                                    .channel()
                                    .writeAndFlush(new PacketOutAuth(auth))
                                    .syncUninterruptibly()
                                    .channel();

            return true;
        } catch (Exception ex) {
            connectionTries++;
            System.out.printf("Failed to connect... [%d]%n", connectionTries);
            ex.printStackTrace();

            if (this.channel != null) {
                this.channel.close();
                this.channel = null;
            }

            if (cancelTask != null) {
                cancelTask.run();
            }

            return false;
        }
    }
}
