package eu.cloudnetservice.cloudnet.v2.master.api.event.network;

import eu.cloudnetservice.cloudnet.v2.event.Event;
import eu.cloudnetservice.cloudnet.v2.master.network.components.INetworkComponent;
import io.netty.channel.Channel;

/**
 * Calls if a channel of some INetworkComponent is connected
 */
public class ChannelInitEvent extends Event {

    private final Channel channel;

    private final INetworkComponent iNetworkComponent;

    public ChannelInitEvent(Channel channel, INetworkComponent iNetworkComponent) {
        this.channel = channel;
        this.iNetworkComponent = iNetworkComponent;
    }

    public Channel getChannel() {
        return channel;
    }

    public INetworkComponent getINetworkComponent() {
        return iNetworkComponent;
    }
}
