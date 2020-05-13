package eu.cloudnetservice.v2.master.api.event.network;

import de.dytanic.cloudnet.event.Event;
import eu.cloudnetservice.v2.master.network.components.INetworkComponent;
import io.netty.channel.Channel;

/**
 * Calls if a channel of some INetworkComponent is connected
 */
public class ChannelInitEvent extends Event {

    private Channel channel;

    private INetworkComponent iNetworkComponent;

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