/*
 * Copyright (c) Tarek Hosni El Alaoui 2017
 */

package de.dytanic.cloudnetcore.api.event.network;

import de.dytanic.cloudnet.event.Event;
import de.dytanic.cloudnetcore.network.components.Wrapper;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Call if a wrapper is connected
 */
@Getter
@AllArgsConstructor
public class WrapperChannelInitEvent extends Event {

    private Wrapper wrapper;

    private Channel channel;

}