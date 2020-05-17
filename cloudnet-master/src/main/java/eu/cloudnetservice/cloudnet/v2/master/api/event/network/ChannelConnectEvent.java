package eu.cloudnetservice.cloudnet.v2.master.api.event.network;

import eu.cloudnetservice.cloudnet.v2.event.Cancelable;
import eu.cloudnetservice.cloudnet.v2.event.Event;
import io.netty.channel.Channel;

/**
 * Calls if a channel is connected
 */
public class ChannelConnectEvent extends Event implements Cancelable {

    private boolean cancelled;

    private final Channel channel;

    public ChannelConnectEvent(boolean cancelled, Channel channel) {
        this.cancelled = cancelled;
        this.channel = channel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public Channel getChannel() {
        return channel;
    }
}
