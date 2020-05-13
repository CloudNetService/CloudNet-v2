package eu.cloudnetservice.v2.master.network.components;

import eu.cloudnetservice.v2.lib.server.ServerGroup;
import eu.cloudnetservice.v2.lib.server.ServerGroupMode;
import eu.cloudnetservice.v2.lib.server.ServerProcessMeta;
import eu.cloudnetservice.v2.lib.server.info.ServerInfo;
import eu.cloudnetservice.v2.lib.service.ServiceId;
import eu.cloudnetservice.v2.lib.utility.document.Document;
import eu.cloudnetservice.v2.master.CloudNet;
import eu.cloudnetservice.v2.master.network.packet.out.PacketOutCustomSubChannelMessage;
import io.netty.channel.Channel;

/**
 * Created by Tareko on 26.05.2017.
 */
public final class MinecraftServer implements INetworkComponent {

    private final ServiceId serviceId;
    private final ServerProcessMeta processMeta;
    private final Wrapper wrapper;
    private final ServerGroupMode groupMode;

    private long channelLostTime = 0L;

    private ServerInfo serverInfo;
    private ServerInfo lastServerInfo;
    private Channel channel;

    public MinecraftServer(ServerProcessMeta processMeta, Wrapper wrapper, ServerGroup group, ServerInfo serverInfo) {
        this.processMeta = processMeta;
        this.serviceId = serverInfo.getServiceId();
        this.wrapper = wrapper;
        this.groupMode = group.getGroupMode();

        this.serverInfo = serverInfo;
        this.lastServerInfo = serverInfo;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    public long getChannelLostTime() {
        return channelLostTime;
    }

    public void setChannelLostTime(long channelLostTime) {
        this.channelLostTime = channelLostTime;
    }

    public ServerInfo getLastServerInfo() {
        return lastServerInfo;
    }

    public void setLastServerInfo(ServerInfo lastServerInfo) {
        this.lastServerInfo = lastServerInfo;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public ServerGroupMode getGroupMode() {
        return groupMode;
    }

    public ServerProcessMeta getProcessMeta() {
        return processMeta;
    }

    public void disconnect() {
        if (this.channel != null) {
            this.channel.close().syncUninterruptibly();
        }
    }

    public void sendCustomMessage(String channel, String message, Document value) {
        this.sendPacket(new PacketOutCustomSubChannelMessage(channel, message, value));
    }

    public ServerGroup getGroup() {
        return CloudNet.getInstance().getServerGroup(serviceId.getGroup());
    }

    @Override
    public Wrapper getWrapper() {
        return wrapper;
    }

    @Override
    public String getServerId() {
        return serviceId.getServerId();
    }

    @Override
    public String getName() {
        return serviceId.getServerId();
    }
}
