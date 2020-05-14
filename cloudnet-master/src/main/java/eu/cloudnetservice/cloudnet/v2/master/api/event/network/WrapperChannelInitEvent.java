package eu.cloudnetservice.cloudnet.v2.master.api.event.network;

import eu.cloudnetservice.cloudnet.v2.event.Event;
import eu.cloudnetservice.cloudnet.v2.master.network.components.Wrapper;
import io.netty.channel.Channel;

/**
 * Call if a wrapper is connected
 */
public class WrapperChannelInitEvent extends Event {

    private final Wrapper wrapper;

    private final Channel channel;

    public WrapperChannelInitEvent(Wrapper wrapper, Channel channel) {
        this.wrapper = wrapper;
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }
}