/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.network;

import de.dytanic.cloudnet.event.Cancelable;
import de.dytanic.cloudnet.event.Event;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Calls if a channel is connected
 */
@Getter
@AllArgsConstructor
public class ChannelConnectEvent extends Event implements Cancelable {

    private boolean cancelled;

    private Channel channel;

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}