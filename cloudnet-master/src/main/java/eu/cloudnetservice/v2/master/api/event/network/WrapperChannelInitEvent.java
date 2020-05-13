package eu.cloudnetservice.v2.master.api.event.network;

import de.dytanic.cloudnet.event.Event;
import eu.cloudnetservice.v2.master.network.components.Wrapper;
import io.netty.channel.Channel;

/**
 * Call if a wrapper is connected
 */
public class WrapperChannelInitEvent extends Event {

    private Wrapper wrapper;

    private Channel channel;

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