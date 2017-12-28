package de.dytanic.cloudnet.lib.network;

import io.netty.channel.Channel;

/**
 * Created by Tareko on 22.07.2017.
 */
public interface ChannelUser {

    void setChannel(Channel channel);

    Channel getChannel();

}