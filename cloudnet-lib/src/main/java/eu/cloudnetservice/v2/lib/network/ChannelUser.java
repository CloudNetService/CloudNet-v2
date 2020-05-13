package eu.cloudnetservice.v2.lib.network;

import io.netty.channel.Channel;

/**
 * Created by Tareko on 22.07.2017.
 */
public interface ChannelUser {

    Channel getChannel();

    void setChannel(Channel channel);

}
